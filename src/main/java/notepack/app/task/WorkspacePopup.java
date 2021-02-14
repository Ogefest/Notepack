package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import notepack.MainViewController;
import notepack.WorkspaceCreateController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspacePopup implements Task, TypeGui {

    private Workspace workspace;

    public WorkspacePopup(Workspace workspace) {
        this.workspace = workspace;
    }

    public WorkspacePopup() {

    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/WorkspaceConfigurationPopup.fxml"));

        Node root;
        try {
            root = fxmlLoader.load();

            WorkspaceCreateController ctrl = fxmlLoader.getController();

            ctrl.setTaskUtil(taskUtil);
            if (this.workspace != null) {
                ctrl.setWorkspaceToEdit(workspace);
                ctrl.setWorkspaceCreateCallback(workspace -> {
                    app.closeWorkspace(workspace);
                    app.openWorkspace(workspace);
                });
            } else {
                ctrl.setWorkspaceCreateCallback(workspace -> app.openWorkspace(workspace));
            }

            taskUtil.openPopup(root);


        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
