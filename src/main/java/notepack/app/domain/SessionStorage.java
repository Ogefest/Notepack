/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author lg
 */
public interface SessionStorage {
    public ArrayList<Notepad> getAvailableNotepads();
    public void addNotepad(Notepad notepad);
    public void removeNotepad(Notepad notepad);
    
    public ArrayList<Note> getLastNotes();
    public void addNote(Note note);
    public void removeNote(Note note);
}
