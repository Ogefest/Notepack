package notepack.noterender;

import javafx.scene.layout.Pane;
import notepack.app.domain.App;
import notepack.app.domain.Note;

public interface NoteRenderController {

    public Note getNote();

    public void setState(App app, Note note);

    public void noteActivated();

    public void noteDeactivated();

    public Pane getTagContainer();
}
