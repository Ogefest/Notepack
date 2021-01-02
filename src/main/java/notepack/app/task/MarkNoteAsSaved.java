package notepack.app.task;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.gui.TabNotepad;
import notepack.noterender.NoteRenderController;

public class MarkNoteAsSaved implements Task, TypeGui {

    @Override
    public void dispatch() {
    }

    private Note note;

    public MarkNoteAsSaved(Note note) {
        this.note = note;
    }

    @Override
    public void proceed(Stage stage, App app) {

        TabPane container = (TabPane) stage.getScene().lookup("#noteTabContainer");


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
