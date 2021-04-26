package notepack.noterender;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import notepack.NoteTabContentCallback;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.task.TagPopup;

import java.net.URL;
import java.util.ResourceBundle;

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
    private HBox tagContainer;
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

    @FXML
    private Button searchOpenBtn;
    @FXML
    private Button closeSearchBtn;
    @FXML
    private Button searchDownBtn;
    //    @FXML
//    private Button searchUpBtn;
    @FXML
    private TextField searchInput;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        searchInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchClose(null);
            }
            if (event.getCode() == KeyCode.ENTER) {
                searchDown(null);
            }
        });
    }

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

        searchDownBtn.setVisible(true);
        searchInput.setVisible(true);
        closeSearchBtn.setVisible(true);
        searchOpenBtn.setVisible(false);
        searchOpenBtn.setManaged(false);

        searchInput.requestFocus();



        /*
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

         */
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

    @Override
    public Pane getTagContainer() {
        return tagContainer;
    }

    @FXML
    void searchClose(ActionEvent event) {
        searchOpenBtn.setVisible(true);
        searchOpenBtn.setManaged(true);
        searchInput.setVisible(false);
//        searchUpBtn.setVisible(false);
        searchDownBtn.setVisible(false);
        closeSearchBtn.setVisible(false);

    }

    @FXML
    void searchUp(ActionEvent event) {

    }

    @FXML
    void searchDown(ActionEvent event) {

        String string = searchInput.getText();
        if (string.length() == 0) {
            return;
        }

        int caretPost = textArea.getCaretPosition();
        int indexStart = textArea.getText().toLowerCase().indexOf(string.toLowerCase(), caretPost);

        if (textArea.getText().indexOf(string) == -1) {
            Alert a = new Alert(Alert.AlertType.INFORMATION, string + " was not found.", ButtonType.OK);
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

}
