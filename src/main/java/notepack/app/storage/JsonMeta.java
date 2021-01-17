package notepack.app.storage;

import notepack.app.domain.NoteMetaStorage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.exception.MessageError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonMeta implements NoteMetaStorage {

    NoteStorage storage;
    String namespace;
    JSONObject noteData;

    public JsonMeta(NoteStorage storage, String namespace) {
        this.storage = storage;
        this.namespace = namespace;

        loadData();
    }

    private void loadData() {
        try {
            noteData = storage.getMeta(namespace);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    synchronized private void saveData() {

        try {
            storage.setMeta(noteData, namespace);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    @Override
    public void changeNamespace(String old, String current) {
        storage.delete(old);
        try {
            storage.setMeta(noteData, current);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    @Override
    public String getValue(String key) {
        if (!noteData.has(key)) {
            return "";
        }
        return noteData.getString(key);
    }

    @Override
    public void setValue(String key, String value) {
        noteData.put(key, value);

        saveData();
    }

    @Override
    public ArrayList<String> getArray(String key) {
        ArrayList<String> result = new ArrayList<>();

        if (!noteData.has(key)) {
            return result;
        }

        JSONArray array = noteData.getJSONArray(key);
        for (Object value : array.toList()) {
            result.add((String)value);
        }
        return result;
    }

    @Override
    public void setArray(String key, ArrayList<String> value) {
        JSONArray array = new JSONArray(value);
        noteData.put(key, array);

        saveData();
    }

    @Override
    public void setObject(String key, HashMap<String, String> map) {

        if (map != null) {
            JSONObject toSet = new JSONObject();
            map.forEach((s, s2) -> toSet.put(s, s2));

            noteData.put(key, toSet);
        } else {
            if (noteData.has(key)) {
                noteData.remove(key);
            }
        }

        saveData();
    }

    @Override
    public HashMap<String, String> getObject(String key) {

        HashMap<String, String> result = new HashMap<>();
        if (!noteData.has(key)) {
            return result;
        }

        JSONObject tmp = noteData.getJSONObject(key);
        for (String objectKey : tmp.keySet()) {
            result.put(objectKey, (String) tmp.get(objectKey));
        }

        return result;
    }
}
