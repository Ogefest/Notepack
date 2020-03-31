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
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.Notepad;
import notepack.app.domain.NotepadStorage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author lg
 */
public class JsonNotepadRepository implements NotepadStorage {

//    private class NotepadStorageClass implements Serializable {
//
//        public String notepadStorageClass;
//        public String notepadName;
//        public NoteStorageConfiguration conf;
//    }
    private ArrayList<Notepad> list = null;

    @Override
    public ArrayList<Notepad> getAvailable() {

        if (list == null) {
            list = new ArrayList<>();

            String content = "";
            try {
                content = new String(Files.readAllBytes(Paths.get("notepads.data")));
            } catch (IOException e) {
                return list;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String notepadName = obj.getString("name");
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

                    Notepad notepad = new Notepad(storage, notepadName);
                    list.add(notepad);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        
        return list;
    }

    @Override
    public void add(Notepad notepad) {
        
        if (list.indexOf(notepad) == -1) {
            list.add(notepad);
        }

        saveToFile();
    }

    @Override
    public void remove(Notepad notepad) {
        list.remove(notepad);

        saveToFile();
    }

    private void saveToFile() {

        JSONArray toSave = new JSONArray();

        for (Notepad n : list) {

            JSONObject current = new JSONObject();
            current.put("storage_class", n.getStorage().getClass().getCanonicalName());
            current.put("name", n.getName());

            JSONObject storageConfig = new JSONObject();

            NoteStorageConfiguration cfg = n.getStorage().getConfiguration();
            for (String k : cfg.getAll().keySet()) {
                storageConfig.put(k, cfg.get(k));
            }

            current.put("storage_config", storageConfig);

            toSave.put(current);
        }

        try {
            Files.write(Paths.get("notepads.data"), toSave.toString().getBytes());
        } catch (IOException ex) {
            Logger.getLogger(JsonNotepadRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
