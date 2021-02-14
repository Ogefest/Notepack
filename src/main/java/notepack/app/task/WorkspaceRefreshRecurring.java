package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;

public class WorkspaceRefreshRecurring extends BaseTask implements Task, TypeRecurring {

    @Override
    public void backgroundWork() {

        for (Workspace workspace : app.getAvailableWorkspaces()) {
            app.refreshWorkspace(workspace);
        }
    }

    @Override
    public int getInterval() {
        return 180;
    }

    private App app;

    public WorkspaceRefreshRecurring(App app) {
        this.app = app;
    }

}
