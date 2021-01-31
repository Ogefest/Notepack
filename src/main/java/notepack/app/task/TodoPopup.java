package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import notepack.MainViewController;
import notepack.TodoPopupController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Todo;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TodoPopup implements Task, TypeGui {

    private Note note;
    private Todo todo;

    public TodoPopup(Note note) {
        this.note = note;
        this.todo = new Todo();
    }

    public TodoPopup(Note note, Todo todo) {
        this.note = note;
        this.todo = todo;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        AnchorPane pane;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/TodoPopup.fxml"));
            pane = loader.load();

            TodoPopupController ctrl = loader.getController();
            ctrl.setAppNote(todo, note, taskUtil);

            taskUtil.openPopup(pane);

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
