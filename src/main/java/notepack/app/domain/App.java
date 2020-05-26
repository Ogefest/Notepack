/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

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

/**
 *
 * @author lg
 */
public class App {

    private MessageBus messageBus;
    private JsonNotepadRepository sessionRepository;
    private ArrayList<Note> activeNotes = new ArrayList<>();
    private ArrayList<Notepad> activeNotepad = new ArrayList<>();

    public App() {
        sessionRepository = new JsonNotepadRepository();

        messageBus = new MessageBus();
        messageBus.startDispatcher();

        messageBus.registerNoteListener(new NoteListener() {
            @Override
            public void onOpen(Note n) {
                activeNotes.add(n);
                sessionRepository.addNote(n);
            }

            @Override
            public void onClose(Note n) {
                activeNotes.remove(n);
                sessionRepository.removeNote(n);
            }

            @Override
            public void onChange(Note n) {
            }

            @Override
            public void onSave(Note n) {
                sessionRepository.removeNote(n);
                sessionRepository.addNote(n);
            }
        });

        messageBus.registerNotepadListener(new NotepadListener() {
            @Override
            public void onOpen(Notepad notepad) {
                activeNotepad.add(notepad);
                sessionRepository.addNotepad(notepad);
            }

            @Override
            public void onClose(Notepad notepad) {
                activeNotepad.remove(notepad);
                sessionRepository.removeNotepad(notepad);
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

    public void openNote(String path, Notepad notepad) {
        Note note = new Note(path, notepad);
        messageBus.addTask(new OpenNote(note));
    }

    public void openNote(Note n) {
        messageBus.addTask(new OpenNote(n));
    }

    public void saveNote(Note note) {
        messageBus.addTask(new SaveNote(note));
    }

    public void closeNote(Note n) {
        activeNotes.remove(n);
        messageBus.addTask(new CloseNote(n));
    }

    public void changeNote(Note n, String newValue) {
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

        ArrayList<Notepad> result = sessionRepository.getAvailableNotepads();

        if (result.size() == 0) {

            NoteStorageConfiguration nsc = new NoteStorageConfiguration();
            nsc.set("directory", System.getProperty("user.home"));

            result.add(new Notepad(new Filesystem(nsc), "Home files"));
        }

        return result;
    }

    public ArrayList<Note> getLastNotes() {
        ArrayList<Note> result = sessionRepository.getLastNotes();
        if (result.size() == 0) {
//            result.add(new Note(new Filesystem()));
        }

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
                    Note n = new Note(it.getPath(), notepad);
                    res.add(n);
                } else {
                    res.addAll(getNoteFromItem(it, notepad));
                }
            }
            

        } else {
            Note n = new Note(item.getPath(), notepad);
            res.add(n);
        }

        return res;
    }

}
