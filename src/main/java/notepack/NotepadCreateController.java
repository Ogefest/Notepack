/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import notepack.NotepadCreateCallback;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.Notepad;
import notepack.app.storage.Filesystem;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class NotepadCreateController implements Initializable {

    @FXML
    private TextField notepadName;
    @FXML
    private TextField directoryPath;

    private NotepadCreateCallback clbk;
    @FXML
    private Button btnCancel;

    private Notepad notepad;
    private boolean notepadEdition = false;
    @FXML
    private ToggleButton btnUserColor6;
    @FXML
    private ToggleButton btnUserColor5;
    @FXML
    private ToggleButton btnUserColor4;
    @FXML
    private ToggleButton btnUserColor3;
    @FXML
    private ToggleButton btnUserColor2;
    @FXML
    private ToggleButton btnUserColor1;

    private ToggleGroup tg;
    @FXML
    private Button btnSave;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tg = new ToggleGroup();
        btnUserColor1.setToggleGroup(tg);
        btnUserColor2.setToggleGroup(tg);
        btnUserColor3.setToggleGroup(tg);
        btnUserColor4.setToggleGroup(tg);
        btnUserColor5.setToggleGroup(tg);
        btnUserColor6.setToggleGroup(tg);

        btnUserColor1.setUserData("#356fcc");
        btnUserColor2.setUserData("#23a7cc");
        btnUserColor3.setUserData("#2db56d");
        btnUserColor4.setUserData("#92c738");
        btnUserColor5.setUserData("#d9c741");
        btnUserColor6.setUserData("#d12121");

        tg.selectToggle(btnUserColor1);
    }

    public void setNotepadCreateCallback(NotepadCreateCallback clbk) {
        this.clbk = clbk;
    }

    public void setNotepadToEdit(Notepad notepad) {
        this.notepad = notepad;
        notepadEdition = true;

        notepadName.setText(notepad.getName());
        
        btnSave.setText("Save");
    }

    @FXML
    private void onAdd(ActionEvent event) {

        String name = notepadName.getText();
        if (name.trim().length() == 0) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Notepad name is required");
            a.show();
            return;
        }

        if (notepad == null) {
            String path = directoryPath.getText();
            if (path.trim().length() == 0) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Directory path is required");
                a.show();
                return;
            }

            File f = new File(path);
            if (!f.exists()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Selected directory not exists");
                a.show();
                return;
            }

            NoteStorage storage = new Filesystem(path);
            notepad = new Notepad(storage, name);
        }

        if (tg.getSelectedToggle() == null) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Notepad color is required");
            a.show();
            return;
        }

        String color = (String) tg.getSelectedToggle().getUserData();
        notepad.setParam("color", color);

        clbk.ready(notepad);

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancel(ActionEvent event) {

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void onChooseDirectory(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Notepad directory");

        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            directoryPath.setText(selectedDirectory.getAbsolutePath());
        }

    }

}
