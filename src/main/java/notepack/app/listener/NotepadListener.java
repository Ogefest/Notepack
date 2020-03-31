/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.domain.Notepad;

/**
 *
 * @author lg
 */
public interface NotepadListener extends EventListener {
    public void onOpen(Notepad notepad);
    public void onClose(Notepad notepad);
    public void onNotesListUpdated(Notepad notepad);
}
