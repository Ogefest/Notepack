package notepack.app.domain;

import java.util.HashMap;

public class NoteStorageConfiguration {
    
    private HashMap<String, String> params = new HashMap<>();
    
    public void set(String key, String value) {
        params.put(key, value);
    }
    
    public String get(String key) {
        return params.get(key);
    }
    
    public HashMap<String, String> getAll() {
        return params;
    }
}
