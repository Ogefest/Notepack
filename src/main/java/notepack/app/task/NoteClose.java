package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

public class NoteClose implements Task,TypeNote,TypeGui {

    private Note note;

    public NoteClose(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onClose(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        this.note = taskUtil.getCurrentNote();
    }
}
