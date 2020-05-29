package notepack.engine;

import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;

public interface EngineController {
    public void setStorage(NoteStorage storage);
    public NoteStorage getStorage();
}
