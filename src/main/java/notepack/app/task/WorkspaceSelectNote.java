package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.WorkspaceTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.gui.TaskUtil;

public class WorkspaceSelectNote implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public enum TYPE {
        INFO, ERROR
    }

    private Note note;

    public WorkspaceSelectNote(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Workspace workspace = note.getWorkspace();
        TabPane container = taskUtil.getWorkspaceContainer();
        Tab workspaceTab = taskUtil.getWorkspaceTab(workspace);
        WorkspaceTabController ctrl = (WorkspaceTabController) workspaceTab.getUserData();

        container.getSelectionModel().select(workspaceTab);
        ctrl.selectNote(note);

    }

}
