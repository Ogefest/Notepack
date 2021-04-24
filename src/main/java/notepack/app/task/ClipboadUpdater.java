package notepack.app.task;

import javafx.scene.input.Clipboard;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

public class ClipboadUpdater extends BaseTask implements Task, TypeGui, TypeRecurring {
    @Override
    public void backgroundWork() throws MessageError {

    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) throws GuiNotReadyError {

        if (app.getSettings().get("clipboard-manager", "1").equals("0")) {
            return;
        }

        Clipboard clipboard = Clipboard.getSystemClipboard();
        String currentContents = clipboard.getString();
        if (currentContents != null) {
            app.getClipboardManager().add(currentContents);
        }

    }

    @Override
    public int getInterval() {
        return 5;
    }
}
