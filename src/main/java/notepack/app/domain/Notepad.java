/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;

/**
 *
 * @author lg
 */
public class Notepad {
    
    private NoteStorage storage;
    private String name;
    
    public Notepad(NoteStorage storage, String name) {
        this.storage = storage;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public NoteStorage getStorage() {
        return storage;
    }
    
    
}
