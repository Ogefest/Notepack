package notepack.app.domain;

import java.io.File;
import java.util.ArrayList;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.OpenNote;
import notepack.app.storage.Filesystem;
import notepack.app.storage.JsonNotepadRepository;
import notepack.app.task.ChangedNote;
import notepack.app.task.CloseNote;
import notepack.app.task.CloseNotepad;
import notepack.app.task.DeleteNote;
import notepack.app.task.NewNote;
import notepack.app.task.OpenNotepad;
import notepack.app.task.RefreshNotepad;
import notepack.app.task.RenameNote;
import notepack.app.task.SaveNote;
import notepack.app.task.ShowGPGPasswordDialog;
import notepack.app.task.ShowUserMessage;
import notepack.encrypt.Fake;

public class App {

    private MessageBus messageBus;
    private SessionStorage sessionStorage;
    private Settings settings;
    private ArrayList<Note> activeNotes = new ArrayList<>();
    private ArrayList<Notepad> activeNotepad = new ArrayList<>();

    public App(SessionStorage sessionStorage, Settings settings) {
        this.sessionStorage = sessionStorage;
        this.settings = settings;

        messageBus = new MessageBus();
        messageBus.startDispatcher();

        messageBus.registerNoteListener(new NoteListener() {
            @Override
            public void onOpen(Note n) {
                activeNotes.add(n);
                sessionStorage.addNote(n);
            }

            @Override
            public void onClose(Note n) {
                activeNotes.remove(n);
                sessionStorage.removeNote(n);
            }

            @Override
            public void onChange(Note n) {
            }

            @Override
            public void onSave(Note n) {
                sessionStorage.removeNote(n);
                sessionStorage.addNote(n);
            }
        });

        messageBus.registerNotepadListener(new NotepadListener() {
            @Override
            public void onOpen(Notepad notepad) {
                activeNotepad.add(notepad);
                sessionStorage.addNotepad(notepad);
            }

            @Override
            public void onClose(Notepad notepad) {
                activeNotepad.remove(notepad);
                sessionStorage.removeNotepad(notepad);
            }

            @Override
            public void onNotesListUpdated(Notepad notepad) {
            }
        });

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

    public void openNote(String path, Notepad notepad, String name) {
        Note note = new Note(path, notepad, name);
        messageBus.addTask(new OpenNote(note));
    }

    public void openNote(Note n) {

        if (n.getNotepad().isGpgEnabled() && !n.getNotepad().getGpg().isPrivateKeyLoaded()) {
            messageBus.addTask(new ShowGPGPasswordDialog(n.getNotepad()));
        } else {
            messageBus.addTask(new OpenNote(n));
        }

    }

    public void saveNote(Note note) {
        messageBus.addTask(new SaveNote(note));
    }

    public void closeNote(Note n) {
        activeNotes.remove(n);
        messageBus.addTask(new CloseNote(n));
    }

    public void changeNote(Note n, byte[] newValue) {
        n.setContents(newValue);
        messageBus.addTask(new ChangedNote(n));
    }

    public void renameNote(Note n, String newPath) {

        messageBus.addTask(new RenameNote(n, newPath));
        messageBus.addTask(new RefreshNotepad(n.getNotepad()));

    }

    public void deleteNote(Note n) {

        messageBus.addTask(new DeleteNote(n));
        messageBus.addTask(new RefreshNotepad(n.getNotepad()));

    }

    public void newNote(Notepad notepad) {
        Note note = new Note(notepad);
        messageBus.addTask(new NewNote(note));
    }

    public void openNotepad(Notepad notepad) {
        messageBus.addTask(new OpenNotepad(notepad));
    }

    public void closeNotepad(Notepad notepad) {
        messageBus.addTask(new CloseNotepad(notepad));
    }

    public void refreshNotepad(Notepad notepad) {
        messageBus.addTask(new RefreshNotepad(notepad));
    }

    public ArrayList<Notepad> getAvailableNotepads() {

        ArrayList<Notepad> result = sessionStorage.getAvailableNotepads();

        if (result.isEmpty()) {

            NoteStorageConfiguration nsc = new NoteStorageConfiguration();

            String dir = System.getProperty("user.home") + File.separator + "NotePack";
            File f = new File(dir);
            if (!f.exists()) {
                f.mkdirs();
            }

            nsc.set("directory", dir);

            Notepad notepad = new Notepad(new Filesystem(nsc), "First notepad");
            notepad.setParam("color", "#356fcc");

            result.add(notepad);
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

}
