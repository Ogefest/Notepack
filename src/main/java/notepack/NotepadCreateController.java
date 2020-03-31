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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.Notepad;
import notepack.app.storage.Filesystem;
import notepack.app.domain.SessionStorage;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setNotepadCreateCallback(NotepadCreateCallback clbk) {
        this.clbk = clbk;
    }

    @FXML
    private void onAdd(ActionEvent event) {
        
        String name = notepadName.getText();
        if (name.trim().length() == 0) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Notepad name is required");
            a.show();
            return;
        }
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
        Notepad notepad = new Notepad(storage, name);
        clbk.added(notepad);
        
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
