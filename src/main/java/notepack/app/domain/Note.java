package notepack.app.domain;

import java.io.File;

public class Note {
    
    private String content;
    private String path = null;
    private String name = null;
    private Notepad notepad;
    private String ident;
    private boolean isSaved = true;
    
    public Note(Notepad notepad) {
        this.ident = notepad.getIdent() + path;
        this.path = null;
        this.notepad = notepad;
    }
    
    public Note(String path, Notepad notepad, String name) {
        this.ident = notepad.getIdent() + path;
        this.path = path;
        this.notepad = notepad;
        this.name = name;
    }
    
    public String getIdent() {
        return ident;
    }
    
    public String getName() {
        if (path == null && name == null) {
            return "New note";
        }
        if (name != null) {
            return name;
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
        isSaved = false;
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
        setPath(path);
        isSaved = true;
    }
    
    public boolean isSaved() {
        return isSaved;
    }
    
    public String toString() {
        return getName();
    }
    
}
