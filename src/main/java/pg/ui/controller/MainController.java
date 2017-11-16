package pg.ui.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.ui.handler.WindowHandler;
import pg.ui.task.AvailableOperationTask;
import pg.ui.task.DeleteTask;
import pg.ui.task.FindTask;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.response.detail.DSTask;

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

    private Map<String, String> existingImdbMap;
    private Future<?> futureTask;
    private List<DSTask> torrentsToDelete;

    private ExecutorService executor;
    private FindTask findTask;
    private AvailableOperationTask availableOperationTask;
    private DeleteTask deleteTask;

    public MainController(WindowHandler windowHandler) {
        super(windowHandler);
        executor = Executors.newFixedThreadPool(3);
        availableOperationTask = new AvailableOperationTask(executor);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        getAvailableOperation();
        setupMenuItems();
        initializeImdbComboBox();
        setupButtons();
        setupListView();
        progressIndicator.setVisible(false);
        //mainTask.messageProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }

    private void getAvailableOperation() {
        executor.submit(availableOperationTask);
        windowHandler.setAvailableOperationTask(availableOperationTask);
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
                findTask = new FindTask(torrentListView, availableOperationTask.getDsApiDetail(), executor);
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
                    resetProperties(findTask);
                    String imdbId = existingImdbMap.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue().equals(imdbComboBox.getValue()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse("");
                    findTask.setImdbId(imdbId);
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
        //torrentListView.setOnKeyTyped(listViewKeyReleasedEventHandler());
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
                deleteTask = new DeleteTask(torrentListView, findTask.getLoginSid(),
                        availableOperationTask.getDsApiDetail().getDownloadStationTask(), torrentsToDelete, executor);
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
            torrentsToDelete = torrentListView.getSelectionModel().getSelectedItems();
        };
    }
}
