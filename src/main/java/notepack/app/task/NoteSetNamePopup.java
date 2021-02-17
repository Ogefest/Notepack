package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import notepack.MainViewController;
import notepack.SetNameController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoteSetNamePopup implements Task, TypeGui {

    private Note note;

    public NoteSetNamePopup(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        AnchorPane pane;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/SetNamePopup.fxml"));
            pane = loader.load();

            SetNameController ctrl = loader.getController();
            ctrl.setTaskUtil(taskUtil);
            ctrl.initView(note, app);

            taskUtil.openPopup(pane);

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
