/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.util.ArrayList;

/**
 *
 * @author lg
 */
public class NoteTreeItem {
    
    private ArrayList<NoteTreeItem> items = new ArrayList<NoteTreeItem>();
    private String path;
    private String name;
    
    public NoteTreeItem(String path, String name) {
        this.path = path;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    public boolean isLeaf() {
        return items.size() == 0;
    }
    
    public void add(NoteTreeItem item) {
        items.add(item);
    }
    
    public ArrayList<NoteTreeItem> get() {
        return items;
    }
    
}
