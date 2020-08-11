package notepack.app.task;

import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NotepadListener;

public class RefreshNotepad implements Task,TypeNotepad {

    private Notepad notepad;
    
    public RefreshNotepad(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void dispatch() throws MessageError {
        notepad.getStorage().refreshItemsInStorage();
    }

    @Override
    public void notify(NotepadListener listener) {
        listener.onNotesListUpdated(notepad);
    }
    
}
