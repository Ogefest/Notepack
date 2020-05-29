package notepack.app.domain;

import java.util.ArrayList;

public interface SessionStorage {
    public ArrayList<Notepad> getAvailableNotepads();
    public void addNotepad(Notepad notepad);
    public void removeNotepad(Notepad notepad);
    
    public ArrayList<Note> getLastNotes();
    public void addNote(Note note);
    public void removeNote(Note note);
}
