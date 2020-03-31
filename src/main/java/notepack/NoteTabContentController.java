/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import javafx.scene.control.TextArea;
import notepack.app.domain.Note;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class NoteTabContentController implements Initializable {

    @FXML
    private TextArea textArea;

    private Note note;
    @FXML
    private MenuItem menuEditUndo;
    @FXML
    private MenuItem menuEditRedo;
    @FXML
    private MenuItem menuEditCut;
    @FXML
    private MenuItem menuEditCopy;
    @FXML
    private MenuItem menuEditPaste;
    @FXML
    private MenuItem menuEditDelete;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setNote(Note note) {
        this.note = note;

        textArea.setText(note.getContent());
        textArea.textProperty().addListener((ov, oldValue, newValue) -> {

            menuEditUndo.setDisable(true);
            if (textArea.isUndoable()) {
                menuEditUndo.setDisable(false);
            }
            menuEditRedo.setDisable(true);
            if (textArea.isRedoable()) {
                menuEditRedo.setDisable(false);
            }

        });
        textArea.selectionProperty().addListener((o) -> {
            menuEditCut.setDisable(true);
            menuEditCopy.setDisable(true);
            if (textArea.getSelectedText().length() > 0) {
                menuEditCut.setDisable(false);
                menuEditCopy.setDisable(false);
            }
        });

        textArea.requestFocus();
    }

    public TextArea getTextArea() {
        return textArea;
    }

    @FXML
    private void onMenuUndo(ActionEvent event) {
        textArea.undo();
    }

    @FXML
    private void onMenuRedo(ActionEvent event) {
        textArea.redo();
    }

    @FXML
    private void onMenuCut(ActionEvent event) {
        textArea.cut();
    }

    @FXML
    private void onMenuCopy(ActionEvent event) {
        textArea.copy();
    }

    @FXML
    private void onMenuPaste(ActionEvent event) {
        textArea.paste();
    }

    @FXML
    private void onMenuDelete(ActionEvent event) {
        textArea.deleteText(textArea.getSelection());
    }

    @FXML
    private void onMenuSelectAll(ActionEvent event) {
        textArea.selectAll();
    }

}
