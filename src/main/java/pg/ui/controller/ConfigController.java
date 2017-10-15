package pg.ui.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import pg.props.ApplicationPropertiesHelper;
import pg.props.ConfigBuilder;
import pg.ui.exception.ProgramException;
import pg.ui.exception.UIError;
import pg.ui.handler.WindowHandler;
import pg.util.StringUtils;
import pg.web.model.AllowedPorts;
import pg.web.model.DSMethod;
import pg.web.model.TorrentUrlType;

import java.io.File;
import java.io.IOException;
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

    private final ApplicationPropertiesHelper loader;

    public ConfigController(WindowHandler windowHandler) {
        super(windowHandler);
        loader = ApplicationPropertiesHelper.getInstance();
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
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Your Synology device address.");
        serverUrl.setTooltip(tooltip);

        final Integer port = loader.getServerPort(AllowedPorts.HTTPS.port());
        if (port == AllowedPorts.HTTPS.port()) {
            serverPort.setValue(String.format("%d - %s", port, AllowedPorts.HTTPS.name()));
        } else {
            serverPort.setValue(String.format("%d - %s", port, AllowedPorts.HTTP.name()));
        }
        tooltip = new Tooltip();
        final String value = String.format("Port used to communication with your Synology device for http is " +
                "5000 and for https is 5001.%nIf 5000 is given then http protocol is used otherwise https is " +
                "used. If you do not have valid https certificate%nuse port 5000, otherwise program will not be " +
                "able to make request to your disk station.");
        tooltip.setText(value);
        serverPort.setTooltip(tooltip);

        serverLogin.setText(loader.getUsername());
        tooltip = new Tooltip();
        tooltip.setText("Login to your Synology.");
        serverLogin.setTooltip(tooltip);

        serverPassword.setText(loader.getPassword());
        tooltip = new Tooltip();
        tooltip.setText("Password to your Synology.");
        serverPassword.setTooltip(tooltip);

        downloadTo.setText(loader.getDestination());
        tooltip = new Tooltip();
        tooltip.setText("Location where download torrents to, starts from one of the shared folders.");
        downloadTo.setTooltip(tooltip);

        apiInfo.setText(loader.getApiInfo());
        tooltip = new Tooltip();
        tooltip.setText("Rest path to get all allowed operation for disk station.");
        apiInfo.setTooltip(tooltip);

        apiUrl.setText(loader.getUrl(""));
        tooltip = new Tooltip();
        tooltip.setText("Request URL to get torrents details.");
        apiUrl.setTooltip(tooltip);

        queryLimit.setText(String.valueOf(loader.getLimit(100)));
        tooltip = new Tooltip();
        tooltip.setText("Max value is 100 it means that GET request to above address will contain 100 torrents.");
        queryLimit.setTooltip(tooltip);

        queryPage.setText(String.valueOf(loader.getPage(1)));
        tooltip = new Tooltip();
        final String val = String.format("Default value is 1. Defines how many times request is executed.%nGreater " +
                "value means longer program execution time.");
        tooltip.setText(val);
        queryPage.setTooltip(tooltip);

        torrentAge.setText(String.valueOf(loader.getTorrentAge(0)));
        tooltip = new Tooltip();
        tooltip.setText("Age of torrent given in days. If not specified then no filtering by date.");
        torrentAge.setTooltip(tooltip);

        maxFileSize.setText(loader.getMaxFileSize(""));
        tooltip = new Tooltip();
        final String text = String.format("Max file size. If not given or 0 than now filtering. Torrents with " +
                "grater size will be filter out.%nPossible values modifiers K-kilo,M-mega,G-giga. " +
                "Example [52K = 52000 bytes, 1M = 1000000 bytes,%n# 4G = 4000000000 bytes]");
        tooltip.setText(text);
        maxFileSize.setTooltip(tooltip);

        releaseDate.setText(loader.getTorrentReleaseDate());
        tooltip = new Tooltip();
        final String tip = String.format("Torrent release date specified in format YYYY-MM-DD or YYYYMMDD. Ex.: " +
                "[2017-06-12,20170612]. All torrents older%nthan this date will be filter out.");
        tooltip.setText(tip);
        releaseDate.setTooltip(tooltip);

        repeatDownload.setSelected(StringUtils.booleanFromString(loader.getRepeatDownload("")));
        tooltip = new Tooltip();
        tooltip.setText("If checked, the same torrent will be downloaded each time, the program is executed.");
        repeatDownload.setTooltip(tooltip);

        torrentLocationText.setText(loader.getTorrentLocation(""));
        resultLocationText.setText(loader.getFilePath(""));

        creationMethod.setValue(DSMethod.valueOf(loader.getCreationMethod(DSMethod.REST.name())));
        tooltip = new Tooltip();
        tooltip.setText("Method of task creation. If REST then Synology API is used.");
        creationMethod.setTooltip(tooltip);

        torrentUrlType.setValue(TorrentUrlType.valueOf(loader.getTorrentUrlType(TorrentUrlType.torrent.name())));
        tooltip = new Tooltip();
        tooltip.setText("What link to use in order to create task. Default value is torrent.");
        torrentUrlType.setTooltip(tooltip);
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
        torrentLocationChooser.setText(".");
        torrentLocationChooser.setOnAction(torrentLocationAction());
        Tooltip tooltip = new Tooltip();
        tooltip.setText("Path where to save files *.torrent. When task creation method is COPY_FILE then path to " +
                "save torrent is mandatory.");
        torrentLocationChooser.setTooltip(tooltip);

        resultLocationChooser.setText(".");
        resultLocationChooser.setOnAction(resultLocationAction());
        tooltip = new Tooltip();
        tooltip.setText("Path where json with result should be written.");
        resultLocationChooser.setTooltip(tooltip);

        doneButton.setOnAction(doneAction());
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
                    .withTorrentLocation(torrentLocationText.getText())
                    .withResultLocation(torrentLocationText.getText())
                    .withCreationMethod(creationMethod.getValue())
                    .withTorrentUrlType(torrentUrlType.getValue());

            try {
                loader.store(configBuilder);
            } catch (IOException ex) {
                throw new ProgramException(UIError.SAVE_PROPERTIES, ex);
            }
            windowHandler.currentWindow().hide();
        };
    }
}
