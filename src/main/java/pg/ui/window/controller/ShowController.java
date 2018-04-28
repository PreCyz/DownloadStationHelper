package pg.ui.window.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import pg.program.ShowDetail;
import pg.props.JsonShowHelper;
import pg.ui.window.WindowHandler;
import pg.ui.window.controller.setup.ActionButtonSetup;
import pg.ui.window.controller.setup.ComponentSetup;
import pg.util.AppConstants;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Gawa 2017-10-04
 */
public class ShowController extends AbstractController {

    @FXML
    private TableView<ShowDetail> showTableView;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button doneButton;

    private JsonShowHelper showHelper;
    private ObservableList<ShowDetail> data;
    private int maxIndex;
    private Set<ShowDetail> showsToDelete;

    public ShowController(WindowHandler windowHandler) {
        super(windowHandler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setUpColumns();
        setupButtons();
        showHelper = JsonShowHelper.getInstance();
        final Set<ShowDetail> showDetails = showHelper.getShowDetails();
        maxIndex = showDetails.stream().mapToInt(ShowDetail::getId).max().orElse(0);
        data = FXCollections.observableArrayList(showDetails);
        showTableView.setItems(data);
        showTableView.setEditable(true);
    }

    private void setupButtons() {
        ComponentSetup setup = new ActionButtonSetup(Arrays.asList(deleteButton, addButton, doneButton));
        setup.setup();
        addButton.setOnAction(addAction());
        deleteButton.setOnAction(removeAction());
        doneButton.setOnAction(doneAction());
    }

    @SuppressWarnings("unchecked")
    private void setUpColumns() {
        TableColumn<ShowDetail, ?> column = showTableView.getColumns().get(0);
        TableColumn<ShowDetail, Integer> idColumn = new TableColumn<>();
        idColumn.setText(column.getText());
        idColumn.setPrefWidth(column.getPrefWidth());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        column = showTableView.getColumns().get(1);
        TableColumn<ShowDetail, String> titleColumn = new TableColumn<>();
        titleColumn.setText(column.getText());
        titleColumn.setPrefWidth(column.getPrefWidth());
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(titleCellEditAction());
        titleColumn.setEditable(true);

        column = showTableView.getColumns().get(2);
        TableColumn<ShowDetail, String> baseWordsColumn = new TableColumn<>();
        baseWordsColumn.setText(column.getText());
        baseWordsColumn.setPrefWidth(column.getPrefWidth());
        baseWordsColumn.setCellValueFactory(new PropertyValueFactory<>("baseWords"));
        baseWordsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        baseWordsColumn.setOnEditCommit(baseWordsCellEditAction());
        baseWordsColumn.setEditable(true);

        column = showTableView.getColumns().get(3);
        TableColumn<ShowDetail, Integer> matchPrecisionColumn = new TableColumn<>();
        matchPrecisionColumn.setText(column.getText());
        matchPrecisionColumn.setPrefWidth(column.getPrefWidth());
        matchPrecisionColumn.setCellValueFactory(new PropertyValueFactory<>("matchPrecision"));
        matchPrecisionColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter()));
        matchPrecisionColumn.setOnEditCommit(precisionCellEditAction());
        matchPrecisionColumn.setEditable(true);

        showTableView.getColumns().clear();
        showTableView.getColumns().addAll(idColumn, titleColumn, baseWordsColumn, matchPrecisionColumn);

        showTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        showTableView.getSelectionModel().selectedItemProperty().addListener(toDeleteChangeListener());
    }

    private EventHandler<TableColumn.CellEditEvent<ShowDetail, String>> baseWordsCellEditAction() {
        return t -> {
            t.getTableView()
                    .getItems()
                    .get(t.getTablePosition().getRow())
                    .setBaseWords(t.getNewValue());
            t.getTableView()
                    .getItems()
                    .get(t.getTablePosition().getRow())
                    .setMatchPrecision(t.getNewValue().split(",").length);
            showTableView.refresh();
        };
    }

    private EventHandler<TableColumn.CellEditEvent<ShowDetail, String>> titleCellEditAction() {
        return t -> t.getTableView()
                .getItems()
                .get(t.getTablePosition().getRow())
                .setTitle(t.getNewValue());
    }

    private StringConverter<Integer> integerStringConverter() {
        return new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return getInt(string);
            }
        };
    }

    private EventHandler<TableColumn.CellEditEvent<ShowDetail, Integer>> precisionCellEditAction() {
        return t -> {
            final Integer newValue = getInt(t.getNewValue().toString());
            final ShowDetail editedShowDetail = t.getTableView().getItems().get(t.getTablePosition().getRow());
            final int defaultPrecision = editedShowDetail.getBaseWordsCount();
            if (newValue <= defaultPrecision) {
                editedShowDetail.setMatchPrecision(newValue);
            } else {
                data.get(editedShowDetail.getId() - 1).setMatchPrecision(defaultPrecision);
                showTableView.refresh();
            }
        };
    }

    private int getInt(String value) {
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private EventHandler<ActionEvent> addAction() {
        return event -> addShowDetail();
    }

    private void addShowDetail() {
        TextInputDialog dialog = new TextInputDialog(AppConstants.EMPTY_STRING);
        dialog.setTitle("Add title");
        dialog.setHeaderText("Enter your title here");
        dialog.setContentText(AppConstants.EMPTY_STRING);
        Optional<String> title = dialog.showAndWait();
        if (title.isPresent()) {
            data.add(new ShowDetail(++maxIndex, title.get()));
            showTableView.setItems(data);
        }

    }

    private EventHandler<ActionEvent> removeAction() {
        return event -> {
            showsToDelete = new LinkedHashSet<>(showTableView.getSelectionModel().getSelectedItems());
            List<ShowDetail> filteredData = data.stream()
                    .filter(sd -> showsToDelete.stream().noneMatch(std -> std.getId() == sd.getId()))
                    .sorted(ShowDetail.COMPARATOR)
                    .collect(Collectors.toList());
            for (int idx = 0; idx < filteredData.size(); idx++) {
                ShowDetail sd = filteredData.get(idx);
                sd.setId(idx + 1);
            }
            data = FXCollections.observableArrayList(filteredData);
            showTableView.setItems(data);
            showTableView.refresh();
        };
    }

    private EventHandler<ActionEvent> doneAction() {
        return event -> done();
    }

    private void done() {
        showHelper.saveShows(new LinkedHashSet<>(data));
        logger.info("Shows configuration saved.");
        windowHandler.currentWindow().hide();
    }

    private ChangeListener<ShowDetail> toDeleteChangeListener() {
        return (observable, oldValue, newValue) ->
                showsToDelete = new LinkedHashSet<>(showTableView.getSelectionModel().getSelectedItems());
    }

}
