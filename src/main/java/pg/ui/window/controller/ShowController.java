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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pg.exception.ProgramException;
import pg.exception.UIError;
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
    private TextField idToRemove;
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
        showTableView.setRowFactory(tableViewRowFactory());
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
        showTableView.setOnKeyReleased(keyReleasedEventHandler());
        showTableView.requestFocus();
    }

    private EventHandler<TableColumn.CellEditEvent<ShowDetail, String>> baseWordsCellEditAction() {
        return t -> t.getTableView()
                .getItems()
                .get(t.getTablePosition().getRow())
                .setBaseWords(t.getNewValue());
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

    private Callback<TableView<ShowDetail>, TableRow<ShowDetail>> tableViewRowFactory() {
        return tableView -> {
            TableRow<ShowDetail> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    ShowDetail clickedRow = row.getItem();
                    if (event.getButton() == MouseButton.PRIMARY && (event.getClickCount() < 3)) {
                        idToRemove.setText(String.valueOf(clickedRow.getId()));
                    } else if (event.getButton() == MouseButton.MIDDLE) {
                        removeRow(clickedRow.getId());
                    }
                }
            });
            return row;
        };
    }

    private void removeRow(final int index) {
        if (index > 0) {
            final Set<ShowDetail> filtered = new TreeSet<>(ShowDetail.COMPARATOR);
            filtered.addAll(data.stream()
                    .filter(d -> d.getId() != index)
                    .collect(Collectors.toSet()));
            int idx = 1;
            for (ShowDetail showDetail : filtered) {
                showDetail.setId(idx);
                idx++;
            }
            data.clear();
            data.addAll(filtered);
            idToRemove.clear();
            maxIndex--;
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
        return event -> removeRow(getInt(idToRemove.getText()));
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

    private EventHandler<KeyEvent> keyReleasedEventHandler() {
        return event -> {
            try {
                if (!showsToDelete.isEmpty() && EnumSet.of(KeyCode.DELETE, KeyCode.BACK_SPACE).contains(event.getCode())) {
                    Set<ShowDetail> currentSet = showHelper.getShowDetails();
                    currentSet.removeAll(showsToDelete);
                    showHelper.saveShows(currentSet);
                } else if (EnumSet.of(KeyCode.A, KeyCode.PLUS).contains(event.getCode())) {
                    addShowDetail();
                } else if (event.getCode() == KeyCode.ENTER) {
                    done();
                }
            } catch (Exception ex) {
                logger.error(ex.getLocalizedMessage());
                if (ex instanceof ProgramException) {
                    windowHandler.handleException((ProgramException) ex);
                } else {
                    windowHandler.handleException(new ProgramException(UIError.SHORTCUT, ex));
                }
            } finally {
                showTableView.requestFocus();
            }
        };
    }

}
