package notepack.app.domain;

import java.util.ArrayList;

public interface NoteMetaStorage {
    public String getValue(String key);
    public void setValue(String key, String value);
    public ArrayList<String> getArray(String key);
    public void setArray(String key, ArrayList<String> value);
}
