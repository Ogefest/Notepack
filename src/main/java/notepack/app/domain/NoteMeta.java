package notepack.app.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

    public void setTags(ArrayList<String> tags) {
        storage.setArray("tag", tags);
    }

    public void setReminder(LocalDate date) {
        HashMap<String, String> map = new HashMap<>();
        map.put("date", date.toString());

        storage.setObject("reminder", map);
    }

    public void removeReminder() {
        storage.setObject("reminder", null);
    }

    public LocalDate getReminder() {
        HashMap<String, String> map = storage.getObject("reminder");

        if (!map.containsKey("date")) {
            return null;
        }

        LocalDate result = LocalDate.parse(map.get("date"));
        return result;
    }

}
