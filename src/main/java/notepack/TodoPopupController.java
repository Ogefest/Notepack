package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Todo;
import notepack.gui.TaskUtil;

import java.time.LocalDate;

public class TodoPopupController {

    @FXML
    private AnchorPane reminderPaneBackground;

    @FXML
    private Button btnRemoveReminder;

    @FXML
    private DatePicker taskDueDate;

    @FXML
    private TextField taskSummary;

    @FXML
    private CheckBox taskDone;

    private App app;
    private Note note;
    private TaskUtil taskUtil;

    public void setAppNote(App app, Note note, TaskUtil taskUtil) {
        this.app = app;
        this.note = note;
        this.taskUtil = taskUtil;

        btnRemoveReminder.setVisible(false);

        Todo todo = note.getMeta().getTodo();
        if (todo != null) {
            taskDueDate.setValue(todo.getDueDate());

            String textSummary  = todo.getSummary();
            if (textSummary.length() == 0) {
                textSummary = note.getName();
            }
            taskSummary.setText(textSummary);
            taskDone.setSelected(todo.isFinished());
            btnRemoveReminder.setVisible(true);
        } else {
            taskSummary.setText(note.getName());
        }
    }

    @FXML
    void onSaveBtn(ActionEvent event) {
        LocalDate date = taskDueDate.getValue();
        Todo todo = new Todo();
        todo.setDueDate(date);
        todo.setSummary(taskSummary.getText());
        todo.setFinished(taskDone.isSelected());

        note.getMeta().setTodo(todo);

        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onCancelBtn(ActionEvent event) {
        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onRemoveReminderBtn(ActionEvent event) {
        note.getMeta().removeTodo();
        taskUtil.closePopup(reminderPaneBackground);
    }

}
