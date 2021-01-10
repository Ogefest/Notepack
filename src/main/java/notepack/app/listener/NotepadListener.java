package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.domain.Notepad;

public interface NotepadListener extends EventListener {
    public void onOpen(Notepad notepad);
    public void onClose(Notepad notepad);
}
