package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

public class NoteRename implements Task,TypeNote {
    
    private Note note;
    private String newPath;
    
    public NoteRename(Note n, String newPath) {
        this.note = n;
        this.newPath = newPath;
    }

    @Override
    public void backgroundWork() {
        note.getStorage().rename(note.getPath(), newPath);
        note.setPath(newPath);
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onChange(note);
    }
    
}
