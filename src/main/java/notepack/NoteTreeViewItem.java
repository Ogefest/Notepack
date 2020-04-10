/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;

/**
 *
 * @author lg
 */
public class NoteTreeViewItem {
    
    private Note note;
    private NoteStorageItem noteStorageItem;
    
    public NoteTreeViewItem(Note n, NoteStorageItem noteStorageItem) {
        this.note = n;
        this.noteStorageItem = noteStorageItem;
    }
    
    public NoteTreeViewItem(NoteStorageItem noteStorageItem) {
        this.noteStorageItem = noteStorageItem;
    }
    
    public String getLabel() {
        return noteStorageItem.getName();
    }
    
    public Note getNote() {
        return note;
    }
    
    public NoteStorageItem getNoteStorageItem() {
        return noteStorageItem;
    }
    
}
