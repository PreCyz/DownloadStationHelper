package pg.ui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;
import javafx.util.StringConverter;
import pg.props.ShowsPropertiesHelper;
import pg.exception.ProgramException;
import pg.exception.UIError;
import pg.ui.handler.WindowHandler;
import pg.util.StringUtils;
import pg.web.model.ShowDetail;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**Created by Gawa 2017-10-04*/
public class ShowController extends AbstractController {

    @FXML private TableView<ShowDetail> showTableView;
    @FXML private TextField baseWords;
    @FXML private TextField precision;
    @FXML private TextField idToRemove;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Button doneButton;

    private ShowsPropertiesHelper showHelper;
    private ObservableList<ShowDetail> data;
    private int maxIndex;

    public ShowController(WindowHandler windowHandler) {
        super(windowHandler);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        setUpColumns();
        showHelper = ShowsPropertiesHelper.getInstance();
        final Set<ShowDetail> showDetails = showHelper.getShowDetails();
        maxIndex = showDetails.stream().mapToInt(ShowDetail::getId).max().orElse(0);
        data = FXCollections.observableArrayList(showDetails);
        showTableView.setItems(data);
        showTableView.setEditable(true);
        showTableView.setRowFactory(tableViewRowFactory());
        addButton.setOnAction(addAction());
        removeButton.setOnAction(removeAction());
        doneButton.setOnAction(doneAction());
    }

    private void setUpColumns() {
        TableColumn<ShowDetail, ?> column = showTableView.getColumns().get(0);
        TableColumn<ShowDetail, Integer> idColumn = new TableColumn<>();
        idColumn.setText(column.getText());
        idColumn.setPrefWidth(column.getPrefWidth());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        column = showTableView.getColumns().get(1);
        TableColumn<ShowDetail, String> baseWordsColumn = new TableColumn<>();
        baseWordsColumn.setText(column.getText());
        baseWordsColumn.setPrefWidth(column.getPrefWidth());
        baseWordsColumn.setCellValueFactory(new PropertyValueFactory<>("baseWords"));
        baseWordsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        baseWordsColumn.setOnEditCommit(baseWordsCellEditAction());
        baseWordsColumn.setEditable(true);

        column = showTableView.getColumns().get(2);
        TableColumn<ShowDetail, Integer> matchPrecisionColumn = new TableColumn<>();
        matchPrecisionColumn.setText(column.getText());
        matchPrecisionColumn.setPrefWidth(column.getPrefWidth());
        matchPrecisionColumn.setCellValueFactory(new PropertyValueFactory<>("matchPrecision"));
        matchPrecisionColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter()));
        matchPrecisionColumn.setOnEditCommit(precisionCellEditAction());
        matchPrecisionColumn.setEditable(true);

        showTableView.getColumns().clear();
        showTableView.getColumns().addAll(idColumn, baseWordsColumn, matchPrecisionColumn);
    }

    private EventHandler<TableColumn.CellEditEvent<ShowDetail, String>> baseWordsCellEditAction() {
        return t -> t.getTableView()
                .getItems()
                .get(t.getTablePosition().getRow())
                .setBaseWords(t.getNewValue());
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
                        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
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
            final Set<ShowDetail> filtered = new TreeSet<>(showHelper.getShowDetailComparator());
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
        return event -> {
            if (!StringUtils.nullOrTrimEmpty(baseWords.getText())) {
                int matchPrecision = getInt(precision.getText());
                if (matchPrecision == 0) {
                    matchPrecision = baseWords.getText().split(",").length;
                }
                data.add(new ShowDetail(++maxIndex, baseWords.getText(), matchPrecision));
                baseWords.clear();
                precision.clear();
            }
        };
    }

    private EventHandler<ActionEvent> removeAction() {
        return event -> removeRow(getInt(idToRemove.getText()));
    }

    private EventHandler<ActionEvent> doneAction() {
        return event -> {
            try {
                showHelper.prepareAndStore(new HashSet<>(data));
                logger.info("Shows configuration saved.");
            } catch (IOException ex) {
                throw new ProgramException(UIError.SAVE_PROPERTIES, ex);
            }
            windowHandler.currentWindow().hide();
        };
    }

}
