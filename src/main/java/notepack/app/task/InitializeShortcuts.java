package notepack.app.task;

import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import notepack.TodoPaneBackgroundController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;
import notepack.noterender.TextAreaController;

public class InitializeShortcuts extends BaseTask implements Task, TypeGui {
    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        Stage parentStage = taskUtil.getStage();
        SplitPane notePane = taskUtil.getNotePane();
        TabPane notes = taskUtil.getNotesContainer();

        KeyCombination kcCloseNote = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        notes.getScene().getAccelerators().put(kcCloseNote, () -> app.addTask(new NoteClose(taskUtil.getCurrentNote())));

        KeyCombination kcTodoNote = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
        notes.getScene().getAccelerators().put(kcTodoNote, () -> {
            Tab t = taskUtil.getNotesContainer().getSelectionModel().getSelectedItem();
            if (t.getUserData() instanceof TodoPaneBackgroundController) {
                app.addTask(new TodoPopup(taskUtil.getCurrentNote()));
            }
        });

        KeyCombination kcSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        notes.getScene().getAccelerators().put(kcSave, () -> app.addTask(new NoteSave(taskUtil.getCurrentNote())));

        KeyCombination kcNewNote = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        notePane.getScene().getAccelerators().put(kcNewNote, () -> app.addTask(new NoteNew(taskUtil.getCurrentNotepad())));

        KeyCombination kcSearchNote = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        parentStage.getScene().getAccelerators().put(kcSearchNote, () -> app.addTask(new ShowSearchForNoteDialog()));

        KeyCombination kcSearchReplaceString = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        notes.getScene().getAccelerators().put(kcSearchReplaceString, () -> {
            Tab t = taskUtil.getNotesContainer().getSelectionModel().getSelectedItem();
            if (t.getUserData() instanceof TextAreaController) {
                ((TextAreaController) t.getUserData()).showSearchReplaceForm();
            }
        });

        KeyCombination kcOpenNotes = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        parentStage.getScene().getAccelerators().put(kcOpenNotes, () -> taskUtil.showNotesPane());

        KeyCombination kcOpenTodo = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        parentStage.getScene().getAccelerators().put(kcOpenTodo, () -> taskUtil.showTodoPane());
    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
