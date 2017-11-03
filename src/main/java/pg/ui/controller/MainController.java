package pg.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pg.ui.handler.WindowHandler;
import pg.ui.task.MainTask;
import pg.util.AppConstants;
import pg.util.JsonUtils;
import pg.util.StringUtils;
import pg.web.model.torrent.ReducedDetail;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
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
    @FXML private ListView<ReducedDetail> torrentListView;
    @FXML private Label infoLabel;
    @FXML private ProgressIndicator progressIndicator;

    private Map<String, String> existingImdbMap;
    private Future<?> futureTask;
    private MainTask mainTask;

    public MainController(WindowHandler windowHandler) {
        super(windowHandler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setupMenuItems();
        initializeImdbComboBox();
        setupButtons();
        progressIndicator.setVisible(false);
        mainTask = new MainTask(torrentListView);
        //mainTask.messageProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
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
            resetProperties();
            cancelTask();
            futureTask = Executors.newSingleThreadExecutor().submit(mainTask);
        };
    }

    private void resetProperties() {
        progressIndicator.setVisible(true);
        progressIndicator.progressProperty().unbind();
        progressIndicator.progressProperty().bind(mainTask.progressProperty());
        infoLabel.textProperty().unbind();
        infoLabel.textProperty().bind(mainTask.messageProperty());
    }

    private void cancelTask() {
        if (futureTask != null && !futureTask.isDone()) {
            logger.info("Cancelling the task.");
            futureTask.cancel(true);
        }
    }

    private EventHandler<ActionEvent> imdbButtonAction() {
        return e -> {
            if (StringUtils.nullOrTrimEmpty(imdbComboBox.getValue())) {
                cancelTask();
                infoLabel.setText("Please choose imdb id.");
            } else {
                cancelTask();
                resetProperties();
                String imdbId = existingImdbMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().equals(imdbComboBox.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("");
                mainTask.setImdbId(imdbId);
                futureTask = Executors.newSingleThreadExecutor().submit(mainTask);
            }
        };
    }
}
