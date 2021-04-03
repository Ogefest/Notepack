package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.listener.WorkspaceListener;
import notepack.gui.TaskUtil;

public class WorkspaceClose implements Task,TypeGui, TypeWorkspace {

    private Workspace workspace;
    
    public WorkspaceClose(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(WorkspaceListener listener) {
        listener.onClose(workspace);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Tab tabToClose = taskUtil.getWorkspaceTab(workspace);
        if (tabToClose == null) {
            return;
        }

        TabPane container = taskUtil.getWorkspaceContainer();
        container.getTabs().remove(tabToClose);

    }
}
