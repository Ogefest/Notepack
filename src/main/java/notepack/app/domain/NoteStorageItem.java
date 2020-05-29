package notepack.app.domain;

import java.util.ArrayList;

public class NoteStorageItem {

    private ArrayList<NoteStorageItem> items = new ArrayList<NoteStorageItem>();
    private String path;
    private String name;
    private long size;
    private long modified;

    public NoteStorageItem(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public NoteStorageItem(String path, String name, long size, long modified) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.modified = modified;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDirectory() {
        return getPath().substring(0, getPath().length() - getName().length());
    }

    public long getSize() {
        return size;
    }

    public long getModified() {
        return modified;
    }

    public boolean isLeaf() {
        return items.size() == 0;
    }

    public void add(NoteStorageItem item) {
        items.add(item);
    }

    public ArrayList<NoteStorageItem> get() {
        return items;
    }

}
