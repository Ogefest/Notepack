/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.task;

import notepack.app.domain.Notepad;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;

/**
 *
 * @author lg
 */
public interface TypeNotepad {
    public void notify(NotepadListener listener);
}
