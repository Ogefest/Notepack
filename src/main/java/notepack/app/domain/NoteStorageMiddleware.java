package notepack.app.domain;

import notepack.app.domain.exception.MessageError;
import notepack.processor.NoteProcessor;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoteStorageMiddleware implements NoteStorage {

    private NoteStorage noteStorage;
    private ArrayList<NoteProcessor> beforeSave = new ArrayList<>();
    private ArrayList<NoteProcessor> afterSave = new ArrayList<>();
    private ArrayList<NoteProcessor> beforeLoad = new ArrayList<>();
    private ArrayList<NoteProcessor> afterLoad = new ArrayList<>();

    public NoteStorageMiddleware(NoteStorage noteStorage) {
        this.noteStorage = noteStorage;
    }

    public void registerBeforeSave(NoteProcessor p) {
        beforeSave.add(p);
    }

    public void registerAfterSave(NoteProcessor p) {
        afterSave.add(p);
    }

    public void registerBeforeLoad(NoteProcessor p) {
        beforeLoad.add(p);
    }

    public void registerAfterLoad(NoteProcessor p) {
        afterLoad.add(p);
    }

    public NoteStorage getParentStorage() {
        return noteStorage;
    }

    @Override
    public void setConfiguration(NoteStorageConfiguration nsc) {
        noteStorage.setConfiguration(nsc);
    }

    @Override
    public NoteStorageConfiguration getConfiguration() {
        return noteStorage.getConfiguration();
    }

    @Override
    public void saveContent(byte[] content, String path) throws MessageError {

        for (NoteProcessor p : beforeSave) {
            if (p.isAvailableForPath(path)) {
                content = p.run(content);
            }
        }

        noteStorage.saveContent(content, path);

        for (NoteProcessor p : afterSave) {
            if (p.isAvailableForPath(path)) {
                content = p.run(content);
            }
        }
    }

    @Override
    public byte[] loadContent(String path) throws MessageError {
        byte[] content = noteStorage.loadContent(path);

        for (NoteProcessor p : afterLoad) {
            if (p.isAvailableForPath(path)) {
                content = p.run(content);
            }
        }

        return content;
    }

    @Override
    public NoteStorageItem getItemsInStorage() {
        return noteStorage.getItemsInStorage();
    }

    @Override
    public void refreshItemsInStorage() throws MessageError {
        noteStorage.refreshItemsInStorage();
    }

    @Override
    public void rename(String oldPath, String newPath) {
        noteStorage.rename(oldPath, newPath);
    }

    @Override
    public void delete(String path) {
        noteStorage.delete(path);
    }

    @Override
    public String getBasePath() {
        return noteStorage.getBasePath();
    }

    @Override
    public void setMeta(JSONObject content, String namespace) throws MessageError {
        noteStorage.setMeta(content, namespace);
    }

    @Override
    public JSONObject getMeta(String namespace) throws MessageError {
        return noteStorage.getMeta(namespace);
    }

    @Override
    public void deleteMeta(String noteIdent) throws MessageError {
        noteStorage.deleteMeta(noteIdent);
    }

}
