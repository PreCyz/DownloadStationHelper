package pg.ui.window.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import pg.ui.window.controller.completable.SearchCompletable;
import pg.util.AppConstants;
import pg.util.ImageUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final ExecutorService executor;
    private Property<ObservableList<SearchItem>> listProperty;
    private Property<Background> imageProperty;

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
        //titleColumn.setResizable(false);
        titleColumn.setText(column.getText());
        titleColumn.setPrefWidth(column.getPrefWidth());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        column = searchResultTableView.getColumns().get(1);
        TableColumn<SearchItem, Long> seedsColumn = new TableColumn<>();
        //seedsColumn.setResizable(false);
        seedsColumn.setText(column.getText());
        seedsColumn.setPrefWidth(column.getPrefWidth());
        seedsColumn.setCellValueFactory(new PropertyValueFactory<>("seeds"));
        seedsColumn.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter()));

        column = searchResultTableView.getColumns().get(2);
        TableColumn<SearchItem, Long> peersColumn = new TableColumn<>();
        //peersColumn.setResizable(false);
        peersColumn.setText(column.getText());
        peersColumn.setPrefWidth(column.getPrefWidth());
        peersColumn.setCellValueFactory(new PropertyValueFactory<>("peers"));
        peersColumn.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter()));

        column = searchResultTableView.getColumns().get(3);
        TableColumn<SearchItem, Long> sizeColumn = new TableColumn<>();
        //peersColumn.setResizable(false);
        sizeColumn.setText(column.getText());
        sizeColumn.setPrefWidth(column.getPrefWidth());
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        sizeColumn.setCellFactory(TextFieldTableCell.forTableColumn(longStringConverter()));

        column = searchResultTableView.getColumns().get(4);
        TableColumn<SearchItem, String> dateColumn = new TableColumn<>();
        //peersColumn.setResizable(false);
        dateColumn.setText(column.getText());
        dateColumn.setPrefWidth(column.getPrefWidth());
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        searchResultTableView.getColumns().clear();
        searchResultTableView.getColumns().addAll(titleColumn, seedsColumn, peersColumn, sizeColumn, dateColumn);

        searchResultTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        searchResultTableView.getSelectionModel().selectedItemProperty().addListener(toManageChangeListener());
        searchResultTableView.setOnKeyReleased(keyReleasedEventHandler());
        searchResultTableView.requestFocus();
        searchResultTableView.setPlaceholder(new Label("Nothing to display"));

        final ObservableList<SearchItem> items = FXCollections.observableArrayList();
        listProperty = new SimpleListProperty<>();
        listProperty.setValue(items);
        searchResultTableView.itemsProperty().bind(listProperty);
    }

    private EventHandler<? super KeyEvent> keyReleasedEventHandler() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

            }
        };
    }

    private ChangeListener<? super SearchItem> toManageChangeListener() {
        return new ChangeListener<SearchItem>() {
            @Override
            public void changed(ObservableValue<? extends SearchItem> observable, SearchItem oldValue, SearchItem newValue) {

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
    }

    private void searchTorrents(String keywords) {
        if (!keywords.isEmpty()) {
            logger.info("Searching torrents with following keywords [{}]", keywords);
            setProgressImage();
            executor.submit(
                    new SearchCompletable(listProperty, imageProperty, keywords, windowHandler.getDsApiDetail())
            );
        }
    }

    private void setListeners() {
        listProperty.addListener((observable, oldValue, newValue) -> {
            logger.info("Values in the list has changed. Old size {}, new size {}",
                    oldValue.size(), newValue.size());
            resultCountLabel.setText(String.valueOf(newValue.size()));
        });
    }

    private void setProgressImage() {
        try {
            Background background = ImageUtils.getBackground(AppConstants.CONNECTING_GIF);
            imageProperty = new SimpleObjectProperty<>(background);
        } catch (IOException e) {
            logger.warn("Could not load progress gif.", e);
        } finally {
            imagePane.backgroundProperty().bindBidirectional(imageProperty);
        }

    }
}
