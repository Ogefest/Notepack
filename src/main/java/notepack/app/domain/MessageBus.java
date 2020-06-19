package notepack.app.domain;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.listener.GuiListener;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.TypeGui;
import notepack.app.task.TypeNote;
import notepack.app.task.TypeNotepad;

public class MessageBus {

    private Queue<Task> tasks;
    private ArrayList<NoteListener> noteListeners;
    private ArrayList<NotepadListener> notepadListeners;
    private ArrayList<GuiListener> guiListeners;

    private Thread dispatchThread;
    private boolean dispatcherStop = false;

    public MessageBus() {
        tasks = new ConcurrentLinkedQueue<>();

        noteListeners = new ArrayList<>();
        notepadListeners = new ArrayList<>();
        guiListeners = new ArrayList<>();
    }

    public void startDispatcher() {

        if (dispatchThread != null) {
            return;
        }

        dispatchThread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        dispatch();
                    } catch (Exception e) {
                        Logger.getLogger(MessageBus.class.getName()).log(Level.SEVERE, null, e);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        break;
                    }
                } while (!dispatcherStop);
            }
        });
        dispatchThread.setName("Task dispatcher");
        dispatchThread.start();
    }

    public void stopDispatcher() {
        dispatcherStop = true;
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
            if (t instanceof TypeGui) {
                for (GuiListener l : guiListeners) {
                    ((TypeGui) t).notify(l);
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
