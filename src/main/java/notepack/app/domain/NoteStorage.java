package notepack.app.domain;

import notepack.app.domain.exception.MessageError;
import org.json.JSONObject;

public interface NoteStorage {

    public void setConfiguration(NoteStorageConfiguration nsc);

    public NoteStorageConfiguration getConfiguration();

    public void saveContent(byte[] content, String path) throws MessageError;

    public byte[] loadContent(String path) throws MessageError;

    public NoteStorageItem getItemsInStorage();

    public void refreshItemsInStorage() throws MessageError;

    public void rename(String oldPath, String newPath);

    public void delete(String path);

    public String getBasePath();

    public void setMeta(JSONObject content, String noteIdent) throws MessageError;

    public JSONObject getMeta(String noteIdent) throws MessageError;

    public void deleteMeta(String noteIdent) throws MessageError;

}
