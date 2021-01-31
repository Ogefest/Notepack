package notepack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import notepack.app.domain.*;
import notepack.gui.TaskUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private Todo todo;
    private TaskUtil taskUtil;
    private DateTimeFormatter formatter;

    public void setAppNote(Todo todo, Note note, TaskUtil taskUtil) {

        this.note = note;
        this.taskUtil = taskUtil;

        this.todo = todo;

        taskSummary.setText(todo.getSummary());

        formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        taskDueDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                if (object == null) {
                    return "";
                }
                return formatter.format(object);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, formatter);
            }
        });

        if (todo.getSummary() == null || todo.getSummary().length() > 0) {
            btnRemoveReminder.setVisible(false);
        }
        taskDone.setSelected(todo.isFinished());
        taskDueDate.setValue(todo.getDueDate());

        Platform.runLater(() -> {
            taskSummary.requestFocus();
        });
    }

    @FXML
    void onSaveBtn(ActionEvent event) {
        LocalDate date = taskDueDate.getValue();

        todo.setDueDate(date);
        todo.setSummary(taskSummary.getText());
        todo.setFinished(taskDone.isSelected());

        TodoWrapper wrapper = new TodoWrapper(note);
        wrapper.setTodo(todo);

        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onCancelBtn(ActionEvent event) {
        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onRemoveReminderBtn(ActionEvent event) {
        TodoWrapper wrapper = new TodoWrapper(note);
        wrapper.removeTodo(todo);

        taskUtil.closePopup(reminderPaneBackground);
    }

}
