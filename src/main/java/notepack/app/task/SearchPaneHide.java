package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

public class SearchPaneHide implements Task, TypeGui {
    @Override
    public void guiWork(TaskUtil taskUtil, App app) throws GuiNotReadyError {
        taskUtil.hideSearchPane();
        taskUtil.showNotesPane();
    }

    @Override
    public void backgroundWork() throws MessageError {
    }
}
