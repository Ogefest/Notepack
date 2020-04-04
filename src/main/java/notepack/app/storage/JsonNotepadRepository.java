/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.Notepad;
import org.json.JSONArray;
import org.json.JSONObject;
import notepack.app.domain.SessionStorage;

/**
 *
 * @author lg
 */
public class JsonNotepadRepository implements SessionStorage {

    private ArrayList<Notepad> notepadsList = null;
    private ArrayList<Note> notesList = null;

    @Override
    public ArrayList<Notepad> getAvailableNotepads() {

        if (notepadsList == null) {
            notepadsList = new ArrayList<>();

            String content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get("notepads.data")));
            } catch (IOException e) {
                return notepadsList;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String storageClassName = obj.getString("storage_class");

                NoteStorageConfiguration nsc = new NoteStorageConfiguration();

                JSONObject nscJson = obj.getJSONObject("storage_config");
                for (String k : nscJson.keySet()) {
                    nsc.set(k, nscJson.getString(k));
                }

                try {
                    Class cls = Class.forName(storageClassName);
                    NoteStorage storage = (NoteStorage) cls.newInstance();
                    storage.setConfiguration(nsc);

                    Notepad notepad = new Notepad(storage);
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

        try {
            Files.write(Paths.get("notepads.data"), toSave.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public ArrayList<Note> getLastNotes() {

        if (notesList == null) {
            notesList = new ArrayList<>();

            String content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get("notessession.data")));
            } catch (IOException e) {
                return notesList;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String notePath = obj.getString("path");
                String storageClassName = obj.getString("storage_class");

                NoteStorageConfiguration nsc = new NoteStorageConfiguration();

                JSONObject nscJson = obj.getJSONObject("storage_config");
                for (String k : nscJson.keySet()) {
                    nsc.set(k, nscJson.getString(k));
                }

                try {
                    Class cls = Class.forName(storageClassName);
                    NoteStorage storage = (NoteStorage) cls.newInstance();
                    storage.setConfiguration(nsc);

                    Note note = new Note(notePath, storage);

                    notesList.add(note);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            current.put("storage_class", n.getStorage().getClass().getCanonicalName());
            current.put("path", n.getPath());

            JSONObject storageConfig = new JSONObject();

            NoteStorageConfiguration cfg = n.getStorage().getConfiguration();
            for (String k : cfg.getAll().keySet()) {
                storageConfig.put(k, cfg.get(k));
            }
            current.put("storage_config", storageConfig);
            toSave.put(current);

        }
        try {
            Files.write(Paths.get("notessession.data"), toSave.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
