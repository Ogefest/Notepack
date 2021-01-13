package notepack.app.domain;

import java.util.ArrayList;
import java.util.HashSet;

public class NoteMeta {
    private NoteMetaStorage storage;
    private String namespace;

    public NoteMeta(NoteMetaStorage storage) {
        this.storage = storage;
    }

    public void changeNamespace(String old, String current) {
//        storage.
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

    public void setTags(ArrayList<String> tags) {
        storage.setArray("tag", tags);
    }

}
