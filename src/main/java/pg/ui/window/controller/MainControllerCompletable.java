package pg.ui.window.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TaskDetail;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.completable.*;
import pg.ui.window.controller.handler.AutoCompleteComboBoxHandler;
import pg.ui.window.controller.setup.ActionButtonSetup;
import pg.ui.window.controller.setup.ComponentSetup;
import pg.ui.window.controller.task.AvailableOperationTask;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/** Created by Gawa 2018-01-03 */
public class MainControllerCompletable extends AbstractController {

    @FXML private MenuItem applicationMenuItem;
    @FXML private MenuItem showsMenuItem;
    @FXML private CheckBox chooseCheckBox;
    @FXML private Button allButton;
    @FXML private Button imdbButton;
    @FXML private Button useLinkButton;
    @FXML private Button deleteButton;
    @FXML private Button addButton;
    @FXML private Button cleanButton;
    @FXML private Button resumeButton;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private Button forceDeleteButton;
    @FXML private ComboBox<String> imdbComboBox;
    @FXML private Label infoLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Pane connectionPane;
    @FXML private Pane imdbPane;
    @FXML private Pane favouritePane;
    @FXML private Label numberOfShowsLabel;
    @FXML private TableView<TaskDetail> taskTableView;

    private Map<String, String> existingImdbMap;
    private Future<?> futureTask;
    private List<TaskDetail> torrentsToDelete;

    private ExecutorService executor;
    private FindTaskCompletable findTask;
    private AvailableOperationTask availableOperationTask;
    private DeleteTaskCompletable deleteTask;
    private ListTaskCompletable listTask;
    private UseLinkTaskCompletable useLinkTask;
    private ApplicationPropertiesHelper application;

    public MainControllerCompletable(WindowHandler windowHandler) {
        super(windowHandler);
        executor = Executors.newFixedThreadPool(3);
        application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setUpTaskTableView();
        setupConnectingPane();
        setupMenuItems();
        initializeImdbComboBox();
        setupButtons();
        getAvailableOperation();
        progressIndicator.setVisible(false);
    }

    @SuppressWarnings("unchecked")
    private void setUpTaskTableView() {
        TableColumn<TaskDetail, ?> column = taskTableView.getColumns().get(0);
        TableColumn<TaskDetail, String> titleColumn = new TableColumn<>();
        titleColumn.setResizable(false);
        titleColumn.setText(column.getText());
        titleColumn.setPrefWidth(column.getPrefWidth());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        column = taskTableView.getColumns().get(1);
        TableColumn<TaskDetail, String> statusColumn = new TableColumn<>();
        statusColumn.setResizable(false);
        statusColumn.setText(column.getText());
        statusColumn.setPrefWidth(column.getPrefWidth());
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        column = taskTableView.getColumns().get(2);
        TableColumn<TaskDetail, Double> progressColumn = new TableColumn<>();
        progressColumn.setResizable(false);
        progressColumn.setText(column.getText());
        progressColumn.setPrefWidth(column.getPrefWidth());
        progressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressColumn.setCellFactory(TextFieldTableCell.forTableColumn(doubleStringConverter()));

        taskTableView.getColumns().clear();
        taskTableView.getColumns().addAll(titleColumn, statusColumn, progressColumn);

        taskTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        taskTableView.getSelectionModel().selectedItemProperty().addListener(toDeleteChangeListener());
        taskTableView.setOnKeyReleased(keyReleasedEventHandler());
        taskTableView.requestFocus();
    }

