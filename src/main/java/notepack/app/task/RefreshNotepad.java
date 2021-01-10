package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NotepadListener;
import notepack.gui.TabNotepad;

public class RefreshNotepad implements Task,TypeNotepad,TypeGui {

    private Notepad notepad;
    
    public RefreshNotepad(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void dispatch() throws MessageError {
        notepad.getStorage().refreshItemsInStorage();
    }

    @Override
    public void notify(NotepadListener listener) {
        listener.onNotesListUpdated(notepad);
    }

    @Override
    public void proceed(Stage stage, App app) {

        TabPane container = (TabPane) stage.getScene().lookup("#notepadTabContainer");

        for (Tab tab : container.getTabs()) {
            if (tab instanceof TabNotepad) {
                NotebookTabController ctrl = (NotebookTabController) tab.getUserData();
                if (ctrl.getNotepad().getIdent().equals(notepad.getIdent())) {
                    ctrl.refreshTreeView();
                    break;
                }
            }
        }
    }
}
