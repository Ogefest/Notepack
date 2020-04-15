/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and loadContent the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author lg
 */
public interface NoteStorage {
    
    public void setConfiguration(NoteStorageConfiguration nsc);
    public NoteStorageConfiguration getConfiguration();
    
    public void saveContent(String content, String path);
    public String loadContent(String path);
    public NoteStorageItem getItemsInStorage();
    public void refreshItemsInStorage();
    public void rename(String oldPath, String newPath);
    public void delete(String path);
    
}
