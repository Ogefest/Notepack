package notepack;

import notepack.NotepadCreateCallback;
import java.io.File;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.NoteStorageMiddleware;
import notepack.app.domain.Notepad;
import notepack.app.storage.Filesystem;
import notepack.app.storage.Webdav;
import notepack.engine.EngineController;
import notepack.engine.EngineType;

/**
 * FXML Controller class
 *
 */
public class NotepadCreateController implements Initializable {

    @FXML
    private TextField notepadName;
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
    private TextField webDavUrl;
    private TextField webDavUsername;
    private PasswordField webDavPassword;
    @FXML
    private ComboBox<EngineType> engineSelection;
    @FXML
    private AnchorPane engineForm;

    private EngineController currentFormController;
    @FXML
    private CheckBox gpgCheckbox;
    @FXML
    private TextField gpgPublicKeyPath;
    @FXML
    private Button gpgSelectPublicKey;
    @FXML
    private TextField gpgPrivateKeyPath;
    @FXML
    private Button gpgSelectPrivateKey;
    @FXML
    private Label gpgPublicLabel;
    @FXML
    private Label gpgPrivateLabel;

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

        gpgCheckbox.selectedProperty().addListener((o) -> {
            if (gpgCheckbox.isSelected()) {
                gpgPrivateLabel.setDisable(false);
                gpgPublicLabel.setDisable(false);
                gpgPrivateKeyPath.setDisable(false);
                gpgPublicKeyPath.setDisable(false);
                gpgSelectPrivateKey.setDisable(false);
                gpgSelectPublicKey.setDisable(false);
            } else {
                gpgPrivateLabel.setDisable(true);
                gpgPublicLabel.setDisable(true);
                gpgPrivateKeyPath.setDisable(true);
                gpgPublicKeyPath.setDisable(true);
                gpgSelectPrivateKey.setDisable(true);
                gpgSelectPublicKey.setDisable(true);
            }
        });

        engineSelection.getItems().add(new EngineType("Filesystem", "engine/Filesystem.fxml"));
        engineSelection.getItems().add(new EngineType("WebDav", "engine/Webdav.fxml"));

        engineSelection.getSelectionModel().selectedIndexProperty().addListener((ov, t, t1) -> {

            EngineType et = engineSelection.getSelectionModel().getSelectedItem();

            engineForm.getChildren().clear();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(et.getFxml()));

            try {
                Parent root = fxmlLoader.load();
                currentFormController = (EngineController) fxmlLoader.getController();

                engineForm.getChildren().add(root);

            } catch (IOException ex) {
                Logger.getLogger(NotepadCreateController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    public void setNotepadCreateCallback(NotepadCreateCallback clbk) {
        this.clbk = clbk;
    }

    public void setNotepadToEdit(Notepad notepad) {
        this.notepad = notepad;
        notepadEdition = true;

        notepadName.setText(notepad.getName());

        engineSelection.getItems().clear();

        NoteStorage parentStorage = ((NoteStorageMiddleware) notepad.getStorage()).getParentStorage();

        if (Filesystem.class.isInstance(parentStorage)) {
            engineSelection.getItems().add(new EngineType("Filesystem", "engine/Filesystem.fxml"));
        }
        if (Webdav.class.isInstance(parentStorage)) {
            engineSelection.getItems().add(new EngineType("WebDav", "engine/Webdav.fxml"));
        }
        engineSelection.getSelectionModel().select(0);
        engineSelection.setDisable(true);

        if (notepad.getParam("gpg-enabled").equals("1")) {
            gpgPrivateKeyPath.setText(notepad.getParam("gpg-private-key"));
            gpgPublicKeyPath.setText(notepad.getParam("gpg-public-key"));
            gpgCheckbox.setSelected(true);
        }

        gpgCheckbox.setDisable(true);
        gpgPrivateKeyPath.setDisable(true);
        gpgPublicKeyPath.setDisable(true);
        gpgSelectPrivateKey.setDisable(true);
        gpgSelectPublicKey.setDisable(true);

        currentFormController.setStorage(notepad.getStorage());

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
            notepad = new Notepad(currentFormController.getStorage(), name);
        } else {
            notepad.getStorage().setConfiguration(currentFormController.getStorage().getConfiguration());
        }

        if (tg.getSelectedToggle() == null) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Notepad color is required");
            a.show();
            return;
        }

        if (gpgCheckbox.isSelected()) {
            notepad.setParam("gpg-enabled", "1");

            File privateFile = new File(gpgPrivateKeyPath.getText());
            File publicFile = new File(gpgPublicKeyPath.getText());

            if (!privateFile.exists()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "GPG private key not exists");
                a.show();
                return;
            }
            if (!publicFile.exists()) {
                Alert a = new Alert(Alert.AlertType.ERROR, "GPG public key not exists");
                a.show();
                return;
            }

            notepad.setParam("gpg-private-key", privateFile.getAbsolutePath());
            notepad.setParam("gpg-public-key", publicFile.getAbsolutePath());

        } else {
            notepad.setParam("gpg-enabled", "0");
        }

        String color = (String) tg.getSelectedToggle().getUserData();
        notepad.setParam("color", color);

        notepad.registerProcessors();

        clbk.ready(notepad);

        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void webDavTestConnection(ActionEvent event) {

        String url = webDavUrl.getText();
        String username = webDavUsername.getText();
        String password = webDavPassword.getText();

        NoteStorageConfiguration nsc = new NoteStorageConfiguration();
        nsc.set("url", url);
        nsc.set("username", username);
        nsc.set("password", password);

        Webdav wd = new Webdav(nsc);

        NoteStorageItem noteItem = wd.getItemsInStorage();
        wd.refreshItemsInStorage();

        noteItem.getName();
    }

    @FXML
    private void onSelectPublicKey(ActionEvent event) {
        Stage stage = (Stage) gpgPublicKeyPath.getScene().getWindow();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select GPG public key");

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            gpgPublicKeyPath.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void onSelectPrivateKey(ActionEvent event) {
        Stage stage = (Stage) gpgPrivateKeyPath.getScene().getWindow();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select GPG private key");

        File selectedFile = chooser.showOpenDialog(stage);
        if (selectedFile != null) {
            gpgPrivateKeyPath.setText(selectedFile.getAbsolutePath());
        }        
    }

}
