/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and loadContent the template in the editor.
 */
package notepack.app.task;

import notepack.app.domain.MessageBus;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.Task;
import notepack.app.event.NoteChanged;
import notepack.app.event.NoteOpened;
import notepack.app.listener.NoteListener;

/**
 *
 * @author lg
 */
public class OpenNote implements Task,TypeNote {
    
    private Note note;
    
    public OpenNote(Note note) {
        this.note = note;
    }

    @Override
    public void dispatch() {
        note.readFromStorage();
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }
    
}
