package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.gui.TaskUtil;

import java.time.LocalDate;

public class ReminderPaneController {

    @FXML
    private AnchorPane reminderPaneBackground;

    @FXML
    private Button btnRemoveReminder;

    @FXML
    private DatePicker calendarPicker;

    private App app;
    private Note note;
    private TaskUtil taskUtil;

    public void setAppNote(App app, Note note, TaskUtil taskUtil) {
        this.app = app;
        this.note = note;
        this.taskUtil = taskUtil;

        btnRemoveReminder.setVisible(false);

        LocalDate currentDate = note.getMeta().getReminder();
        if (currentDate != null) {
            calendarPicker.setValue(currentDate);
            btnRemoveReminder.setVisible(true);
        }
    }

    @FXML
    void onSaveBtn(ActionEvent event) {
        LocalDate date = calendarPicker.getValue();
        note.getMeta().setReminder(date);

        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onCancelBtn(ActionEvent event) {
        taskUtil.closePopup(reminderPaneBackground);
    }

    @FXML
    void onRemoveReminderBtn(ActionEvent event) {
        note.getMeta().removeReminder();
        taskUtil.closePopup(reminderPaneBackground);
    }

}
