package notepack.noterender;

import notepack.NoteTabContentCallback;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Settings;

public interface NoteRenderController {

    public Note getNote();

    public void setState(App app,Note note);

    public void noteActivated();
    public void noteDeactivated();
}
