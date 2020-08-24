package notepack.app.domain;

import java.io.File;
import java.util.Date;
import notepack.app.domain.exception.MessageError;

public class Note {

    private byte[] content;
    private String path = null;
    private String name = null;
    private Notepad notepad;
    private String ident;
    private boolean isSaved = true;

    public Note(Notepad notepad) {
        this.ident = notepad.getIdent() + Long.toString(new Date().getTime());
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

    public void setContents(byte[] content) {
        this.content = content;
        isSaved = false;
    }

    public byte[] getContent() {
        return content;
    }

    public void readFromStorage() throws MessageError {
        if (path == null) {
            content = new byte[0];
        } else {
            content = notepad.getStorage().loadContent(path);
        }
    }

    public void saveToStorage() throws MessageError {
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
