package notepack.app.domain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import notepack.Theme;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.*;
import notepack.app.storage.Filesystem;

public class App {

    private MessageBus messageBus;
    private SessionStorage sessionStorage;
    private Settings settings;
    private Theme theme;
    private ArrayList<Note> activeNotes = new ArrayList<>();
    private ArrayList<Notepad> activeNotepad = new ArrayList<>();

    public App(SessionStorage sessionStorage, Settings settings) {
        this.sessionStorage = sessionStorage;
        this.settings = settings;
        this.theme = new Theme(settings);

        messageBus = new MessageBus();

        initializeRecurringTasks();

        messageBus.registerNoteListener(new NoteListener() {
            @Override
            public void onOpen(Note n) {
                activeNotes.add(n);
            }

            @Override
            public void onClose(Note n) {
                activeNotes.remove(n);
            }

            @Override
            public void onChange(Note n) {
            }

            @Override
            public void onSave(Note n) {
            }
        });

        messageBus.registerNotepadListener(new NotepadListener() {
            @Override
            public void onOpen(Notepad notepad) {
                activeNotepad.add(notepad);
            }

            @Override
            public void onClose(Notepad notepad) {
                activeNotepad.remove(notepad);
            }
        });

    }

    public void startDispatcher() {
        messageBus.startDispatcher();
    }

    public void terminate() {
        messageBus.stopDispatcher();
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public Settings getSettings() {
        return settings;
    }

    public SessionStorage getSessionStorage() {
        return sessionStorage;
    }

    public Theme getTheme() {
        return theme;
    }

    public void addTask(Task task) {
        messageBus.addTask(task);
    }

    public void openNote(String path, Notepad notepad, String name) {
        Note note = new Note(path, notepad, name);
        messageBus.addTask(new NoteOpen(note));
    }

    public void openNote(Note n) {
        messageBus.addTask(new NoteOpen(n));
    }

    public void saveNote(Note note) {
        messageBus.addTask(new NoteSave(note));
    }

    public void closeNote(Note n) {
        activeNotes.remove(n);
        messageBus.addTask(new NoteClose(n));
    }

    public void changeNote(Note n, byte[] newValue) {
        n.setContents(newValue);
        messageBus.addTask(new NoteChanged(n));
    }

    public void renameNote(Note n, String newPath) {

        messageBus.addTask(new NoteRename(n, newPath));
        messageBus.addTask(new NotepadRefresh(n.getNotepad()));

    }

    public void deleteNote(Note n) {

        messageBus.addTask(new NoteDelete(n));
        messageBus.addTask(new NotepadRefresh(n.getNotepad()));

    }

    public void newNote(Notepad notepad) {
        messageBus.addTask(new NoteNew(notepad));
    }

    public void openNotepad(Notepad notepad) {
        messageBus.addTask(new NotepadOpen(notepad));
    }

    public void closeNotepad(Notepad notepad) {
        messageBus.addTask(new NotepadClose(notepad));
    }

    public void refreshNotepad(Notepad notepad) {
        messageBus.addTask(new NotepadRefresh(notepad));
    }

    public ArrayList<Notepad> getAvailableNotepads() {

        ArrayList<Notepad> result = sessionStorage.getAvailableNotepads();

        if (result.isEmpty()) {

            NoteStorageConfiguration nsc = new NoteStorageConfiguration();

            String dir = System.getProperty("user.home") + File.separator + "NotePack";
            File f = new File(dir);
            if (!f.exists()) {
                f.mkdirs();

                try {
                    String content = new String(getClass().getResourceAsStream("/notepack/welcome.md").readAllBytes());
                    File fileOut = new File(f.getAbsolutePath() + File.separator + "Welcome.md");
                    Files.write(fileOut.toPath(), content.getBytes());

                } catch (IOException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            nsc.set("directory", dir);

            Notepad notepad = new Notepad(new Filesystem(nsc), "First notepad");
            notepad.setParam("color", "#356fcc");

            result.add(notepad);

            openNote(notepad.getStorage().getBasePath() + File.separator + "Welcome.md", notepad, "Welcome.md");
        }

        return result;
    }

    public ArrayList<Note> getLastNotes() {
        ArrayList<Note> result = sessionStorage.getLastNotes();

        return result;
    }

    public ArrayList<Note> searchForNote(String q) {

        ArrayList<Note> tmp = new ArrayList<>();
        for (Notepad notepad : activeNotepad) {
            NoteStorageItem it = notepad.getStorage().getItemsInStorage();

            tmp.addAll(getNoteFromItem(it, notepad));
        }

        ArrayList<Note> result = new ArrayList<>();
        for (Note n : tmp) {
            if (n.getPath().toLowerCase().contains(q.toLowerCase())) {
                result.add(n);
            }
        }

        return result;
    }

    public void selectNoteInNotepad(Note note) {
        messageBus.addTask(new NotepadSelectNote(note));
    }

    private ArrayList<Note> getNoteFromItem(NoteStorageItem item, Notepad notepad) {

        ArrayList<Note> res = new ArrayList<>();

        if (!item.isLeaf()) {

            for (NoteStorageItem it : item.get()) {

                if (it.isLeaf()) {
                    Note n = new Note(it.getPath(), notepad, it.getName());
                    res.add(n);
                } else {
                    res.addAll(getNoteFromItem(it, notepad));
                }
            }

        } else {
            Note n = new Note(item.getPath(), notepad, item.getName());
            res.add(n);
        }

        return res;
    }

    private void initializeRecurringTasks() {

        NotepadRefreshRecurring task = new NotepadRefreshRecurring(this);
        task.startTaskAfterSecondsFromNow(60);
        messageBus.addTask(task);

        SaveSession sessionTask = new SaveSession();
        sessionTask.startTaskAfterSecondsFromNow(30);
        messageBus.addTask(sessionTask);

    }

}
