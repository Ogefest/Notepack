/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 *
 * @author lg
 */
public class Notepad {
    
    private NoteStorage storage;
    private String name;
    private String ident;
    private HashMap<String, String> params = new HashMap<>();
    
    public Notepad(NoteStorage storage) {
        this.storage = storage;
        this.ident = UUID.randomUUID().toString();
        params.put("name", ident);
    }
    
    public Notepad(NoteStorage storage, String name) {
        this.storage = storage;
        this.ident = UUID.randomUUID().toString();
        params.put("name", name);
    }
    
    public Notepad(NoteStorage storage, String name, String ident) {
        this.storage = storage;
        this.name = name;
        this.ident = ident;
        
        params.put("name", name);
    }
    
    public String getName() {
        return params.get("name");
    }
    
    public NoteStorage getStorage() {
        return storage;
    }
    
    public String getIdent() {
        return ident;
    }
    
    public HashMap<String, String> getParams() {
        return params;
    }
    
    public void setParam(String key, String value) {
        params.put(key, value);
        if (key.equals("name")) {
            name = value;
        }
    }
    
    
}
