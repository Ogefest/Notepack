package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class SaveNote implements Task,TypeNote {

    private Note note;

    public SaveNote(Note note) {
        this.note = note;
    }

    @Override
    public void dispatch() {
        note.saveToStorage();
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onSave(note);
    }

}
