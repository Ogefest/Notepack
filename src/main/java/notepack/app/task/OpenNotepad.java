package notepack.app.task;

import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.listener.NotepadListener;

public class OpenNotepad implements Task,TypeNotepad {

    private Notepad notepad;
    
    public OpenNotepad(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NotepadListener listener) {
        listener.onOpen(notepad);
    }
    
}
