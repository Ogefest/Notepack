package notepack.app.domain;

import java.util.ArrayList;
import java.util.List;

public interface SessionStorage {
    public ArrayList<Notepad> getAvailableNotepads();
    public void setNotepadList(ArrayList<Notepad> notepads);
    public void addNotepad(Notepad notepad);
    public void removeNotepad(Notepad notepad);
    
    public ArrayList<Note> getLastNotes();
    public void setNoteList(ArrayList<Note> notes);
    public void addNote(Note note);
    public void removeNote(Note note);

}
