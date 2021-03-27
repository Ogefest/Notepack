package notepack.noterender;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.*;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.storage.PreferencesSettings;
import notepack.app.task.TagPopup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 */
public class TextAreaController implements Initializable, NoteRenderController {

    @FXML
    protected TextArea textArea;

    protected Note note;
    protected App app;

    @FXML
    protected AnchorPane tabBackground;
    @FXML
    protected MenuItem menuUndo;
    @FXML
    protected MenuItem menuRedo;
    @FXML
    protected MenuItem menuCut;
    @FXML
    protected MenuItem menuCopy;

    protected NoteTabContentCallback clbk;
    @FXML
    protected CheckMenuItem wordWrapMenu;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

//    public void setNoteTabContentCallback(NoteTabContentCallback clbk) {
//        this.clbk = clbk;
//    }
    @Override
    public void setState(App app, Note note) {
        this.note = note;
        this.app = app;

        textArea.setText(new String(note.getContent()));

        tabBackground.setStyle("-fx-background-color: " + note.getWorkspace().getBackgroundColor());

        textArea.textProperty().addListener((ov, oldValue, newValue) -> {
            app.changeNote(note, newValue.getBytes());
        });

        textArea.requestFocus();
        Platform.runLater(() -> {
            textArea.requestFocus();
        });
    }

    @Override
    public Note getNote() {
        return note;
    }

    @FXML
    protected void onSaveNote(ActionEvent event) {
        app.saveNote(note);
    }

    @FXML
    protected void onSearchInNote(ActionEvent event) {
        showSearchReplaceForm();
    }

    public void showSearchReplaceForm() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/SearchForm.fxml"));

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
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);

            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void onWordWrap(ActionEvent event) {
        if (textArea.isWrapText()) {
            textArea.setWrapText(false);
            wordWrapMenu.setSelected(false);
        } else {
            textArea.setWrapText(true);
            wordWrapMenu.setSelected(true);
        }
    }

    @FXML
    protected void onUndo(ActionEvent event) {
        textArea.undo();
    }

    @FXML
    protected void onRedo(ActionEvent event) {
        textArea.redo();
    }

    @FXML
    protected void onCut(ActionEvent event) {
        textArea.cut();
    }

    @FXML
    protected void onCopy(ActionEvent event) {
        textArea.copy();
    }

    @FXML
    protected void onPaste(ActionEvent event) {
        textArea.paste();
    }

    @FXML
    protected void onSelectAll(ActionEvent event) {
        textArea.selectAll();
        ;
    }

    @FXML
    protected void onCloseNote(ActionEvent event) {
        app.closeNote(note);
    }

    @FXML
    protected void onTagNote(ActionEvent event) {
        app.addTask(new TagPopup(note));
    }

    @Override
    public void noteActivated() {
    }

    @Override
    public void noteDeactivated() {
    }

}
