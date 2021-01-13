package notepack.app.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.exception.MessageError;
import notepack.noterender.Render;
import org.json.JSONObject;

public class Filesystem implements NoteStorage {

    private NoteStorageConfiguration nsc;

    private ArrayList<NoteStorageItem> items = new ArrayList<>();

    private NoteStorageItem rootItem;

    private int deep = 0;

    private String metadataPath = ".metadata.json";
    private JSONObject meta;

    public Filesystem() {
        /*
        this constructor is in use when session is loaded
         */
        nsc = new NoteStorageConfiguration();
        nsc.set("directory", System.getProperty("java.io.tmpdir"));

        loadMetaFromStorage();
    }
//
//    public Filesystem(String path) {
//        nsc = new NoteStorageConfiguration();
//        nsc.set("directory", path);
//    }

    public Filesystem(NoteStorageConfiguration nsc) {
        this.nsc = nsc;

        loadMetaFromStorage();
    }

    @Override
    public String getBasePath() {
        return nsc.get("directory");
    }

    @Override
    synchronized public void setMeta(JSONObject content, String namespace) throws MessageError {
        meta.put(namespace, content);

        saveMetaToStorage();
    }

    @Override
    public JSONObject getMeta(String noteIdent) throws MessageError {
        if (meta.has(noteIdent)) {
            return meta.getJSONObject(noteIdent);
        }
        return new JSONObject();
    }

    synchronized private void saveMetaToStorage() {
        byte[] bytesToSave = meta.toString().getBytes();

        try {
            saveContent(bytesToSave, getBasePath() + File.separator + metadataPath);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    private void loadMetaFromStorage() {
        File f = new File(getBasePath() + File.separator + metadataPath);
        if (!f.exists()) {
            meta = new JSONObject();
            return;
        }

        byte[] content = new byte[0];
        try {
            content = loadContent(getBasePath() + File.separator + metadataPath);
            String cnt = new String(content);
            meta = new JSONObject(cnt);
        } catch (MessageError messageError) {
            meta = new JSONObject();
        }

    }

    @Override
    public byte[] loadContent(String path) throws MessageError {

        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new MessageError(e.getMessage(), e);
        }
    }

    @Override
    public NoteStorageItem getItemsInStorage() {

        if (rootItem == null) {
            File f = new File(getBasePath());
            rootItem = new NoteStorageItem(getBasePath(), f.getName());
        }

        return rootItem;
    }

    @Override
    public void refreshItemsInStorage() {
        File f = new File(getBasePath());
        rootItem = new NoteStorageItem(getBasePath(), f.getName());
        rootItem = addItems(rootItem, getBasePath(), 0);
    }

    private NoteStorageItem addItems(NoteStorageItem parent, String startPath, int deep) {
        if (deep > 5) {
            return parent;
        }
        
        ArrayList<String> supportedExtensions = Render.getSupportedExtensions();

        File f = new File(startPath);
        for (String p : f.list()) {
            if (p.substring(0, 1).equals(".")) {
                continue;
            }

            File ff = new File(startPath + File.separator + p);
            long lastModified = 0;
            try {
                FileTime ft = Files.getLastModifiedTime(ff.toPath());
                lastModified = ft.toMillis();
            } catch (IOException ex) {
                Logger.getLogger(Filesystem.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            String extension = "";

            int i = p.lastIndexOf('.');
            int pos = Math.max(p.lastIndexOf(File.separator), p.lastIndexOf('\\'));

            if (i > pos) {
                extension = p.substring(i + 1);
            }

            if (supportedExtensions.contains(extension)) {

                parent.add(new NoteStorageItem(startPath + File.separator + p, p, ff.length(), lastModified));

            } else if (ff.isDirectory()) {

                NoteStorageItem newDirectoryParent = new NoteStorageItem(startPath + File.separator + p, p, ff.length(), lastModified);

                newDirectoryParent = addItems(newDirectoryParent, startPath + File.separator + p, deep + 1);
                if (newDirectoryParent.get().size() > 0) {
                    parent.add(newDirectoryParent);
                }
            }
        }

        return parent;
    }

    @Override
    public void saveContent(byte[] content, String path) throws MessageError {

        try {
            Files.write(Paths.get(path), content);
        } catch (IOException ex) {
            throw new MessageError(ex.getMessage(), ex);
        }
    }

    @Override
    public void setConfiguration(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
        loadMetaFromStorage();
    }

    @Override
    public NoteStorageConfiguration getConfiguration() {
        return nsc;
    }

    @Override
    public void rename(String oldPath, String newPath) {

        try {
            Files.move(Paths.get(oldPath), Paths.get(newPath), REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Filesystem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void delete(String path) {
        try {
            Files.delete(Paths.get(path));
        } catch (IOException ex) {
            Logger.getLogger(Filesystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
