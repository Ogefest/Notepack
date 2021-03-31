package notepack.app.task;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TagColor;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

public class TagRefresh implements Task, TypeGui {

    private Note note;

    public TagRefresh(Note note) {
        this.note = note;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) throws GuiNotReadyError {

        if (note.getMeta().getTags().size() == 0) {
            return;
        }

        NoteRenderController ctrl = (NoteRenderController) taskUtil.getNoteTab(note).getUserData();
        if (ctrl == null) {
            return;
        }

        Pane tags = ctrl.getTagContainer();
        if (tags == null) {
            return;
        }

        tags.getChildren().clear();

        for (String tag : note.getMeta().getTags()) {

            Label tagLabel = new Label();
            tagLabel.setText(tag);
            tagLabel.getStyleClass().add("tag-label");

            String color = TagColor.get(tag);
            tagLabel.setStyle("-fx-background-color: " + color);

            tags.getChildren().add(tagLabel);
        }

    }

    @Override
    public void backgroundWork() throws MessageError {

    }
}
