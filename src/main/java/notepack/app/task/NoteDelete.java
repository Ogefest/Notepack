package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class NoteDelete implements Task,TypeNote {
    
    private Note note;
    
    public NoteDelete(Note n) {
        this.note = n;
    }

    @Override
    public void backgroundWork() {
        note.getStorage().delete(note.getPath());
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onClose(note);
    }
    
}
