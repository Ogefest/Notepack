package notepack.engine;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.exception.MessageError;
import notepack.app.storage.Webdav;

/**
 * FXML Controller class
 *
 */
public class WebdavController implements Initializable, EngineController {

    @FXML
    private TextField webDavUrl;
    @FXML
    private TextField webDavUsername;
    @FXML
    private Button webdavTestBtn;
    @FXML
    private PasswordField webDavPassword;

    private NoteStorageConfiguration nsc;
    private NoteStorage storage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void webDavTestConnection(ActionEvent event) {

        String url = webDavUrl.getText();
        String username = webDavUsername.getText();
        String password = webDavPassword.getText();

        NoteStorageConfiguration nsc = new NoteStorageConfiguration();
        nsc.set("url", url);
        nsc.set("username", username);
        nsc.set("password", password);

        Webdav wd = new Webdav(nsc);

        try {
            NoteStorageItem noteItem = wd.getItemsInStorage();
            wd.refreshItemsInStorage();
        } catch (MessageError ex) {
            Alert a = new Alert(Alert.AlertType.ERROR, "WebDAV problem: " + ex.getMessage());
            a.showAndWait();
            return;
        }

        Alert a = new Alert(Alert.AlertType.INFORMATION, "WebDAV configuration looks good ");
        a.showAndWait();

    }

    @Override
    public void setStorage(NoteStorage storage) {
        this.storage = storage;
        webDavUrl.setText(storage.getConfiguration().get("url"));
        webDavUsername.setText(storage.getConfiguration().get("username"));
        webDavPassword.setText(storage.getConfiguration().get("password"));
    }

    @Override
    public NoteStorage getStorage() {

        NoteStorageConfiguration conf = new NoteStorageConfiguration();
        conf.set("url", webDavUrl.getText());
        conf.set("username", webDavUsername.getText());
        conf.set("password", webDavPassword.getText());

        Webdav wd = new Webdav(conf);

        return wd;
    }

}
