package notepack.app.task;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NotepadListener;

import java.util.ArrayList;

public class RefreshNotepadRecurring extends BaseTask implements Task, TypeRecurring {

    @Override
    public void backgroundWork() {

        for (Notepad notepad : app.getAvailableNotepads()) {
            app.refreshNotepad(notepad);
        }
    }

    @Override
    public int getInterval() {
        return 180;
    }

    private App app;

    public RefreshNotepadRecurring(App app) {
        this.app = app;
    }

}
