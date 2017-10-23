package pg.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import pg.ui.handler.WindowHandler;
import pg.util.AppConstants;
import pg.util.JsonUtils;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**Created by Gawa 2017-10-04*/
public class MainController extends AbstractController {

    @FXML private MenuItem applicationMenuItem;
    @FXML private MenuItem showsMenuItem;
    @FXML private CheckBox chooseCheckBox;
    @FXML private Button allButton;
    @FXML private Button imdbButton;
    @FXML private ComboBox<Map<String, String>> imdbComboBox;

    public MainController(WindowHandler windowHandler) {
        super(windowHandler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        Path filePath = AppConstants.fullFilePath(AppConstants.IMDB_FILE_NAME);
        Map<String, String> existingImdbMap = JsonUtils.convertFromFile(filePath, TreeMap.class)
                .orElse(new TreeMap<String,String>());
        imdbComboBox.setItems(FXCollections.observableArrayList(existingImdbMap));
    }
}