    private StringConverter<Double> doubleStringConverter() {
        return new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.toString();
            }
            @Override
            public Double fromString(String string) {
                return Double.valueOf(string);
            }
        };
    }

    private void setupConnectingPane() {
        int width = 4;
        int height = 4;
        BackgroundSize backgroundSize = new BackgroundSize(width, height, true, true, false, false);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(AppConstants.CONNECTING_GIF);
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(inputStream),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        Background background = new Background(backgroundImage);
        connectionPane.setBackground(background);
    }

    private void getAvailableOperation() {
        availableOperationTask = new AvailableOperationTask(executor, connectionPane);
        availableOperationTask.setFavouritePane(favouritePane);
        availableOperationTask.setImdbPane(imdbPane);
        resetProperties(availableOperationTask);
        futureTask = executor.submit(availableOperationTask);
    }

    private void setupMenuItems() {
        applicationMenuItem.setOnAction(applicationMenuItemAction());
        showsMenuItem.setOnAction(showMenuItemAction());
    }

    private EventHandler<ActionEvent> applicationMenuItemAction() {
        return e -> windowHandler.launchConfigWindow();
    }

    private EventHandler<ActionEvent> showMenuItemAction() {
        return e -> windowHandler.launchShowWindow();
    }

    @SuppressWarnings("unchecked")
    private void initializeImdbComboBox() {
        Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
        existingImdbMap = JsonUtils.convertFromFile(filePath, TreeMap.class)
                .orElse(new TreeMap<String, String>());
        List<String> sortedImdbs = existingImdbMap.keySet()
                .stream()
                .map(existingImdbMap::get)
                .sorted()
                .collect(Collectors.toList());
        imdbComboBox.setItems(FXCollections.observableList(sortedImdbs));
        numberOfShowsLabel.setText(String.valueOf(existingImdbMap.size()));
        new AutoCompleteComboBoxHandler<>(imdbComboBox, numberOfShowsLabel);
    }

    private void setupButtons() {
        ComponentSetup setup = new ActionButtonSetup(Arrays.asList(
                deleteButton, addButton, cleanButton, resumeButton, pauseButton, stopButton, forceDeleteButton
        ));
        setup.setup();
        allButton.setOnAction(allButtonAction());
        imdbButton.setOnAction(imdbButtonAction());
        useLinkButton.setOnAction(useLinkButtonAction());
    }

    private EventHandler<ActionEvent> allButtonAction() {
        return e -> {
            try {
                cancelTask();
                findTask = new FindTaskCompletable(
                        taskTableView,
                        availableOperationTask.getDsApiDetail(),
                        windowHandler,
                        executor
                );
                findTask.setSid(extractSid());
                findTask.setNumberOfShowsLabel(numberOfShowsLabel);
                resetProperties(findTask);
                futureTask = executor.submit(findTask);
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.FAVOURITES, ex));
                }
            }
        };
    }

    private void resetProperties(Task task) {
        progressIndicator.setVisible(true);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(task.progressProperty());
        infoLabel.textProperty().unbind();
        infoLabel.textProperty().bind(task.messageProperty());
    }

    private void cancelTask() {
        if (futureTask != null && !futureTask.isDone()) {
            logger.info("Cancelling the task.");
            futureTask.cancel(true);
        }
    }

    private EventHandler<ActionEvent> imdbButtonAction() {
        return e -> {
            try {
                cancelTask();
                if (StringUtils.nullOrTrimEmpty(imdbComboBox.getValue())) {
                    infoLabel.setText("Please choose imdb id.");
                } else {
                    String imdbId = existingImdbMap.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(imdbComboBox.getValue()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse("");
                    findTask = new FindTaskCompletable(
                            taskTableView,
                            availableOperationTask.getDsApiDetail(),
                            windowHandler,
                            executor
                    );
                    findTask.setImdbId(imdbId);
                    findTask.setSid(extractSid());
                    resetProperties(findTask);
                    futureTask = executor.submit(findTask);
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.IMDB, ex));
                }
            }
        };
    }

    private EventHandler<ActionEvent> useLinkButtonAction() {
        return e -> {
            String userName = application.getUsername();
            String password = application.getPassword();
            if (StringUtils.nullOrTrimEmpty(userName)) {
                windowHandler.handleException(new ProgramException(UIError.USERNAME_DS));
                return;
            }
            if (StringUtils.nullOrTrimEmpty(password)) {
                windowHandler.handleException(new ProgramException(UIError.PASSWORD_DS));
                return;
            }

            TextInputDialog dialog = new TextInputDialog(AppConstants.EMPTY_STRING);
            dialog.setTitle("Link dialog");
            String msg = "Paste the link.\n" +
                    "You may use: (http://, http://, ftp://, ftps://, sftp://, magnet:, thunder://, flashget://, " +
                    "qqdl://)";
            dialog.setHeaderText(msg);
            dialog.setContentText(AppConstants.EMPTY_STRING);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(link -> {
                try {
                    useLinkTask = new UseLinkTaskCompletable(
                            taskTableView,
                            availableOperationTask.getDsApiDetail(),
                            windowHandler,
                            link,
                            executor
                    );
                    useLinkTask.setSid(extractSid());
                    resetProperties(useLinkTask);
                    futureTask = executor.submit(useLinkTask);
                } catch (Exception ex) {
                    logger.error(ex.getLocalizedMessage());
                    windowHandler.handleException((ProgramException) ex);
                }
            });
        };
    }

    private EventHandler<KeyEvent> keyReleasedEventHandler() {
        return event -> {
            try {
                String sid = extractSid();
                if (KeyCode.L == event.getCode()) {
                    listTask = new ListTaskCompletable(
                            taskTableView,
                            availableOperationTask.getDsApiDetail(),
                            windowHandler,
                            executor
                    );
                    listTask.setSid(sid);
                    resetProperties(listTask);
                    futureTask = executor.submit(listTask);
                }
                if (torrentsToDelete == null || torrentsToDelete.isEmpty()) {
                    return;
                }
                if (EnumSet.of(KeyCode.DELETE, KeyCode.BACK_SPACE, KeyCode.C).contains(event.getCode())) {
                    deleteTask = new DeleteTaskCompletable(
                            taskTableView,
                            sid,
                            availableOperationTask.getDsApiDetail().getDownloadStationTask(),
                            torrentsToDelete,
                            executor
                    );
                    resetProperties(deleteTask);
                    futureTask = executor.submit(deleteTask);
                } else if (KeyCode.F == event.getCode()) {
                    deleteTask = new DeleteForceCompleteTaskCompletable(
                            taskTableView,
                            sid,
                            availableOperationTask.getDsApiDetail().getDownloadStationTask(),
                            torrentsToDelete,
                            executor
                    );
                    resetProperties(deleteTask);
                    futureTask = executor.submit(deleteTask);
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.SHORTCUT, ex));
                }
            }
        };
    }

    private String extractSid() {
        String sid = null;
        if (findTask != null) {
            sid = findTask.getLoginSid();
        }
        if (sid != null) {
            return sid;
        }
        if (listTask != null) {
            sid = listTask.getLoginSid();
        }
        return sid;
    }

    private ChangeListener<TaskDetail> toDeleteChangeListener() {
        return (observable, oldValue, newValue) -> {
            torrentsToDelete = new ArrayList<>();
            torrentsToDelete.addAll(taskTableView.getSelectionModel().getSelectedItems());
        };
    }
}
