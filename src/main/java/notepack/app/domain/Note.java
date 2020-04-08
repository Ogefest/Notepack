/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.File;

/**
 *
 * @author lg
 */
public class Note {
    
    private String content;
    private String path = null;
//    private NoteStorage storage;
    private Notepad notepad;
    
    public Note(Notepad notepad) {
        this.path = null;
        this.notepad = notepad;
//        this.storage = storage;
    }
    
    public Note(String path, Notepad notepad) {
        this.path = path;
        this.notepad = notepad;
    }
    
    public String getName() {
        if (path == null) {
            return "New note";
        }
        
        File f = new File(path);
        return f.getName();
    }
    
    public Notepad getNotepad() {
        return notepad;
    }
    
    public NoteStorage getStorage() {
        return notepad.getStorage();
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public void setContents(String content) {
        this.content = content;
    }
    
    public String getContent() {
        return content;
    }
    
    public void readFromStorage() {
        if (path == null) {
            content = "";
        } else {
            content = notepad.getStorage().loadContent(path);
        }
    }
    
    public void saveToStorage() {
        notepad.getStorage().saveContent(content, path);
    }
    
    public String toString() {
        return getName();
    }
    
}
