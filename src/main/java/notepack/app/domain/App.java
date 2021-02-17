package notepack.app.domain;

import notepack.Theme;
import notepack.app.listener.NoteListener;
import notepack.app.listener.WorkspaceListener;
import notepack.app.storage.Filesystem;
import notepack.app.task.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private MessageBus messageBus;
    private SessionStorage sessionStorage;
    private Settings settings;
    private Theme theme;
    private ArrayList<Note> activeNotes = new ArrayList<>();
    private ArrayList<Workspace> activeWorkspace = new ArrayList<>();
    private ArrayList<Todo> activeTodo = new ArrayList<>();

    public App(SessionStorage sessionStorage, Settings settings) {
        this.sessionStorage = sessionStorage;
        this.settings = settings;
        this.theme = new Theme(settings);

        messageBus = new MessageBus();

        initializeRecurringTasks();
        messageBus.addTask(new TodoRefresh());

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

        messageBus.registerWorkspaceListener(new WorkspaceListener() {
            @Override
            public void onOpen(Workspace workspace) {
                activeWorkspace.add(workspace);
            }

            @Override
            public void onClose(Workspace workspace) {
                activeWorkspace.remove(workspace);
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

    public void openNote(String path, Workspace workspace, String name) {
        Note note = new Note(path, workspace, name);
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
        messageBus.addTask(new WorkspaceRefresh(n.getWorkspace()));

    }

    public void deleteNote(Note n) {

        messageBus.addTask(new NoteDelete(n));
        messageBus.addTask(new WorkspaceRefresh(n.getWorkspace()));

    }

    public void newNote(Workspace workspace) {
        messageBus.addTask(new NoteNew(workspace));
    }

    public void openWorkspace(Workspace workspace) {
        messageBus.addTask(new WorkspaceOpen(workspace));
    }

    public void closeWorkspace(Workspace workspace) {
        messageBus.addTask(new WorkspaceClose(workspace));
    }

    public void refreshWorkspace(Workspace workspace) {
        messageBus.addTask(new WorkspaceRefresh(workspace));
    }

    public ArrayList<Workspace> getAvailableWorkspaces() {

        ArrayList<Workspace> result = sessionStorage.getAvailableWorkspaces();

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

            Workspace workspace = new Workspace(new Filesystem(nsc), "First workspace");
            workspace.setParam("color", "#356fcc");

            result.add(workspace);

            openNote(workspace.getStorage().getBasePath() + File.separator + "Welcome.md", workspace, "Welcome.md");
        }

        return result;
    }

    public ArrayList<Note> getLastNotes() {
        ArrayList<Note> result = sessionStorage.getLastNotes();

        return result;
    }

    public ArrayList<Note> searchForNote(String q) {

        ArrayList<Note> tmp = new ArrayList<>();
        for (Workspace workspace : activeWorkspace) {
            NoteStorageItem it = workspace.getStorage().getItemsInStorage();

            tmp.addAll(getNoteFromItem(it, workspace));
        }

        ArrayList<Note> result = new ArrayList<>();
        for (Note n : tmp) {
            if (n.getPath().toLowerCase().contains(q.toLowerCase())) {
                result.add(n);
            }
        }

        return result;
    }

    public void selectNoteInWorkspace(Note note) {
        messageBus.addTask(new WorkspaceSelectNote(note));
    }

    private ArrayList<Note> getNoteFromItem(NoteStorageItem item, Workspace workspace) {

        ArrayList<Note> res = new ArrayList<>();

        if (!item.isLeaf()) {

            for (NoteStorageItem it : item.get()) {

                if (it.isLeaf()) {
                    Note n = new Note(it.getPath(), workspace, it.getName());
                    res.add(n);
                } else {
                    res.addAll(getNoteFromItem(it, workspace));
                }
            }

        } else {
            Note n = new Note(item.getPath(), workspace, item.getName());
            res.add(n);
        }

        return res;
    }

    private void initializeRecurringTasks() {

        WorkspaceRefreshRecurring task = new WorkspaceRefreshRecurring(this);
        task.startTaskAfterSecondsFromNow(60);
        messageBus.addTask(task);

        SaveSession sessionTask = new SaveSession();
        sessionTask.startTaskAfterSecondsFromNow(30);
        messageBus.addTask(sessionTask);

        NoteSaveRecurring noteSave = new NoteSaveRecurring();
        noteSave.startTaskAfterSecondsFromNow(20);
        messageBus.addTask(noteSave);

    }

    public ArrayList<Note> getNotesWithTodo() {

        ArrayList<Note> tmp = new ArrayList<>();
        for (Workspace workspace : activeWorkspace) {
            NoteStorageItem it = workspace.getStorage().getItemsInStorage();

            tmp.addAll(getNoteFromItem(it, workspace));
        }

        ArrayList<Note> result = new ArrayList<>();
        for (Note n : tmp) {

            if (n.getName().contains(".ics")) {
                result.add(n);
            }

        }

        return result;
    }

    public void refreshTodo(Note note) {

    }


}
