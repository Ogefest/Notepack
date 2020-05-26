/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and loadContent the template in the editor.
 */
package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;

/**
 *
 * @author lg
 */
public class DeleteNote implements Task,TypeNote {
    
    private Note note;
    
    public DeleteNote(Note n) {
        this.note = n;
    }

    @Override
    public void dispatch() {
        note.getStorage().delete(note.getPath());
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onClose(note);
    }
    
}
