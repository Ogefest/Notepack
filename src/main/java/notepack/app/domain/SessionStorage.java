package notepack.app.domain;

import java.util.ArrayList;

public interface SessionStorage {
    public ArrayList<Workspace> getAvailableWorkspaces();
    public void setWorkspaceList(ArrayList<Workspace> workspaces);
    public void addWorkspace(Workspace workspace);
    public void removeWorkspace(Workspace workspace);
    
    public ArrayList<Note> getLastNotes();
    public void setNoteList(ArrayList<Note> notes);
    public void addNote(Note note);
    public void removeNote(Note note);

}
