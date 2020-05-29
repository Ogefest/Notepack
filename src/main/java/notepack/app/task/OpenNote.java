package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class OpenNote implements Task,TypeNote {
    
    private Note note;
    
    public OpenNote(Note note) {
        this.note = note;
    }

    @Override
    public void dispatch() {
        note.readFromStorage();
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }
    
}
