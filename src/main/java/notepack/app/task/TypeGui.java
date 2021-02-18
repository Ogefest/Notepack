package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.gui.TaskUtil;

public interface TypeGui {

    public void guiWork(TaskUtil taskUtil, App app) throws GuiNotReadyError;
    
}
