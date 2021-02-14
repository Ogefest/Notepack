package notepack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Todo;

public class TodoNoteCellController {

    @FXML
    private Button noteButton;

    @FXML
    private Button editNote;

    @FXML
    private CheckBox doneCheckbox;

    @FXML
    private AnchorPane cellBackground;

    private Note note;
    private App app;
    private Todo todo;
//    private TodoWrapper todoWrapper;
    private TodoNoteCellCallback callback;

    public void setTodo(Todo todo, TodoNoteCellCallback callback) {
        this.app = app;
        this.todo = todo;
        this.callback = callback;

        doneCheckbox.setText(this.todo.getSummary());
        doneCheckbox.setSelected(this.todo.isFinished());

        if (this.todo.isFinished()) {
            cellBackground.getStyleClass().add("todo-finished");
        }

        doneCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                todo.setFinished(doneCheckbox.isSelected());

                callback.onTodoFinished(todo);

                if (TodoNoteCellController.this.todo.isFinished()) {
                    cellBackground.getStyleClass().add("todo-finished");
                } else {
                    cellBackground.getStyleClass().remove("todo-finished");
                }

            }
        });
    }

    public void setNoteButtonVisible(boolean value) {
        noteButton.setVisible(value);
    }

    @FXML
    void onShowNote(ActionEvent event) {
//        app.addTask(new NoteOpen(note));
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        editNote.setVisible(true);
    }

    @FXML
    void onMouseExited(MouseEvent event) {
        editNote.setVisible(false);
    }

    @FXML
    void onEditTodo(ActionEvent event) {
        callback.onTodoEdit(todo);
    }

}
