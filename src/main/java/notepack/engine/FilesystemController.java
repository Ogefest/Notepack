package notepack.engine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.storage.Filesystem;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 */
public class FilesystemController implements Initializable, EngineController {

    @FXML
    private TextField directoryPath;

    NoteStorageConfiguration nsc;
    NoteStorage storage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onChooseDirectory(ActionEvent event) {

        Stage stage = (Stage) directoryPath.getScene().getWindow();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Workspace directory");

        File selectedDirectory = chooser.showDialog(stage);
        if (selectedDirectory != null) {
            directoryPath.setText(selectedDirectory.getAbsolutePath());
        }

    }

    @Override
    public void setStorage(NoteStorage storage) {
        this.storage = storage;
        directoryPath.setText(storage.getConfiguration().get("directory"));
    }

    @Override
    public NoteStorage getStorage() {

        NoteStorageConfiguration conf = new NoteStorageConfiguration();
        conf.set("directory", directoryPath.getText());

        Filesystem fs = new Filesystem(conf);

        return fs;
    }

}
