package notepack.noterender;

import notepack.NoteTabContentCallback;
import notepack.app.domain.App;
import notepack.app.domain.Note;

public interface NoteRenderController {

    public Note getNote();

    public void setNote(Note note);

    public void setApp(App app);
}
