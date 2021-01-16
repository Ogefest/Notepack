package notepack.app.task;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

import java.util.Optional;

public class NoteClose implements Task,TypeNote,TypeGui {

    private Note note;

    public NoteClose(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onClose(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        this.note = taskUtil.getCurrentNote();

        TabPane tabContainer = taskUtil.getNotesContainer();
        Tab noteTab = taskUtil.getNoteTab(note);

        if (!note.isSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Changes not saved, do you want to save document?");

            ButtonType buttonSave = new ButtonType("Save");
            ButtonType buttonClose = new ButtonType("Close without saving");
            ButtonType buttonDontClose = new ButtonType("Don't close");

            alert.getButtonTypes().setAll(buttonDontClose, buttonClose, buttonSave);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonSave) {
                app.saveNote(note);

                tabContainer.getTabs().remove(noteTab);
            } else if (result.isPresent() && result.get() == buttonClose) {
                tabContainer.getTabs().remove(noteTab);
            }
        } else {
            tabContainer.getTabs().remove(noteTab);
        }


    }
}
