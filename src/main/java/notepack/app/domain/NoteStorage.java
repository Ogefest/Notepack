package notepack.app.domain;

public interface NoteStorage {
    
    public void setConfiguration(NoteStorageConfiguration nsc);
    public NoteStorageConfiguration getConfiguration();
    
    public void saveContent(String content, String path);
    public String loadContent(String path);
    public NoteStorageItem getItemsInStorage();
    public void refreshItemsInStorage();
    public void rename(String oldPath, String newPath);
    public void delete(String path);
    public String getBasePath();
    
}
