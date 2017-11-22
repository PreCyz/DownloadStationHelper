package pg.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.ui.handler.WindowHandler;
import pg.ui.task.AvailableOperationTask;
import pg.ui.task.CleanTask;
import pg.ui.task.DeleteTask;
import pg.ui.task.FindTask;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.response.detail.DSTask;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/** Created by Gawa 2017-10-04 */
public class MainController extends AbstractController {

    @FXML private MenuItem applicationMenuItem;
    @FXML private MenuItem showsMenuItem;
    @FXML private CheckBox chooseCheckBox;
    @FXML private Button allButton;
    @FXML private Button imdbButton;
    @FXML private ComboBox<String> imdbComboBox;
    @FXML private ListView<DSTask> torrentListView;
    @FXML private Label infoLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Pane connectionPane;

    private Map<String, String> existingImdbMap;
    private Future<?> futureTask;
    private List<DSTask> torrentsToDelete;

    private ExecutorService executor;
    private FindTask findTask;
    private AvailableOperationTask availableOperationTask;
    private DeleteTask deleteTask;
    private ApplicationPropertiesHelper application;

    public MainController(WindowHandler windowHandler) {
        super(windowHandler);
        executor = Executors.newFixedThreadPool(3);
        application = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupConnectingPane();
        setupMenuItems();
        initializeImdbComboBox();
        setupButtons();
        setupListView();
        getAvailableOperation();
        progressIndicator.setVisible(false);
    }

    private void setupConnectingPane() {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(AppConstants.CONNECTING_GIF);
        Image image = new Image(resourceAsStream);
        Background background = new Background(
                new BackgroundImage(
                        image,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        BackgroundSize.DEFAULT
                )
        );
        Tooltip tooltip = new Tooltip();
        tooltip.setText(String.format("Connecting to %s ...", application.getServerUrl()));
        connectionPane.setBackground(background);
    }

    private void getAvailableOperation() {
        availableOperationTask = new AvailableOperationTask(executor, connectionPane);
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

    public void initializeImdbComboBox() {
        Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
        existingImdbMap = JsonUtils.convertFromFile(filePath, TreeMap.class)
                .orElse(new TreeMap<String, String>());
        List<String> sortedImdbs = existingImdbMap.keySet()
                .stream()
                .map(existingImdbMap::get)
                .sorted()
                .collect(Collectors.toList());
        imdbComboBox.setItems(FXCollections.observableList(sortedImdbs));
    }

    private void setupButtons() {
        allButton.setOnAction(allButtonAction());
        imdbButton.setOnAction(imdbButtonAction());
    }

    private EventHandler<ActionEvent> allButtonAction() {
        return e -> {
            try {
                cancelTask();
                findTask = new FindTask(
                        torrentListView,
                        availableOperationTask.getDsApiDetail(),
                        windowHandler,
                        executor
                );
                resetProperties(findTask);
                futureTask = executor.submit(findTask);
            } catch (Exception ex) {
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.LAUNCH_PROGRAM, ex));
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
                if (StringUtils.nullOrTrimEmpty(imdbComboBox.getValue())) {
                    cancelTask();
                    infoLabel.setText("Please choose imdb id.");
                } else {
                    cancelTask();
                    String imdbId = existingImdbMap.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(imdbComboBox.getValue()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse("");
                    findTask = new FindTask(
                            torrentListView,
                            availableOperationTask.getDsApiDetail(),
                            windowHandler, executor
                    );
                    findTask.setImdbId(imdbId);
                    resetProperties(findTask);
                    futureTask = executor.submit(findTask);
                }
            } catch (Exception ex) {
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.LAUNCH_PROGRAM, ex));
                }
            }
        };
    }

    private void setupListView() {
        //torrentListView.setOnMouseClicked(listViewDoubleClickEvent());
        torrentListView.setOnKeyReleased(listViewKeyReleasedEventHandler());
        torrentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        torrentListView.getSelectionModel().selectedItemProperty().addListener(listViewChangeListener());
    }

    private EventHandler<KeyEvent> listViewKeyReleasedEventHandler() {
        return event -> {
            if (torrentsToDelete.isEmpty()) {
                return;
            }
            if (EnumSet.of(KeyCode.DELETE, KeyCode.BACK_SPACE).contains(event.getCode())) {
                deleteTask = new DeleteTask(
                        torrentListView,
                        findTask.getLoginSid(),
                        availableOperationTask.getDsApiDetail().getDownloadStationTask(),
                        torrentsToDelete,
                        executor
                );
                resetProperties(deleteTask);
                futureTask = executor.submit(deleteTask);
            } else if (KeyCode.C == event.getCode()) {
                deleteTask = new CleanTask(
                        torrentListView,
                        findTask.getLoginSid(),
                        availableOperationTask.getDsApiDetail().getDownloadStationTask(),
                        torrentsToDelete,
                        executor
                );
                resetProperties(deleteTask);
                futureTask = executor.submit(deleteTask);
            }
        };
    }

    private EventHandler<MouseEvent> listViewDoubleClickEvent() {
        return mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                System.out.println("Double clicked");
            }
        };
    }

    private ChangeListener<DSTask> listViewChangeListener() {
        return (observable, oldValue, newValue) -> {
            torrentsToDelete = new ArrayList<>();
            torrentsToDelete.addAll(torrentListView.getSelectionModel().getSelectedItems());
        };
    }
}
