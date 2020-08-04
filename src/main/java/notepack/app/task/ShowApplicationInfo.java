package notepack.app.task;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import notepack.AboutDialogController;
import notepack.app.domain.App;
import notepack.app.domain.Task;

public class ShowApplicationInfo extends BaseTask implements Task, TypeGui {

    @Override
    public void dispatch() {
    }

    private HostServices hostServices;

    public ShowApplicationInfo(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void proceed(Stage parentStage, App app) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/AboutDialog.fxml"));

        Scene scene;
        try {
            Parent root = fxmlLoader.load();

            AboutDialogController ctrl = (AboutDialogController) fxmlLoader.getController();
            ctrl.hostServices = hostServices;

            scene = new Scene(root);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(parentStage.getScene().getWindow());
            stage.setTitle("About");
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException ex) {
            Logger.getLogger(AboutDialogController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
