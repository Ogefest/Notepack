package notepack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import notepack.app.domain.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoPopupController extends PopupController {

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

    @FXML
    private Label headerLabel;

    private App app;
    private Note note;
    private Todo todo;
    private DateTimeFormatter formatter;

    public void setAppNote(Todo todo, Note note) {

        this.note = note;

        this.todo = todo;

        taskSummary.setText(todo.getSummary());
        if (todo.getSummary() != null) {
            headerLabel.setText("Edit todo");
        }

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

        getTaskUtil().closePopup();
    }

    @FXML
    void onCancelBtn(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @FXML
    void onRemoveReminderBtn(ActionEvent event) {
        TodoWrapper wrapper = new TodoWrapper(note);
        wrapper.removeTodo(todo);

        getTaskUtil().closePopup();
    }

}
