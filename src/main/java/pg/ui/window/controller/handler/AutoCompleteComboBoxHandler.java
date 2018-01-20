package pg.ui.window.controller.handler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.EnumSet;

/** Created by Gawa 2018-01-20 */
public class AutoCompleteComboBoxHandler<T> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private final Label numberOfShowsLabel;
    private ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;
    private boolean terminate = false;

    public AutoCompleteComboBoxHandler(ComboBox<T> comboBox, Label numberOfShowsLabel) {
        this.comboBox = comboBox;
        this.data = comboBox.getItems();
        this.comboBox.setOnKeyPressed(t -> comboBox.hide());
        this.comboBox.setOnKeyReleased(this);
        this.numberOfShowsLabel = numberOfShowsLabel;
    }

    @Override
    public void handle(KeyEvent event) {
        if (doNothing(event)) {
            return;
        }

        updateCaretPosition(event);

        if (terminate) {
            return;
        }

        updateComponents(filterData());
        updateCaret();
    }

    private boolean doNothing(KeyEvent event) {
        EnumSet<KeyCode> doNothingKeyCodeSet = EnumSet.of(
                KeyCode.RIGHT, KeyCode.LEFT, KeyCode.HOME, KeyCode.END, KeyCode.TAB, KeyCode.ESCAPE, KeyCode.ENTER
        );
        return doNothingKeyCodeSet.contains(event.getCode()) || event.isControlDown();
    }

    private void updateCaretPosition(KeyEvent event) {
        String comboBoxText = comboBox.getEditor().getText();
        switch (event.getCode()) {
            case UP:
                caretPos = -1;
                moveCaret(comboBoxText.length());
                terminate = true;
            case DOWN:
                if (!comboBox.isShowing()) {
                    comboBox.show();
                }
                caretPos = -1;
                moveCaret(comboBoxText.length());
                terminate = true;
            case BACK_SPACE:
                moveCaretToPos = true;
                caretPos = comboBox.getEditor().getCaretPosition();
                terminate = false;
                break;
            case DELETE:
                moveCaretToPos = true;
                caretPos = comboBox.getEditor().getCaretPosition();
                terminate = false;
                break;
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

    private void updateComponents(ObservableList<T> list) {
        String comboBoxText = comboBox.getEditor().getText();
        comboBox.setItems(list);
        comboBox.getEditor().setText(comboBoxText);
        if (!list.isEmpty()) {
            comboBox.show();
            String info = numberOfShowsLabel.getText().substring(0, numberOfShowsLabel.getText().indexOf(":") + 1);
            numberOfShowsLabel.setText(String.format("%s %d", info, list.size()));
        }
    }

    private void updateCaret() {
        if (!moveCaretToPos) {
            caretPos = -1;
        }
        String comboBoxText = comboBox.getEditor().getText();
        moveCaret(comboBoxText.length());
    }

    private ObservableList<T> filterData() {
        String comboBoxText = comboBox.getEditor().getText();
        ObservableList<T> list = FXCollections.observableArrayList();
        for (T value : data) {
            String lowerCaseValue = value.toString().toLowerCase();
            if (lowerCaseValue.startsWith(comboBoxText.toLowerCase())) {
                list.add(value);
            }
        }
        return list;
    }

}
