/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author lg
 */
public class Notepad {
    
    private NoteStorage storage;
    private String name;
    private String ident;
    
    public Notepad(NoteStorage storage, String name) {
        this.storage = storage;
        this.name = name;
        this.ident = UUID.randomUUID().toString();
    }
    
    public Notepad(NoteStorage storage, String name, String ident) {
        this.storage = storage;
        this.name = name;
        this.ident = ident;
    }
    
    public String getName() {
        return name;
    }
    
    public NoteStorage getStorage() {
        return storage;
    }
    
    public String getIdent() {
        return ident;
    }
    
}
