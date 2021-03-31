package notepack.app.domain;

import java.util.ArrayList;
import java.util.HashSet;

public class NoteMeta {
    private NoteMetaStorage storage;

    public NoteMeta(NoteMetaStorage storage) {
        this.storage = storage;
    }

    public void changeNamespace(String old, String current) {
        storage.changeNamespace(old, current);
    }

    public ArrayList<String> getTags() {
        return storage.getArray("tag");
    }

    public void addTag(String tag) {
        ArrayList<String> currentTags = storage.getArray("tag");
        currentTags.add(tag);

        HashSet<String> uniqueTags = new HashSet(currentTags);

        ArrayList<String> result = new ArrayList<>();
        result.addAll(uniqueTags);

        storage.setArray("tag", result);
    }

    public void removeTag(String tag) {
        ArrayList<String> currentTags = getTags();
        currentTags.remove(tag);
        setTags(currentTags);
    }

    public void setTags(ArrayList<String> tags) {
        storage.setArray("tag", tags);
    }

}
