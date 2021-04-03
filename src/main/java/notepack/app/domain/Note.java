package notepack.app.domain;

import notepack.app.domain.exception.MessageError;

import java.io.File;
import java.util.Date;

public class Note {

    private byte[] content;
    private String path = null;
    private String name = null;
    private Workspace workspace;
    private NoteMeta meta;
    private String ident;
    private boolean isSaved = true;

    public Note(Workspace workspace) {
        this.ident = workspace.getIdent() + Long.toString(new Date().getTime());
        this.path = null;
        this.workspace = workspace;
    }

    public Note(String path, Workspace workspace, String name) {
        this.ident = workspace.getIdent() + path;
        this.path = path;
        this.workspace = workspace;
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

    public Workspace getWorkspace() {
        return workspace;
    }

    public NoteStorage getStorage() {
        return workspace.getStorage();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        ident = workspace.getIdent() + path;
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
            content = workspace.getStorage().loadContent(path);
        }
    }

    public void saveToStorage() throws MessageError {
        workspace.getStorage().saveContent(content, path);
        setPath(path);
        isSaved = true;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public String toString() {
        return getName();
    }

    public NoteMeta getMeta() {
        if (meta == null) {
            meta = workspace.getMetaForNote(this);
        }

        return meta;
    }

}
