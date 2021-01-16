package notepack.app.domain;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.GuiListener;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.task.*;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageBus {

    private Queue<Task> tasks;
    private Queue<Task> tasksLowPriority;
    private ArrayList<NoteListener> noteListeners;
    private ArrayList<NotepadListener> notepadListeners;
    private ArrayList<GuiListener> guiListeners;

    private Thread dispatchThread;
    private boolean dispatcherStop = false;
    private final AtomicInteger tasksActive = new AtomicInteger();
    private int maxTasksActive = 3;

    public MessageBus() {
        tasks = new ConcurrentLinkedQueue<>();
        tasksLowPriority = new ConcurrentLinkedQueue<>();

        noteListeners = new ArrayList<>();
        notepadListeners = new ArrayList<>();
        guiListeners = new ArrayList<>();

        tasksActive.set(0);
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
                    } catch (MessageError e) {
                        Logger.getLogger(MessageBus.class.getName()).log(Level.SEVERE, null, e);
                        tasks.add(new ShowUserMessage(e.getMessage(), ShowUserMessage.TYPE.ERROR));
                    } catch (Exception e) {
                        Logger.getLogger(MessageBus.class.getName()).log(Level.SEVERE, null, e);
                        tasks.add(new ShowUserMessage(e.getMessage(), ShowUserMessage.TYPE.ERROR));
                    }
                    try {
                        Thread.sleep(100);
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
        if (dispatchThread != null) {
            dispatchThread.interrupt();
        }
        dispatchThread = null;
    }

    synchronized private Task getTaskToDispatch() {
        int queueSize = tasks.size();

        Task task = tasks.poll();
        if (task == null) {
            task = tasksLowPriority.poll();
        }
        if (task == null) {
            return null;
        }

        if (task instanceof BaseTask) {
            if (!((BaseTask) task).isTaskReadyToStart()) {
                tasksLowPriority.add(task);
                return null;
            }
        }

        return task;
    }

    private void dispatch() throws MessageError {

        int currentTasksCounter = tasksActive.get();
        if (currentTasksCounter > maxTasksActive) {
            return;
        }

        Task t = getTaskToDispatch();
        if (t == null) {
            return;
        }
        if (t instanceof BaseTask) {
            ((BaseTask) t).setMessageBus(this);
        }

        Thread job = new Thread(new Runnable() {
            @Override
            public void run() {
                tasksActive.incrementAndGet();

                try {
                    if (t instanceof Task) {
                        t.backgroundWork();
                    }

                    if (t instanceof TypeGui) {
                        for (GuiListener l : guiListeners) {
                            l.proceed((TypeGui) t);
                        }
                    }

                    if (t instanceof TypeRecurring) {
                        ((TypeRecurring) t).startTaskAfterSecondsFromNow(((TypeRecurring) t).getInterval());;
                        tasks.add(t);
                    }

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
                } catch (MessageError ex) {
                    Logger.getLogger(MessageBus.class.getName()).log(Level.SEVERE, null, ex);
                    tasks.add(new ShowUserMessage(ex.getMessage(), ShowUserMessage.TYPE.ERROR));
                }

                tasksActive.decrementAndGet();
            }

        });
        job.setName("Task processing " + t.toString());
        job.start();
    }

    public void registerNoteListener(NoteListener l) {
        noteListeners.add(l);
    }

    public void registerNotepadListener(NotepadListener l) {
        notepadListeners.add(l);
    }

    public void registerGuiListener(GuiListener l) {
        guiListeners.add(l);
    }

    public void addTask(Task t) {
        tasks.add(t);
    }

}
