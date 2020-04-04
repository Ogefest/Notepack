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
    private NoteStorage storage;
    
    public Note(NoteStorage storage) {
        this.path = null;
        this.storage = storage;
    }
    
    public Note(String path, NoteStorage storage) {
        this.path = path;
        this.storage = storage;
    }
    
    public String getName() {
        if (path == null) {
            return "";
        }
        
        File f = new File(path);
        return f.getName();
    }
    
    public NoteStorage getStorage() {
        return storage;
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
            content = storage.loadContent(path);
        }
    }
    
    public void saveToStorage() {
        storage.saveContent(content, path);
    }
    
    public String toString() {
        return getName();
    }
    
}
