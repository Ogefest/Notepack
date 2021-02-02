package notepack;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import notepack.app.domain.PopupController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 */
public class AboutDialogController extends PopupController implements Initializable {

    @FXML
    private Button btnCancel;

    public HostServices hostServices;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
