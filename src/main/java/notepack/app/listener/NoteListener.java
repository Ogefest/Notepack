package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.domain.Note;

public interface NoteListener extends EventListener {
    
    public void onOpen(Note n);
    public void onClose(Note n);
    public void onChange(Note n);
    public void onSave(Note n);
    
}
