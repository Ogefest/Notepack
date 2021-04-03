package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

import java.util.ArrayList;

public class SaveSession extends BaseTask implements Task, TypeRecurring, TypeGui {
    @Override
    public void backgroundWork() throws MessageError {

    }

    @Override
    public int getInterval() {
        return 10;
    }

    /*
    save to file only when all idens are different from previous check
    this is simple cache to avoid saving file to disk even if nothing was changed
     */
    private String notesCacheString = "";

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        ArrayList<Workspace> workspacesToSave = new ArrayList<>();
        String tmpKey = "";

        for (Workspace w : app.getAvailableWorkspaces()) {
            workspacesToSave.add(w);
        }
        app.getSessionStorage().setWorkspaceList(workspacesToSave);

        TabPane notesTabs = taskUtil.getNotesContainer();

        ArrayList<Note> notesToSave = new ArrayList<>();
        tmpKey = "";
        for (Tab tab : notesTabs.getTabs()) {
            NoteRenderController render = (NoteRenderController) tab.getUserData();
            notesToSave.add(render.getNote());

            tmpKey += render.getNote().getIdent();
        }
        if (!tmpKey.equals(notesCacheString)) {
            app.getSessionStorage().setNoteList(notesToSave);
            notesCacheString = tmpKey;
        }

    }
}
