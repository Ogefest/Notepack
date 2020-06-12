package notepack.app.storage;

import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;

public class GpgEncrypted implements NoteStorage {

    private NoteStorage storage;
    private String privateKey;
    private String publicKey;

    public GpgEncrypted(NoteStorage noteStorage) {
        this.storage = noteStorage;
    }

    public void setKeysPath(String publicKey, String privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public void setConfiguration(NoteStorageConfiguration nsc) {
        storage.setConfiguration(nsc);
    }

    @Override
    public NoteStorageConfiguration getConfiguration() {
        return storage.getConfiguration();
    }

    @Override
    public void saveContent(String content, String path) {
        storage.saveContent(content, path);
    }

    @Override
    public String loadContent(String path) {
        return "GPG" + storage.loadContent(path);
    }

    @Override
    public NoteStorageItem getItemsInStorage() {
        return storage.getItemsInStorage();
    }

    @Override
    public void refreshItemsInStorage() {
        storage.refreshItemsInStorage();
    }

    @Override
    public void rename(String oldPath, String newPath) {
        storage.rename(oldPath, newPath);
    }

    @Override
    public void delete(String path) {
        storage.delete(path);
    }

    @Override
    public String getBasePath() {
        return storage.getBasePath();
    }

}
