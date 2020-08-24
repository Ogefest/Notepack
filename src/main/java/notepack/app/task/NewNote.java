package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class NewNote implements Task,TypeNote {
    
    private Note note;
    
    public NewNote(Note note) {
        this.note = note;
    }

    @Override
    public void dispatch() {
        note.setContents(new byte[0]);
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }
    
}
