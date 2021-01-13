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
        String oldNoteIdent = note.getIdent();
        note.getStorage().rename(note.getPath(), newPath);
        note.setPath(newPath);
        String newNoteIdent = note.getIdent();
        note.getMeta().changeNamespace(oldNoteIdent, newNoteIdent);
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onChange(note);
    }
    
}
