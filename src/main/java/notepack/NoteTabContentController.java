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
    @FXML
    private AnchorPane tabBackground;
    @FXML
    private MenuItem menuUndo;
    @FXML
    private MenuItem menuRedo;
    @FXML
    private MenuItem menuCut;
    @FXML
    private MenuItem menuCopy;
    
    private NoteTabContentCallback clbk;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setNoteTabContentCallback(NoteTabContentCallback clbk) {
        this.clbk = clbk;
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

    @FXML
    private void onOpenNote(ActionEvent event) {
        clbk.onOpenNote();
    }

    @FXML
    private void onSaveNote(ActionEvent event) {
        clbk.onSaveNote(note);
    }

    @FXML
    private void onSearchInNote(ActionEvent event) {
    }

    @FXML
    private void onWordWrap(ActionEvent event) {
        textArea.setWrapText(true);
    }

    @FXML
    private void onUndo(ActionEvent event) {
        textArea.undo();
    }

    @FXML
    private void onRedo(ActionEvent event) {
        textArea.redo();
    }

    @FXML
    private void onCut(ActionEvent event) {
        textArea.cut();
    }

    @FXML
    private void onCopy(ActionEvent event) {
        textArea.copy();
    }

    @FXML
    private void onPaste(ActionEvent event) {
        textArea.paste();
    }

    @FXML
    private void onSelectAll(ActionEvent event) {
        textArea.selectAll();;
    }

    @FXML
    private void onCloseNote(ActionEvent event) {
        clbk.onCloseNote(note);
    }

}
