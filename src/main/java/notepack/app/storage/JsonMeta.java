package notepack.app.storage;

import notepack.app.domain.NoteMetaStorage;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.exception.MessageError;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
}
