package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

import java.io.File;

public class TodoNew extends BaseTask implements Task,TypeNote,TypeGui {

    private Note note;
    private Workspace workspace;

    public TodoNew(Workspace workspace) {
        this.workspace = workspace;

        initializeNewChecklist();
    }

    private void initializeNewChecklist() {
        note = new Note(workspace);
        note.setContents(new byte[0]);

        note.setPath(workspace.getStorage().getBasePath() + File.separator + "checklist.ics");
        try {
            note.saveToStorage();
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    @Override
    public void backgroundWork() {
        note.setContents(new byte[0]);
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        if (note == null) {
            initializeNewChecklist();
        }
        addTaskToQueue(new WorkspaceRefresh(workspace));
        addTaskToQueue(new NoteOpen(note));
    }
}
