/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.util.ArrayList;
import notepack.app.event.NoteOpened;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.OpenNote;
import notepack.app.storage.Filesystem;
import notepack.app.storage.JsonNotepadRepository;
import notepack.app.task.ChangedNote;
import notepack.app.task.CloseNote;
import notepack.app.task.CloseNotepad;
import notepack.app.task.NewNote;
import notepack.app.task.OpenNotepad;
import notepack.app.task.SaveNote;

/**
 *
 * @author lg
 */
public class App {
    
    private MessageBus messageBus;
    private JsonNotepadRepository notepadRepository;
    private ArrayList<Note> activeNotes = new ArrayList<>();
    private ArrayList<Notepad> activeNotepad = new ArrayList<>();
    
    public App() {
        notepadRepository = new JsonNotepadRepository();
        
        messageBus = new MessageBus();
        messageBus.startDispatcher();
        
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
                notepadRepository.add(notepad);
            }

            @Override
            public void onClose(Notepad notepad) {
                activeNotepad.remove(notepad);
                notepadRepository.remove(notepad);
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
    
    public void openNote(String path, NoteStorage storage) {
        Note note = new Note(path, storage);
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
    
    public void newNote(NoteStorage storage) {
        Note note = new Note(storage);
        messageBus.addTask(new NewNote(note));
    }
    
    public void openNotepad(Notepad notepad) {
        messageBus.addTask(new OpenNotepad(notepad));
    }
    
    public void closeNotepad(Notepad notepad) {
        messageBus.addTask(new CloseNotepad(notepad));
    }
    
    public void refreshNotepad(Notepad notepad) {
        
    }
    
    public ArrayList<Notepad> getAvailableNotepads() {
        
        ArrayList<Notepad> result = notepadRepository.getAvailable();
        
        if (result.size() == 0) {
            
            NoteStorageConfiguration nsc = new NoteStorageConfiguration();
            nsc.set("directory", System.getProperty("user.home"));
            
            result.add(new Notepad(new Filesystem(nsc), "Home files"));
        }
        
        return result;
    }
    
}
