package notepack.app.task;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

public class ShowNotesView extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        taskUtil.showNotesPane();

    }
}
