package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;

public class NoteOpen extends BaseTask implements Task, TypeNote {

    private Note note;

    public NoteOpen(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() throws MessageError {
        try {
            note.readFromStorage();
        } catch(MessageError ex) {
            messageBus.addTask(new NoteClose(note));
            
            throw ex;
        }
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }

}
