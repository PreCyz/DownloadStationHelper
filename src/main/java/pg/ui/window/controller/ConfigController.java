package pg.ui.window.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.program.TorrentUrlType;
import pg.props.ApplicationPropertiesHelper;
import pg.props.ConfigBuilder;
import pg.ui.window.WindowHandler;
import pg.util.StringUtils;
import pg.web.ds.DSAllowedProtocol;
import pg.web.ds.detail.DSMethod;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**Created by Gawa 2017-10-04*/
public class ConfigController extends AbstractController {

    @FXML private TextField serverUrl;
    @FXML private ComboBox<String> serverPort;
    @FXML private TextField serverLogin;
    @FXML private PasswordField serverPassword;
    @FXML private TextField downloadTo;
    @FXML private TextArea apiInfo;
    @FXML private TextField apiUrl;
    @FXML private TextField queryLimit;
    @FXML private TextField queryPage;
    @FXML private TextField torrentAge;
    @FXML private TextField maxFileSize;
    @FXML private TextField releaseDate;
    @FXML private CheckBox repeatDownload;
    @FXML private CheckBox handleDuplicates;
    @FXML private Button torrentLocationChooser;
    @FXML private Button resultLocationChooser;
    @FXML private Text torrentLocationText;
    @FXML private Text resultLocationText;
    @FXML private ComboBox<DSMethod> creationMethod;
    @FXML private ComboBox<TorrentUrlType> torrentUrlType;
    @FXML private Button doneButton;
    @FXML private TextField liveTrackInterval;

    private final ApplicationPropertiesHelper appHelper;

    public ConfigController(WindowHandler windowHandler) {
        super(windowHandler);
        appHelper = ApplicationPropertiesHelper.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        initializeFromAppProperties();
        setupComoBoxes();
        setupButtons();
    }

    private void initializeFromAppProperties() {
        final DSAllowedProtocol protocol = appHelper.getServerPort(DSAllowedProtocol.https);
        serverUrl.setText(appHelper.getServerUrl());
        serverPort.setValue(String.format("%d - %s", protocol.port(), protocol.name()));
        serverLogin.setText(appHelper.getUsername());
        serverPassword.setText(appHelper.getPassword());
        downloadTo.setText(appHelper.getDestination());
        apiInfo.setText(appHelper.getApiInfo());
        apiUrl.setText(appHelper.getUrl(""));
        queryLimit.setText(String.valueOf(appHelper.getLimit(100)));
        queryPage.setText(String.valueOf(appHelper.getPage(1)));
        torrentAge.setText(String.valueOf(appHelper.getTorrentAge(0)));
        maxFileSize.setText(appHelper.getMaxFileSize(""));
        releaseDate.setText(appHelper.getTorrentReleaseDate());
        repeatDownload.setSelected(StringUtils.booleanFromString(appHelper.getRepeatDownload("")));
        handleDuplicates.setSelected(StringUtils.booleanFromString(appHelper.getHandleDuplicates("")));
        torrentLocationText.setText(appHelper.getTorrentLocation(""));
        resultLocationText.setText(appHelper.getFilePath(""));
        creationMethod.setValue(DSMethod.valueOf(appHelper.getCreationMethod(DSMethod.REST.name())));
        torrentUrlType.setValue(TorrentUrlType.valueOf(appHelper.getTorrentUrlType(TorrentUrlType.torrent.name())));
        liveTrackInterval.setText(String.valueOf(appHelper.getLiveTrackInterval()));
    }

    private void setupComoBoxes() {
        serverPort.setItems(
                FXCollections.observableList(
                        Arrays.stream(DSAllowedProtocol.values())
                                .map(port -> String.format("%d - %s", port.port(), port.name()))
                                .collect(Collectors.toList())
                )
        );
        creationMethod.setItems(FXCollections.observableList(Arrays.asList(DSMethod.values())));
        torrentUrlType.setItems(FXCollections.observableList(Arrays.asList(TorrentUrlType.values())));
    }

    private void setupButtons() {
        torrentLocationChooser.setText(".");
        torrentLocationChooser.setOnAction(torrentLocationAction());

        resultLocationChooser.setText(".");
        resultLocationChooser.setOnAction(resultLocationAction());

        doneButton.setOnAction(doneAction());
    }

    private EventHandler<ActionEvent> torrentLocationAction() {
        return e -> {
            Optional<String> torrentLocation = openDirectoryChooser("Choose torrent location");
            torrentLocation.ifPresent(location -> torrentLocationText.setText(location));
        };
    }

    private Optional<String> openDirectoryChooser(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = directoryChooser.showDialog(windowHandler.currentWindow());
        if (file == null) {
            return Optional.empty();
        }
        return Optional.of(file.getAbsolutePath());
    }

    private EventHandler<ActionEvent> resultLocationAction() {
        return e -> {
            Optional<String> resultLocation = openDirectoryChooser("Choose result location");
            resultLocation.ifPresent(location -> resultLocationText.setText(location));
        };
    }

    private EventHandler<ActionEvent> doneAction() {
        return e -> {
            ConfigBuilder configBuilder = new ConfigBuilder()
                    .withServerUrl(serverUrl.getText())
                    .withServerPort(serverPort.getValue())
                    .withLogin(serverLogin.getText())
                    .withPassword(serverPassword.getText())
                    .withDownloadTo(downloadTo.getText())
                    .withApiInfo(apiInfo.getText())
                    .withApiUrl(apiUrl.getText())
                    .withQueryLimit(queryLimit.getText())
                    .withQueryPage(queryPage.getText())
                    .withTorrentAge(torrentAge.getText())
                    .withMaxFileSize(maxFileSize.getText())
                    .withReleaseDate(releaseDate.getText())
                    .withRepeatDownload(repeatDownload.isSelected())
                    .withHandleDuplicates(handleDuplicates.isSelected())
                    .withTorrentLocation(torrentLocationText.getText())
                    .withResultLocation(resultLocationText.getText())
                    .withCreationMethod(creationMethod.getValue())
                    .withTorrentUrlType(torrentUrlType.getValue())
                    .withLiveTrackInterval(liveTrackInterval.getText());

            try {
                appHelper.store(configBuilder);
            } catch (IOException ex) {
                throw new ProgramException(UIError.SAVE_PROPERTIES, ex);
            }
            windowHandler.currentWindow().hide();
        };
    }
}
