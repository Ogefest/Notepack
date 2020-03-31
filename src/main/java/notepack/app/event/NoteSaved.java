/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.event;

import notepack.app.domain.Event;
import notepack.app.domain.Note;

/**
 *
 * @author lg
 */
public class NoteSaved implements Event {
    
    private Note n;
    
    public NoteSaved(Note n) {
        this.n = n;
    }
    
    public Note getNote() {
        return n;
    }
}
