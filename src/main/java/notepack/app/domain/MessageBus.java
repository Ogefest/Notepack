/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.app.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.event.NoteChanged;
import notepack.app.event.NoteOpened;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.TypeNote;
import notepack.app.task.TypeNotepad;

/**
 *
 * @author lg
 */
public class MessageBus {

    private Queue<Task> tasks;
//    private Queue<Event> events;
//    private ArrayList<EventListener> listeners;
    private ArrayList<NoteListener> noteListeners;
    private ArrayList<NotepadListener> notepadListeners;

    private Thread dispatchThread;

    public MessageBus() {
        tasks = new ConcurrentLinkedQueue<>();
//        events = new ConcurrentLinkedQueue<>();
//        listeners = new ArrayList<>();
        
        noteListeners = new ArrayList<>();
        notepadListeners = new ArrayList<>();
    }

    public void startDispatcher() {
        dispatchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    dispatch();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        break;
                    }
                } while (true);
            }
        });
        dispatchThread.setName("Task dispatcher");
        dispatchThread.start();
    }

    public void stopDispatcher() {
        dispatchThread.interrupt();
        dispatchThread = null;
    }

    private void dispatch() {

        for (Task t : tasks) {
            t.dispatch();
            if (t instanceof TypeNote) {
                for (NoteListener l : noteListeners) {
                    ((TypeNote) t).notify(l);
                }
            }
            if (t instanceof TypeNotepad) {
                for (NotepadListener l : notepadListeners) {
                    ((TypeNotepad) t).notify(l);
                }
            }

            tasks.remove(t);
        }

    }
    
    public void registerNoteListener(NoteListener l) {
        noteListeners.add(l);
    }
    
    public void registerNotepadListener(NotepadListener l) {
        notepadListeners.add(l);
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

}
