/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.domain.Note;

/**
 *
 * @author lg
 */
public interface NoteListener extends EventListener {
    
    public void onOpen(Note n);
    public void onClose(Note n);
    public void onChange(Note n);
    public void onSave(Note n);
    
}
