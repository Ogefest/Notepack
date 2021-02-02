package notepack.app.task;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import notepack.AboutDialogController;
import notepack.MainViewController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShowApplicationInfo extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    private HostServices hostServices;

    public ShowApplicationInfo(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {


        AnchorPane pane;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/AboutPopup.fxml"));
            pane = loader.load();

            AboutDialogController ctrl = loader.getController();
            ctrl.setTaskUtil(taskUtil);

            taskUtil.openPopup(pane);

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
