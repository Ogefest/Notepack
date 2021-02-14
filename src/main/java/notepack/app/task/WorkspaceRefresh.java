package notepack.app.task;

import javafx.scene.control.Tab;
import notepack.WorkspaceTabController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.WorkspaceListener;
import notepack.gui.TaskUtil;

public class WorkspaceRefresh extends BaseTask implements Task, TypeWorkspace,TypeGui {

    private Workspace workspace;
    
    public WorkspaceRefresh(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void backgroundWork() throws MessageError {
        workspace.getStorage().refreshItemsInStorage();
    }

    @Override
    public void notify(WorkspaceListener listener) {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Tab notepadTab = taskUtil.getWorkspaceTab(workspace);
        WorkspaceTabController ctrl = (WorkspaceTabController) notepadTab.getUserData();
        ctrl.refreshTreeView();

        addTaskToQueue(new WorkspaceSelectNote(taskUtil.getCurrentNote()));
    }
}
