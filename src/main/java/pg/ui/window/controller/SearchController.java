package pg.ui.window.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import pg.program.SearchItem;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.completable.SearchCleanCompletable;
import pg.ui.window.controller.completable.SearchCompletable;
import pg.ui.window.controller.completable.StartDownloadCompletable;
import pg.util.AppConstants;
import pg.util.ImageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.joining;

public class SearchController extends AbstractController {

    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<SearchItem> searchResultTableView;
    @FXML
    private Button searchButton;
    @FXML
    private Label resultCountLabel;
    @FXML
    private Pane imagePane;
    @FXML
    private Button downloadButton;
    @FXML
    private Button stopButton;

    private final ExecutorService executor;
    private Property<ObservableList<SearchItem>> listProperty;
    private Property<Background> imageProperty;
    private List<SearchItem> toDownloadSearchItems;
    private Future<?> searchFeature;

    public SearchController(WindowHandler windowHandler) {
        super(windowHandler);
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setUpTaskTableView();
        setActions();
        setListeners();
    }

    @SuppressWarnings("unchecked")
    private void setUpTaskTableView() {
        TableColumn<SearchItem, ?> column = searchResultTableView.getColumns().get(0);
        TableColumn<SearchItem, String> titleColumn = new TableColumn<>();
        titleColumn.setText(column.getText());
        titleColumn.setPrefWidth(column.getPrefWidth());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn(titleConverter()));

