package notepack;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import notepack.app.domain.PopupController;
import notepack.app.domain.Version;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 */
public class AboutDialogController extends PopupController implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private Label versionLabel;

    public HostServices hostServices;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        versionLabel.setText("Version: " + Version.app());
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private void onCancel(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @FXML
    private void openBrowser(ActionEvent event) {

        hostServices.showDocument("https://github.com/Ogefest/Notepack");

    }

}
