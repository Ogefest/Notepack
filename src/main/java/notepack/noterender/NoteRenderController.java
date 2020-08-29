package notepack.noterender;

import notepack.NoteTabContentCallback;
import notepack.app.domain.Note;

public interface NoteRenderController {
    public Note getNote();
    public void setNote(Note note);
    public void setNoteTabContentCallback(NoteTabContentCallback clbk);
}
