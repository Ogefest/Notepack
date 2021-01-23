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

    public void setTodo(Todo todo) {
        HashMap<String, String> map = new HashMap<>();
        map.put("duedate", todo.getDueDate().toString());
        map.put("summary", todo.getSummary());
        map.put("finished", todo.isFinished() ? "1" : "0");

        storage.setObject("todo", map);
    }

    public void removeTodo() {
        storage.setObject("todo", null);
    }

    public Todo getTodo() {

        HashMap<String, String> map = storage.getObject("todo");

        if (map == null) {
            return null;
        }

        if (!map.containsKey("summary")) {
            return null;
        }

        LocalDate dueDate = LocalDate.parse(map.get("duedate"));
        boolean isFinished = map.get("finished").equals("1") ? true : false;
        String summary = map.get("summary");

        Todo result = new Todo();
        result.setDueDate(dueDate);
        result.setFinished(isFinished);
        result.setSummary(summary);

        return result;
    }

}
