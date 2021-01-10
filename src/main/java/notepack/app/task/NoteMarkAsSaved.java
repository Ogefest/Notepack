package notepack.app.task;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

public class NoteMarkAsSaved implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    private Note note;

    public NoteMarkAsSaved(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        TabPane container = (TabPane) taskUtil.getStage().getScene().lookup("#noteTabContainer");


        for (Tab tab : container.getTabs()) {

            NoteRenderController render = (NoteRenderController) tab.getUserData();
            if (render.getNote().getIdent().equals(note.getIdent())) {

                Label l = (Label) tab.getGraphic();
                l.getStyleClass().clear();
                break;

            }

        }
    }

}
