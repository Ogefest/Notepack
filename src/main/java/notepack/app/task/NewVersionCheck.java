package notepack.app.task;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import notepack.MainViewController;
import notepack.NewVersionController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewVersionCheck extends BaseTask implements Task, TypeRecurring, TypeGui {

    private HostServices hostServices;

    public NewVersionCheck(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public int getInterval() {
        return 86400;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) throws GuiNotReadyError {
        AnchorPane pane;

        String lastCheckTimestamp = app.getSettings().get("last-check-timestamp", "0");
        long lastCheckTs = Long.parseLong(lastCheckTimestamp);
        long currentTs = System.currentTimeMillis() / 1000L;
        if (lastCheckTs + 3600 > currentTs) {
            return;
        }
        app.getSettings().set("last-check-timestamp", Long.toString(currentTs));

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/NewVersionPopup.fxml"));
            pane = loader.load();

            NewVersionController ctrl = loader.getController();
            ctrl.setTaskUtil(taskUtil);
            ctrl.setHostServices(hostServices);

            if (ctrl.isNewVersionAvailable()) {
                taskUtil.openPopup(pane);
            }

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
