package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import notepack.MainViewController;
import notepack.WorkspaceArchiveController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceArchivePopup implements Task, TypeGui {

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/WorkspaceArchivePopup.fxml"));

        Node root;
        try {
            root = fxmlLoader.load();

            WorkspaceArchiveController ctrl = fxmlLoader.getController();

            ctrl.setTaskUtil(taskUtil);
            ctrl.setApp(app);

            taskUtil.openPopup(root);


        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
