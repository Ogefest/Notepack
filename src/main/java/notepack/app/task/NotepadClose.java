package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.listener.NotepadListener;
import notepack.gui.TaskUtil;

public class NotepadClose implements Task,TypeGui,TypeNotepad {

    private Notepad notepad;
    
    public NotepadClose(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NotepadListener listener) {
        listener.onClose(notepad);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Tab tabToClose = taskUtil.getNotepadTab(notepad);
        TabPane container = taskUtil.getNotepadContainer();
        container.getTabs().remove(tabToClose);

    }
}
