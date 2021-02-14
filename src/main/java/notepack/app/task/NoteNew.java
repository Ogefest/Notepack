package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

public class NoteNew extends BaseTask implements Task,TypeNote,TypeGui {
    
    private Note note;
    
    public NoteNew(Workspace workspace) {
        this.note = new Note(workspace);
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
            note = new Note(taskUtil.getCurrentWorkspace());
            note.setContents(new byte[0]);
        }
        addTaskToQueue(new NoteOpen(note));
    }
}
