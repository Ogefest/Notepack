package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.listener.WorkspaceListener;
import notepack.gui.TaskUtil;

public class WorkspaceArchive implements Task, TypeGui, TypeWorkspace {

    private Workspace workspace;

    public WorkspaceArchive(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(WorkspaceListener listener) {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        workspace.setParam("is_archived", "1");
        Tab tabToClose = taskUtil.getWorkspaceTab(workspace);
        TabPane container = taskUtil.getWorkspaceContainer();
        container.getTabs().remove(tabToClose);

    }
}
