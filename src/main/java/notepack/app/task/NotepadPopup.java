package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import notepack.MainViewController;
import notepack.NotepadCreateController;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotepadPopup implements Task, TypeGui {

    private Notepad notepad;

    public NotepadPopup(Notepad notepad) {
        this.notepad = notepad;
    }

    public NotepadPopup() {

    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/NotepadConfigurationPopup.fxml"));

        Node root;
        try {
            root = fxmlLoader.load();

            NotepadCreateController ctrl = fxmlLoader.getController();

            ctrl.setTaskUtil(taskUtil);
            if (this.notepad != null) {
                ctrl.setNotepadToEdit(notepad);
                ctrl.setNotepadCreateCallback(notepad -> {
                    app.closeNotepad(notepad);
                    app.openNotepad(notepad);
                });
            } else {
                ctrl.setNotepadCreateCallback(notepad -> app.openNotepad(notepad));
            }

            taskUtil.openPopup(root);


        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
