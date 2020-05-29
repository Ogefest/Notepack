/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.Notepad;
import org.json.JSONArray;
import org.json.JSONObject;
import notepack.app.domain.SessionStorage;
import notepack.app.domain.Settings;
import notepack.app.domain.StringEncryption;

/**
 *
 * @author lg
 */
public class JsonNotepadRepository implements SessionStorage {

    private ArrayList<Notepad> notepadsList = null;
    private ArrayList<Note> notesList = null;

    private StringEncryption encryption;
    private Settings settings;

    public JsonNotepadRepository(StringEncryption encryption, Settings settings) {
        this.encryption = encryption;
        this.settings = settings;
    }

    private String getJsonFromFile(String filename) {
        try {
            String directory = System.getProperty("user.home") + File.separator + ".notepack" + File.separator;
            String path = directory + filename;

            File f = new File(directory);
            if (!f.exists()) {
                f.mkdirs();
            }

            String dataFromFile = new String(Files.readAllBytes(Paths.get(path)));

            String key = settings.get("encryption.key");
            if (key.length() == 0) {
                key = UUID.randomUUID().toString();
                settings.set("encryption.key", key);
            }

            return encryption.decrypt(key, dataFromFile);

        } catch (IOException ex) {
            return "";
        }
    }

    private void setJsonToFile(String json, String filename) {
        try {

            String directory = System.getProperty("user.home") + File.separator + ".notepack" + File.separator;
            String path = directory + filename;

            File f = new File(directory);
            if (!f.exists()) {
                f.mkdirs();
            }

            String key = settings.get("encryption.key");
            if (key.length() == 0) {
                key = UUID.randomUUID().toString();
                settings.set("encryption.key", key);
            }

            String toSave = encryption.encrypt(key, json);

            Files.write(Paths.get(path), toSave.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Notepad> getAvailableNotepads() {

        if (notepadsList == null) {
            notepadsList = new ArrayList<>();

            String content = getJsonFromFile("notepads.data");
            if (content.length() == 0) {
                return notepadsList;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String storageClassName = obj.getString("storage_class");
                String notepadIdent = obj.getString("ident");

                NoteStorageConfiguration nsc = new NoteStorageConfiguration();

                JSONObject nscJson = obj.getJSONObject("storage_config");
                for (String k : nscJson.keySet()) {
                    nsc.set(k, nscJson.getString(k));
                }

                try {
                    Class cls = Class.forName(storageClassName);
                    NoteStorage storage = (NoteStorage) cls.newInstance();
                    storage.setConfiguration(nsc);

                    Notepad notepad = new Notepad(storage, "", notepadIdent);
                    JSONObject paramsJson = obj.getJSONObject("params");
                    for (String k : paramsJson.keySet()) {
                        notepad.setParam(k, paramsJson.getString(k));
                    }

                    notepadsList.add(notepad);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return notepadsList;
    }

    @Override
    public void addNotepad(Notepad notepad) {

        if (notepadsList.indexOf(notepad) == -1) {
            notepadsList.add(notepad);
        }

        saveNotepadsToFile();
    }

    @Override
    public void removeNotepad(Notepad notepad) {
        notepadsList.remove(notepad);

        saveNotepadsToFile();
    }

    private void saveNotepadsToFile() {

        JSONArray toSave = new JSONArray();

        for (Notepad n : notepadsList) {

            JSONObject current = new JSONObject();
            current.put("storage_class", n.getStorage().getClass().getCanonicalName());
            current.put("ident", n.getIdent());

            JSONObject storageConfig = new JSONObject();

            NoteStorageConfiguration cfg = n.getStorage().getConfiguration();
            for (String k : cfg.getAll().keySet()) {
                storageConfig.put(k, cfg.get(k));
            }
            current.put("storage_config", storageConfig);

            JSONObject notepadParams = new JSONObject();
            HashMap<String, String> params = n.getParams();
            for (String k : params.keySet()) {
                notepadParams.put(k, params.get(k));
            }
            current.put("params", notepadParams);

            toSave.put(current);
        }

        setJsonToFile(toSave.toString(), "notepads.data");

    }

    @Override
    public ArrayList<Note> getLastNotes() {

        if (notesList == null) {
            notesList = new ArrayList<>();

            String content = getJsonFromFile("notessession.data");
            if (content.length() == 0) {
                return notesList;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String notePath = obj.getString("path");
                String noteName = obj.getString("name");
                String notepadIdent = obj.getString("notepad_ident");
                Notepad notepadToUse = null;

                for (Notepad nn : getAvailableNotepads()) {
                    if (nn.getIdent().equals(notepadIdent)) {
                        notepadToUse = nn;
                        break;
                    }
                }

                if (notepadToUse == null) {
                    continue;
                }

                Note note = new Note(notePath, notepadToUse, noteName);

                notesList.add(note);
            }
        }

        return notesList;
    }

    @Override
    public void addNote(Note note) {
        if (notesList.indexOf(note) == -1) {
            notesList.add(note);
            saveNotesToFile();
        }
    }

    @Override
    public void removeNote(Note note) {
        notesList.remove(note);
        saveNotesToFile();
    }

    private void saveNotesToFile() {

        JSONArray toSave = new JSONArray();

        for (Note n : notesList) {

            if (n.getPath() == null) {
                continue;
            }

            JSONObject current = new JSONObject();
//            current.put("storage_class", n.getStorage().getClass().getCanonicalName());
            current.put("notepad_ident", n.getNotepad().getIdent());
            current.put("path", n.getPath());
            current.put("name", n.getName());

            toSave.put(current);

        }
        setJsonToFile(toSave.toString(), "notessession.data");

    }
}