        column = searchResultTableView.getColumns().get(1);
        TableColumn<SearchItem, Long> seedsColumn = new TableColumn<>();
        seedsColumn.setText(column.getText());
        seedsColumn.setPrefWidth(column.getPrefWidth());
        seedsColumn.setCellValueFactory(new PropertyValueFactory<>("seeds"));
        seedsColumn.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter()));

        column = searchResultTableView.getColumns().get(2);
        TableColumn<SearchItem, Long> peersColumn = new TableColumn<>();
        peersColumn.setText(column.getText());
        peersColumn.setPrefWidth(column.getPrefWidth());
        peersColumn.setCellValueFactory(new PropertyValueFactory<>("peers"));
        peersColumn.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter()));

        column = searchResultTableView.getColumns().get(3);
        TableColumn<SearchItem, Long> sizeColumn = new TableColumn<>();
        sizeColumn.setText(column.getText());
        sizeColumn.setPrefWidth(column.getPrefWidth());
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setCellFactory(TextFieldTableCell.forTableColumn(sizeStringConverter()));

        column = searchResultTableView.getColumns().get(4);
        TableColumn<SearchItem, String> dateColumn = new TableColumn<>();
        dateColumn.setText(column.getText());
        dateColumn.setPrefWidth(column.getPrefWidth());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        column = searchResultTableView.getColumns().get(5);
        TableColumn<SearchItem, String> moduleColumn = new TableColumn<>();
        moduleColumn.setText(column.getText());
        moduleColumn.setPrefWidth(column.getPrefWidth());
        moduleColumn.setCellValueFactory(new PropertyValueFactory<>("moduleId"));

        searchResultTableView.getColumns().clear();
        searchResultTableView.getColumns().addAll(
                titleColumn, seedsColumn, peersColumn, sizeColumn, dateColumn, moduleColumn
        );

        searchResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        searchResultTableView.getSelectionModel().selectedItemProperty().addListener(selectItemChangeListener());
        searchResultTableView.setOnKeyReleased(keyReleasedEventHandler());
        searchResultTableView.requestFocus();
        searchResultTableView.setPlaceholder(new Label("Nothing to display"));

        final ObservableList<SearchItem> items = FXCollections.observableArrayList();
        listProperty = new SimpleListProperty<>();
        listProperty.setValue(items);
        searchResultTableView.itemsProperty().bind(listProperty);
    }

    private EventHandler<? super KeyEvent> keyReleasedEventHandler() {
        return (EventHandler<KeyEvent>) event -> {
            if (event.getCode() == KeyCode.D) {
                startDownload();
            }
        };
    }

    private ChangeListener<? super SearchItem> selectItemChangeListener() {
        return (ChangeListener<SearchItem>) (observable, oldValue, newValue) -> {
            toDownloadSearchItems = new ArrayList<>();
            toDownloadSearchItems.addAll(searchResultTableView.getSelectionModel().getSelectedItems());
            downloadButton.setDisable(toDownloadSearchItems.isEmpty());
        };
    }

    private StringConverter<String> titleConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(String object) {
                String result = object;
                final int lastSpace = object.lastIndexOf(" ");
                if (lastSpace > 0) {
                    result = object.substring(0, lastSpace);
                }
                return result;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        };
    }

    private StringConverter<Long> longStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Long object) {
                return object.toString();
            }

            @Override
            public Long fromString(String string) {
                return Long.valueOf(string);
            }
        };
    }

    private StringConverter<Long> sizeStringConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Long object) {
                final String value = object.toString();
                String result = value + " B";
                if (value.length() == 12) {
                    result = value.substring(0, 3) + "," + value.substring(3, 5) + " GB";
                } else if (value.length() == 11) {
                    result = value.substring(0, 1) + "," + value.substring(1, 3) + " GB";
                } else if (value.length() == 10) {
                    result = value.charAt(0) + "," + value.substring(1, 3) + " GB";
                } else if (value.length() == 9) {
                    result = value.substring(0, 3) + "," + value.substring(3, 5) + " MB";
                } else if (value.length() == 8) {
                    result = value.substring(0, 2) + "," + value.substring(1, 3) + " MB";
                } else if (value.length() == 7) {
                    result = value.charAt(0) + "," + value.substring(1, 3) + " MB";
                }
                return result;
            }

            @Override
            public Long fromString(String string) {
                final String value = string.substring(0, string.indexOf(" ")).replaceAll(",", "");
                return Long.valueOf(value);
            }
        };
    }

    private void setActions() {
        searchButton.setOnAction(event -> {
            logger.info("Search button pressed. Text captured [{}]", searchTextField.getText());
            searchTorrents(searchTextField.getText());
        });

        searchTextField.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                logger.info("Enter key pressed. Text captured [{}]", searchTextField.getText());
                searchTorrents(searchTextField.getText());
            }
        });

        stopButton.setOnAction(event -> {
            searchFeature.cancel(true);
            executor.submit(new SearchCleanCompletable(
                    windowHandler.getDsApiDetail(), imageProperty, windowHandler.getSearchTaskId()
            ));
        });

        downloadButton.setOnAction(event -> startDownload());
    }

    private void searchTorrents(String keywords) {
        if (!keywords.isEmpty()) {
            logger.info("Searching torrents with following keywords [{}]", keywords);
            setProgressImage();
            searchFeature = executor.submit(new SearchCompletable(
                    listProperty, imageProperty, keywords, windowHandler.getDsApiDetail(), windowHandler
            ));
        }
    }

    private void setProgressImage() {
        try {
            final int width = 4;
            final int height = 4;
            imageProperty = new SimpleObjectProperty<>(
                    ImageUtils.getBackground(AppConstants.CONNECTING_GIF, width, height)
            );
        } catch (IOException e) {
            logger.warn("Could not load progress gif.", e);
        } finally {
            imagePane.backgroundProperty().bindBidirectional(imageProperty);
        }

    }

    private void startDownload() {
        if (!toDownloadSearchItems.isEmpty()) {
            logger.info("Starting download following items {}",
                    toDownloadSearchItems.stream().map(SearchItem::getTitle).collect(joining(","))
            );
            executor.submit(new StartDownloadCompletable(
                    toDownloadSearchItems, imageProperty, windowHandler.getDsApiDetail(), executor
            ));
        }
    }

    private void setListeners() {
        listProperty.addListener((observable, oldValue, newValue) -> {
            logger.info("Values in the list has changed. New size {}", newValue.size());
            resultCountLabel.setText(String.valueOf(newValue.size()));
        });
    }

}
