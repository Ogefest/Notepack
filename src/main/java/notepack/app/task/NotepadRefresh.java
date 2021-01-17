package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NotepadListener;
import notepack.gui.TabNotepad;
import notepack.gui.TaskUtil;

public class NotepadRefresh extends BaseTask implements Task,TypeNotepad,TypeGui {

    private Notepad notepad;
    
    public NotepadRefresh(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void backgroundWork() throws MessageError {
        notepad.getStorage().refreshItemsInStorage();
    }

    @Override
    public void notify(NotepadListener listener) {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Tab notepadTab = taskUtil.getNotepadTab(notepad);
        NotebookTabController ctrl = (NotebookTabController) notepadTab.getUserData();
        ctrl.refreshTreeView();

        addTaskToQueue(new NotepadSelectNote(taskUtil.getCurrentNote()));
    }
}
