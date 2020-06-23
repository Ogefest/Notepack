package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;

public class OpenNote extends BaseTask implements Task, TypeNote {

    private Note note;

    public OpenNote(Note note) {
        this.note = note;
    }

    @Override
    public void dispatch() throws MessageError {
        try {
            note.readFromStorage();
        } catch(MessageError ex) {
            messageBus.addTask(new CloseNote(note));
            
            throw ex;
        }
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }

}
