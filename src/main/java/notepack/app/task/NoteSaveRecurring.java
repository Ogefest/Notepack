package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

public class NoteSaveRecurring extends BaseTask implements Task, TypeRecurring, TypeGui {
    @Override
    public void backgroundWork() throws MessageError {

    }

    @Override
    public int getInterval() {
        return 30;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        TabPane tabs = taskUtil.getNotesContainer();
        for (Tab t : tabs.getTabs()) {
            NoteRenderController ctrl = (NoteRenderController) t.getUserData();
            Note n = ctrl.getNote();
            if (!n.isSaved()) {
                app.addTask(new NoteSave(n));
            }
        }
    }
}
