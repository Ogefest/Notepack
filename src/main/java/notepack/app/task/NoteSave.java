package notepack.app.task;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;
import notepack.app.storage.Validator;
import notepack.gui.TaskUtil;

public class NoteSave extends BaseTask implements Task, TypeNote,TypeGui {

    private Note note;

    public NoteSave(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() throws MessageError {

        if (note.getPath() != null) {

            if (Validator.isNameValid(note.getPath())) {
                addTaskToQueue(new ShowUserMessage("Invalid note name", ShowUserMessage.TYPE.ERROR));
                return;
            }

            note.saveToStorage();
            addTaskToQueue(new NoteMarkAsSaved(note));
            addTaskToQueue(new WorkspaceRefresh(note.getWorkspace()));
        }
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onSave(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        if (note.getPath() == null) {
            app.addTask(new NoteSetNamePopup(note));
        }

        Tab t = taskUtil.getNoteTab(note);
        t.setText(note.getName());
        Label l = (Label) t.getGraphic();
        l.setText("");

    }
}
