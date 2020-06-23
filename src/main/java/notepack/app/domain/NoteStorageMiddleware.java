package notepack.app.domain;

import java.util.ArrayList;
import notepack.app.domain.exception.MessageError;
import notepack.processor.NoteProcessor;

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
    public void saveContent(String content, String path) throws MessageError {

        for (NoteProcessor p : beforeSave) {
            content = p.run(content);
        }

        noteStorage.saveContent(content, path);

        for (NoteProcessor p : afterSave) {
            content = p.run(content);
        }
    }

    @Override
    public String loadContent(String path) throws MessageError {
        String content = noteStorage.loadContent(path);

        for (NoteProcessor p : afterLoad) {
            content = p.run(content);
        }

        return content;
    }

    @Override
    public NoteStorageItem getItemsInStorage() {
        return noteStorage.getItemsInStorage();
    }

    @Override
    public void refreshItemsInStorage() {
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

}
