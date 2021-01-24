package notepack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Todo;
import notepack.app.task.NoteOpen;
import notepack.gui.Icon;

public class TodoNoteCellController {

    @FXML
    private Label notepadLabel;

    @FXML
    private CheckBox doneCheckbox;

    @FXML
    private AnchorPane cellBackground;



    private Note note;
    private App app;
    private Todo todo;

    public void setNote(Note note, App app) {
        this.app = app;
        this.note = note;
        todo = note.getMeta().getTodo();

        notepadLabel.setText(note.getNotepad().getName());
        doneCheckbox.setText(todo.getSummary());
        doneCheckbox.setSelected(todo.isFinished());

        notepadLabel.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());
        if (todo.isFinished()) {
            cellBackground.getStyleClass().add("todo-finished");
        }

        doneCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                todo.setFinished(doneCheckbox.isSelected());
                note.getMeta().setTodo(todo);

                if (todo.isFinished()) {
                    cellBackground.getStyleClass().add("todo-finished");
                } else {
                    cellBackground.getStyleClass().remove("todo-finished");
                }

            }
        });

    }

    @FXML
    void onShowNote(ActionEvent event) {
        app.addTask(new NoteOpen(note));
    }

}
