/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.engine;

import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;

/**
 *
 * @author lg
 */
public interface EngineController {
    public void setStorage(NoteStorage storage);
    public NoteStorage getStorage();
}
