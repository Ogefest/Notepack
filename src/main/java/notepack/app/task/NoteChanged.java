package notepack.app.task;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

public class NoteChanged implements Task,TypeNote,TypeGui {
    
    private Note note;
    
    public NoteChanged(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onChange(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        Tab t = taskUtil.getNoteTab(note);
        t.setText(note.getName());
        Label l = (Label) t.getGraphic();
        l.getStyleClass().addAll("icon-base","mi-asterisk");
    }
}
