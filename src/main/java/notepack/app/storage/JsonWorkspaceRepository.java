package notepack.app.storage;

import notepack.app.domain.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonWorkspaceRepository implements SessionStorage {

    private ArrayList<Workspace> workspacesList = null;
    private ArrayList<Note> notesList = null;

    private StringEncryption encryption;
    private Settings settings;

    public JsonWorkspaceRepository(StringEncryption encryption, Settings settings) {
        this.encryption = encryption;
        this.settings = settings;
    }

    private String getJsonFromFile(String filename) {
        try {
            String directory = settings.get("session.directory", System.getProperty("user.home")) + File.separator + ".notepack" + File.separator;
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

            String directory = settings.get("session.directory", System.getProperty("user.home")) + File.separator + ".notepack" + File.separator;
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
            Logger.getLogger(JsonWorkspaceRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ArrayList<Workspace> getAvailableWorkspaces() {

        if (workspacesList == null) {
            workspacesList = new ArrayList<>();

            String content = getJsonFromFile("notepads.data");
            if (content.length() == 0) {
                return workspacesList;
            }

            JSONArray input = new JSONArray(content);
            for (int i = 0; i < input.length(); i++) {

                JSONObject obj = input.getJSONObject(i);

                String storageClassName = obj.getString("storage_class");
                String workspaceIdent = obj.getString("ident");

                NoteStorageConfiguration nsc = new NoteStorageConfiguration();

                JSONObject nscJson = obj.getJSONObject("storage_config");
                for (String k : nscJson.keySet()) {
                    nsc.set(k, nscJson.getString(k));
                }

                try {
                    Class cls = Class.forName(storageClassName);
                    NoteStorage storage = (NoteStorage) cls.newInstance();
                    storage.setConfiguration(nsc);

                    Workspace workspace = new Workspace(storage, "", workspaceIdent);
                    JSONObject paramsJson = obj.getJSONObject("params");
                    for (String k : paramsJson.keySet()) {
                        workspace.setParam(k, paramsJson.getString(k));
                    }
                    workspace.registerProcessors();

                    if (!workspacesList.contains(workspace)) {
                        workspacesList.add(workspace);
                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(JsonWorkspaceRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(JsonWorkspaceRepository.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JsonWorkspaceRepository.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return workspacesList;
    }

    @Override
    public void setWorkspaceList(ArrayList<Workspace> workspaces) {
        this.workspacesList = workspaces;

        saveWorkspacesToFile();
    }

    @Override
    public void addWorkspace(Workspace workspace) {

        if (workspacesList.indexOf(workspace) == -1) {
            workspacesList.add(workspace);
        }

        saveWorkspacesToFile();
    }

    @Override
    public void removeWorkspace(Workspace workspace) {
        workspacesList.remove(workspace);

        saveWorkspacesToFile();
    }

    private void saveWorkspacesToFile() {

        JSONArray toSave = new JSONArray();

        for (Workspace n : workspacesList) {

            JSONObject current = new JSONObject();
            
            NoteStorageMiddleware nsm = (NoteStorageMiddleware) n.getStorage();
            NoteStorage storage = nsm.getParentStorage();
            
            current.put("storage_class", storage.getClass().getCanonicalName());
            current.put("ident", n.getIdent());

            JSONObject storageConfig = new JSONObject();

            NoteStorageConfiguration cfg = n.getStorage().getConfiguration();
            for (String k : cfg.getAll().keySet()) {
                storageConfig.put(k, cfg.get(k));
            }
            current.put("storage_config", storageConfig);

            JSONObject workspaceParams = new JSONObject();
            HashMap<String, String> params = n.getParams();
            for (String k : params.keySet()) {
                workspaceParams.put(k, params.get(k));
            }
            current.put("params", workspaceParams);

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
                String workspaceIdent = obj.getString("notepad_ident");
                Workspace workspaceToUse = null;

                for (Workspace nn : getAvailableWorkspaces()) {
                    if (nn.getIdent().equals(workspaceIdent)) {
                        workspaceToUse = nn;
                        break;
                    }
                }

                if (workspaceToUse == null) {
                    continue;
                }

                Note note = new Note(notePath, workspaceToUse, noteName);
                if (!notesList.contains(note)) {
                    notesList.add(note);
                }
            }
        }

        return notesList;
    }

    @Override
    public void setNoteList(ArrayList<Note> notes) {
        this.notesList = notes;

        saveNotesToFile();
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
            current.put("notepad_ident", n.getWorkspace().getIdent());
            current.put("path", n.getPath());
            current.put("name", n.getName());

            toSave.put(current);

        }
        setJsonToFile(toSave.toString(), "notessession.data");

    }
}
