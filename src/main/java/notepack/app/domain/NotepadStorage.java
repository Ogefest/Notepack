/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author lg
 */
public interface NotepadStorage {
    public ArrayList<Notepad> getAvailable();
    public void add(Notepad notepad);
    public void remove(Notepad notepad);
}
