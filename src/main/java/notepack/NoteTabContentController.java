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

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
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
//    private MenuItem menuEditUndo;
//    private MenuItem menuEditRedo;
//    private MenuItem menuEditCut;
//    private MenuItem menuEditCopy;
    @FXML
    private AnchorPane tabBackground;

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
        
        tabBackground.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());

        textArea.requestFocus();
    }
    
    public Note getNote() {
        return note;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    private void onMenuUndo(ActionEvent event) {
        textArea.undo();
    }

    private void onMenuRedo(ActionEvent event) {
        textArea.redo();
    }

    private void onMenuCut(ActionEvent event) {
        textArea.cut();
    }

    private void onMenuCopy(ActionEvent event) {
        textArea.copy();
    }

    private void onMenuPaste(ActionEvent event) {
        textArea.paste();
    }

    private void onMenuDelete(ActionEvent event) {
        textArea.deleteText(textArea.getSelection());
    }

    private void onMenuSelectAll(ActionEvent event) {
        textArea.selectAll();
    }

}
