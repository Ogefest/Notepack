package notepack;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import notepack.app.domain.Note;
import notepack.app.storage.PreferencesSettings;

/**
 * FXML Controller class
 *
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
    @FXML
    private CheckMenuItem wordWrapMenu;

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

        textArea.setText(new String(note.getContent()));

        tabBackground.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());

        textArea.requestFocus();
    }

    public Note getNote() {
        return note;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    private void onOpenNote(ActionEvent event) {
        clbk.onOpenNote();
    }

    @FXML
    private void onSaveNote(ActionEvent event) {
        clbk.onSaveNote(note);
    }

    @FXML
    private void onSearchInNote(ActionEvent event) {
        showSearchReplaceForm();
    }

    public void showSearchReplaceForm() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("SearchForm.fxml"));

        Scene scene;
        try {
            Parent root = fxmlLoader.load();

            SearchFormController ctrl = (SearchFormController) fxmlLoader.getController();
            ctrl.setCallback(new SearchFormCallback() {
                @Override
                public void search(String string) {
                    int caretPost = textArea.getCaretPosition();
                    int indexStart = textArea.getText().indexOf(string, caretPost);

                    if (textArea.getText().indexOf(string) == -1) {
                        Alert a = new Alert(AlertType.INFORMATION, string + " was not found. Start from beginning?", ButtonType.OK);
                        a.showAndWait();
                    } else {
                        if (indexStart > 0) {
                            textArea.selectRange(indexStart, indexStart + string.length());
                        } else {
                            indexStart = textArea.getText().indexOf(string, 0);
                            textArea.selectRange(indexStart, indexStart + string.length());
                        }
                    }

                }

                @Override
                public void replace(String from, String to, boolean replaceAll) {

                    int caretPost = textArea.getCaretPosition();

                    if (replaceAll) {
                        String current = textArea.getText();
                        String afterReplacement = current.replace(from, to);
                        textArea.setText(afterReplacement);
                    } else {
                        String taText = textArea.getText();
                        int indexStart = taText.indexOf(from, caretPost);
                        if (indexStart > 0) {

                            String part1 = taText.substring(0, indexStart);
                            String part2 = taText.substring(indexStart + from.length());

                            String result = part1 + to + part2;
                            textArea.setText(result);
                            textArea.positionCaret(indexStart);

                        }
                    }
                }
            });

            scene = new Scene(root);
            new Theme(new PreferencesSettings()).setCurrent(scene);

            Stage stage = new Stage();
            stage.setTitle("Search/Replace");
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onWordWrap(ActionEvent event) {
        if (textArea.isWrapText()) {
            textArea.setWrapText(false);
            wordWrapMenu.setSelected(false);
        } else {
            textArea.setWrapText(true);
            wordWrapMenu.setSelected(true);
        }
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
