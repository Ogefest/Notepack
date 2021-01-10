package notepack.app.task;

import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.listener.GuiListener;
import notepack.gui.TaskUtil;

public interface TypeGui {
    
    public void guiWork(TaskUtil taskUtil, App app);
    
}
