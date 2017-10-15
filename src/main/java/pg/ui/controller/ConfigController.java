package pg.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import pg.props.ApplicationPropertiesLoader;
import pg.ui.handler.WindowHandler;
import pg.util.StringUtils;
import pg.web.model.AllowedPorts;
import pg.web.model.DSMethod;
import pg.web.model.TorrentUrlType;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**Created by Gawa 2017-10-04*/
public class ConfigController extends AbstractController {

    @FXML private TextField serverUrl;
    @FXML private ComboBox<String> serverPort;
    @FXML private TextField serverLogin;
    @FXML private TextField serverPassword;
    @FXML private TextField downloadTo;
    @FXML private TextArea apiInfo;
    @FXML private TextField apiUrl;
    @FXML private TextField queryLimit;
    @FXML private TextField queryPage;
    @FXML private TextField torrentAge;
    @FXML private TextField maxFileSize;
    @FXML private TextField releaseDate;
    @FXML private CheckBox repeatDownload;
    @FXML private Button torrentLocationChooser;
    @FXML private Button resultLocationChooser;
    @FXML private Text torrentLocationText;
    @FXML private Text resultLocationText;
    @FXML private ComboBox<DSMethod> creationMethod;
    @FXML private ComboBox<TorrentUrlType> torrentUrlType;
    @FXML private Button doneButton;

    private final ApplicationPropertiesLoader loader;

    public ConfigController(WindowHandler windowHandler) {
        super(windowHandler);
        loader = ApplicationPropertiesLoader.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeFromAppProperties();
        setupComoBoxes();
        setupButtons();
    }

    private void initializeFromAppProperties() {
        serverUrl.setText(loader.getServerUrl());
        final Integer port = loader.getServerPort(AllowedPorts.HTTPS.port());
        if (port == AllowedPorts.HTTPS.port()) {
            serverPort.setValue(String.format("%d - %s", port, AllowedPorts.HTTPS.name()));
        } else {
            serverPort.setValue(String.format("%d - %s", port, AllowedPorts.HTTP.name()));
        }
        serverLogin.setText(loader.getUsername());
        serverPassword.setText(loader.getPassword());
        downloadTo.setText(loader.getDestination());
        apiInfo.setText(loader.getApiInfo());

        apiUrl.setText(loader.getUrl(""));
        queryLimit.setText(String.valueOf(loader.getLimit(0)));
        queryPage.setText(String.valueOf(loader.getPage(0)));

        torrentAge.setText(String.valueOf(loader.getTorrentAge(0)));
        maxFileSize.setText(loader.getMaxFileSize(""));
        releaseDate.setText(loader.getTorrentReleaseDate());
        repeatDownload.setSelected(StringUtils.booleanFromString(loader.getRepeatDownload("")));

        torrentLocationText.setText(loader.getTorrentLocation(""));
        resultLocationText.setText(loader.getFilePath(""));

        creationMethod.setValue(DSMethod.valueOf(loader.getCreationMethod(DSMethod.REST.name())));
        torrentUrlType.setValue(TorrentUrlType.valueOf(loader.getTorrentUrlType(TorrentUrlType.torrent.name())));
    }

    private void setupComoBoxes() {
        serverPort.setItems(
                FXCollections.observableList(
                        Arrays.stream(AllowedPorts.values())
                                .map(port -> String.format("%d - %s", port.port(), port.name()))
                                .collect(Collectors.toList())
                )
        );
        creationMethod.setItems(FXCollections.observableList(Arrays.asList(DSMethod.values())));
        torrentUrlType.setItems(FXCollections.observableList(Arrays.asList(TorrentUrlType.values())));
    }

    private void setupButtons() {
        setupButton(torrentLocationChooser, torrentLocationAction());
        setupButton(resultLocationChooser, resultLocationAction());
    }

    private void setupButton(Button button, EventHandler<ActionEvent> actionEventHandler) {
        button.setText(".");
        button.setOnAction(actionEventHandler);
    }

    private EventHandler<ActionEvent> torrentLocationAction() {
        return e -> torrentLocationText.setText(openDirectoryChooser("Choose torrent location"));
    }

    private String openDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = directoryChooser.showDialog(windowHandler.currentWindow());
        return file.getAbsolutePath();
    }

    private EventHandler<ActionEvent> resultLocationAction() {
        return e -> resultLocationText.setText(openDirectoryChooser("Choose result location"));
    }

    private EventHandler<ActionEvent> doneAction() {
        return e -> {
            //TODO: save application properties
        };
    }
}
