package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Todo;
import notepack.app.domain.TodoWrapper;
import notepack.app.task.TodoPopup;
import notepack.noterender.NoteRenderController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


public class TodoPaneBackgroundController implements Initializable, NoteRenderController {

    @FXML
    private AnchorPane todoBackground;

    @FXML
    private VBox listviewContainer;

    @FXML
    private TextField filterInput;

    private App app;
    private Note note;
    private DateTimeFormatter formatter;

    private TodoWrapper todoWrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterInput.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshTodolist();
        });
        filterInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                filterInput.setText("");
                filterInput.setVisible(false);
            }
        });
    }

    public void refreshTodolist() {

        listviewContainer.getChildren().clear();

        todoWrapper = new TodoWrapper(note);

        ArrayList<Todo> allTodos = todoWrapper.getTodos();
        if (allTodos.size() == 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        HashMap<LocalDate, ArrayList<Todo>> groups = new HashMap<>();
        LocalDate currentKey;
        ArrayList<LocalDate> keys = new ArrayList<>();
        String filterKey = filterInput.getText().toLowerCase();

        for (Todo n : allTodos) {

            if (filterKey.length() > 0) {
                if (!n.getSummary().toLowerCase().contains(filterKey)) {
                    continue;
                }
            }

            LocalDate dueDate = n.getDueDate();
            LocalDate completedDate = n.getCompleteDate();
            /*
            skip finished tasks from past
             */
            if (filterKey.length() == 0 && n.isFinished()) {
                if (completedDate == null || today.compareTo(completedDate) > 0) {
                    continue;
                }
            }

            if (n.isFinished()) {
                currentKey = completedDate;
            } else {
                if (dueDate == null || today.compareTo(dueDate) >= 0) {
                    currentKey = LocalDate.now();
                } else {
                    currentKey = dueDate;
                }
            }


            if (!groups.containsKey(currentKey)) {
                groups.put(currentKey, new ArrayList<>());
            }

            groups.get(currentKey).add(n);
            if (!keys.contains(currentKey)) {
                keys.add(currentKey);
            }

        }

        keys.sort((o1, o2) -> {
            return o1.compareTo(o2);
        });

        for (LocalDate d : keys) {
            String groupLabel = "";
            if (d.compareTo(LocalDate.now()) <= 0) {
                if (filterInput.getText().length() > 0) {
                    groupLabel = d.format(formatter);
                } else {
                    groupLabel = "Today";
                }
            } else {
                groupLabel = d.format(formatter);
            }

            addListview(groupLabel, groups.get(d));
        }

    }

    private void addListview(String header, ArrayList<Todo> group) {

        Label label = new Label();
        label.setText(header);
        label.getStyleClass().add("summary");
        label.setStyle("-fx-background: red");
        listviewContainer.getChildren().add(label);
        for (Todo n : group) {
            addTodo(n);
        }

    }

    private void addTodo(Todo todo) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TodoNoteCell.fxml"));
            AnchorPane pane = loader.load();

            TodoNoteCellController ctrl = loader.getController();

            ctrl.setTodo(todo, new TodoNoteCellCallback() {
                @Override
                public void onTodoFinished(Todo todo) {
                    todoWrapper.setTodo(todo);
                }

                @Override
                public void onTodoEdit(Todo todo) {
                    app.addTask(new TodoPopup(note, todo));
                }
            });
            if (note != null) {
                ctrl.setNoteButtonVisible(false);
            }

            listviewContainer.getChildren().add(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setState(App app, Note note) {
        formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        this.app = app;
        this.note = note;

        todoWrapper = new TodoWrapper(note);
        todoBackground.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());
    }

    @Override
    public void noteActivated() {
        refreshTodolist();
    }

    @Override
    public void noteDeactivated() {

    }

    public void onCloseNote(ActionEvent actionEvent) {
        app.closeNote(note);
    }

    public void onTaskCreate(ActionEvent actionEvent) {
        app.addTask(new TodoPopup(note));
    }

    public void onTaskSearch(ActionEvent actionEvent) {
        toggleSearchInput();
    }

    public void toggleSearchInput() {
        if (filterInput.isVisible()) {
            filterInput.setVisible(false);
            filterInput.setText("");
        } else {
            filterInput.setVisible(true);
            filterInput.requestFocus();
        }
    }
}
