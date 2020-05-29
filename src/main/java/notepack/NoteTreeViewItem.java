package notepack;

import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;

public class NoteTreeViewItem {
    
    private Note note;
    private NoteStorageItem noteStorageItem;
    private String label;
    private boolean isRoot = false;
    
    public NoteTreeViewItem(String label) {
        this.label = label;
        this.isRoot = true;
    }
    
    public boolean isRoot() {
        return isRoot;
    }
    
    public NoteTreeViewItem(Note n, NoteStorageItem noteStorageItem) {
        this.note = n;
        this.noteStorageItem = noteStorageItem;
    }
    
    public NoteTreeViewItem(NoteStorageItem noteStorageItem) {
        this.noteStorageItem = noteStorageItem;
    }
    
    public String getLabel() {
        if (noteStorageItem == null) {
            return label;
        }
        
        return noteStorageItem.getName();
    }
    
    public Note getNote() {
        return note;
    }
    
    public NoteStorageItem getNoteStorageItem() {
        return noteStorageItem;
    }
    
}
