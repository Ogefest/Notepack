package notepack;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import notepack.app.domain.PopupController;
import notepack.app.domain.Version;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 */
public class NewVersionController extends PopupController implements Initializable {

    @FXML
    private Button btnCancel;

    @FXML
    private Label versionLabel;

    public HostServices hostServices;

    private String urlWithVersion;
    private boolean isNewVersionAvailable = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public boolean isNewVersionAvailable() {

        String currentVersion = Version.app();
        String currentBuild = Version.build();
        String[] parts = currentVersion.split("\\.");

        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        int g1 = 0;
        int g2 = 0;
        int g3 = 0;

        if (parts.length == 3) {
            v1 = Integer.parseInt(parts[0]);
            v2 = Integer.parseInt(parts[1]);
            v3 = Integer.parseInt(parts[2]);
        }

        HttpURLConnection con = null;
        try {
            URL appVersionUrl = new URL("https://notepackapp.com/version.json?build=" + currentBuild + "&version=" + currentVersion + "&system=" + System.getProperty("os.name").replace(' ','_'));
            con = (HttpURLConnection) appVersionUrl.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(content.toString());
            String githubVersion = jsonObject.getString("version");
            String[] ghParts = githubVersion.split("\\.");

            if (ghParts.length == 3) {
                g1 = Integer.parseInt(ghParts[0]);
                g2 = Integer.parseInt(ghParts[1]);
                g3 = Integer.parseInt(ghParts[2]);
            }

            urlWithVersion = jsonObject.getString("url");

        } catch (IOException e) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, e);
            isNewVersionAvailable = false;
        }

        if (g1 > v1 || g2 > v2 || g3 > v3) {
            isNewVersionAvailable = true;
        }

        return this.isNewVersionAvailable;
    }

    @FXML
    private void onCancel(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @FXML
    private void onOpen(ActionEvent event) {
        hostServices.showDocument(urlWithVersion);
        getTaskUtil().closePopup();
    }

}
