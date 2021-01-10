package notepack.app.task;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;
import notepack.noterender.TextAreaController;

public class InitializeShortcuts extends BaseTask implements Task, TypeGui {
    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        Stage parentStage = taskUtil.getStage();

        Note currentNote = taskUtil.getCurrentNote();
        Notepad currentNotepad = taskUtil.getCurrentNotepad();

        KeyCombination kcCloseNote = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        parentStage.getScene().getAccelerators().put(kcCloseNote, () -> app.addTask(new NoteClose(currentNote)));

        KeyCombination kcSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        parentStage.getScene().getAccelerators().put(kcSave, () -> app.addTask(new NoteSave(taskUtil.getCurrentNote())));

        KeyCombination kcNewNote = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        parentStage.getScene().getAccelerators().put(kcNewNote, () -> app.addTask(new NoteNew()));

        KeyCombination kcSearchNote = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        parentStage.getScene().getAccelerators().put(kcSearchNote, () -> app.addTask(new ShowSearchForNoteDialog()));

        KeyCombination kcSearchReplaceString = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        parentStage.getScene().getAccelerators().put(kcSearchReplaceString, () -> {
            Tab t = taskUtil.getNotesContainer().getSelectionModel().getSelectedItem();
            Parent p = (Parent) t.getContent();
            ((TextAreaController) t.getUserData()).showSearchReplaceForm();
        });
    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
