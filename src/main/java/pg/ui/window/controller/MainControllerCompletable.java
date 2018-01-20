package pg.ui.window.controller;

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
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.props.ApplicationPropertiesHelper;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.completable.DeleteForceCompleteTaskCompletable;
import pg.ui.window.controller.completable.DeleteTaskCompletable;
import pg.ui.window.controller.completable.FindTaskCompletable;
import pg.ui.window.controller.completable.ListTaskCompletable;
import pg.ui.window.controller.task.AvailableOperationTask;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.ds.detail.DSTask;

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
    @FXML private ComboBox<String> imdbComboBox;
    @FXML private ListView<DSTask> torrentListView;
    @FXML private Label infoLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Pane connectionPane;
    @FXML private Pane imdbPane;
    @FXML private Pane favouritePane;
    @FXML private Label numberOfShowsLabel;

    private Map<String, String> existingImdbMap;
    private Future<?> futureTask;
    private List<DSTask> torrentsToDelete;

    private ExecutorService executor;
    private FindTaskCompletable findTask;
    private AvailableOperationTask availableOperationTask;
    private DeleteTaskCompletable deleteTask;
    private ListTaskCompletable listTask;
    private ApplicationPropertiesHelper application;

    public MainControllerCompletable(WindowHandler windowHandler) {
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
        numberOfShowsLabel.setText(String.format("%s %d", numberOfShowsLabel.getText(), existingImdbMap.size()));
    }

    private void setupButtons() {
        allButton.setOnAction(allButtonAction());
        imdbButton.setOnAction(imdbButtonAction());
    }

    private EventHandler<ActionEvent> allButtonAction() {
        return e -> {
            try {
                cancelTask();
                findTask = new FindTaskCompletable(
                        torrentListView,
                        availableOperationTask.getDsApiDetail(),
                        windowHandler,
                        executor
                );
                findTask.setSid(extractSid());
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
                    //infoLabel.textProperty().unbind();
                    infoLabel.setText("Please choose imdb id.");
                } else {
                    String imdbId = existingImdbMap.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(imdbComboBox.getValue()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse("");
                    findTask = new FindTaskCompletable(
                            torrentListView,
                            availableOperationTask.getDsApiDetail(),
                            windowHandler, executor
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

    private void setupListView() {
        //torrentListView.setOnMouseClicked(listViewDoubleClickEvent());
        torrentListView.setOnKeyReleased(listViewKeyReleasedEventHandler());
        torrentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        torrentListView.getSelectionModel().selectedItemProperty().addListener(listViewChangeListener());
        torrentListView.requestFocus();
    }

    private EventHandler<KeyEvent> listViewKeyReleasedEventHandler() {
        return event -> {
            try {
                String sid = extractSid();
                if (KeyCode.L == event.getCode()) {
                    listTask = new ListTaskCompletable(
                            torrentListView,
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
                            torrentListView,
                            sid,
                            availableOperationTask.getDsApiDetail().getDownloadStationTask(),
                            torrentsToDelete,
                            executor
                    );
                    resetProperties(deleteTask);
                    futureTask = executor.submit(deleteTask);
                } else if (KeyCode.F == event.getCode()) {
                    deleteTask = new DeleteForceCompleteTaskCompletable(
                            torrentListView,
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
