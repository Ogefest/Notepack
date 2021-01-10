package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.gui.TabNotepad;

public class SelectNoteInNotepad implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public enum TYPE {
        INFO, ERROR
    }

    private Note note;

    public SelectNoteInNotepad(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(Stage stage, App app) {
        TabPane container = (TabPane) stage.getScene().lookup("#notepadTabContainer");
        Notepad notepad = note.getNotepad();
        for (Tab tab : container.getTabs()) {
            if (tab instanceof TabNotepad) {
                NotebookTabController ctrl = (NotebookTabController) tab.getUserData();
                if (ctrl.getNotepad().getIdent().equals(notepad.getIdent())) {
                    container.getSelectionModel().select(tab);

                    ctrl.selectNote(note);

                    break;
                }
            }
        }
    }

}
