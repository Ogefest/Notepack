package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class ChangedNote implements Task,TypeNote {
    
    private Note note;
    
    public ChangedNote(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onChange(note);
    }
    
}
