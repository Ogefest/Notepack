package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.gui.TabNotepad;
import notepack.gui.TaskUtil;

public class NotepadSelectNote implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public enum TYPE {
        INFO, ERROR
    }

    private Note note;

    public NotepadSelectNote(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        Notepad notepad = note.getNotepad();
        TabPane container = taskUtil.getNotepadContainer();
        Tab notepadTab = taskUtil.getNotepadTab(notepad);
        NotebookTabController ctrl = (NotebookTabController) notepadTab.getUserData();

        container.getSelectionModel().select(notepadTab);
        ctrl.selectNote(note);

    }

}
