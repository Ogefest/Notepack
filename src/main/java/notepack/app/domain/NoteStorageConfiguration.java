/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author lg
 */
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
