package notepack;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Todo;
import notepack.gui.Icon;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class TodoPaneBackgroundController implements Initializable {

    @FXML
    private BorderPane todoBackground;

    @FXML
    private VBox listviewContainer;

    @FXML
    private TextField filterInput;

    private App app;
    private DateTimeFormatter formatter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterInput.textProperty().addListener((observable, oldValue, newValue) -> {
            refreshTodolist();
        });
    }

    public void setApp(App app) {
        this.app = app;
        formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    }

    public void refreshTodolist() {

        listviewContainer.getChildren().clear();

        ArrayList<Note> allTodos = app.getNotesWithTodo();
        if (allTodos.size() == 0) {
            return;
        }

        LocalDate today = LocalDate.now();

        HashMap<LocalDate, ArrayList<Note>> groups = new HashMap<>();
        LocalDate currentKey;
        ArrayList<LocalDate> keys = new ArrayList<>();
        String filterKey = filterInput.getText().toLowerCase();

        for (Note n : allTodos) {

            if (filterKey.length() > 0) {
                if (!n.getMeta().getTodo().getSummary().toLowerCase().contains(filterKey)) {
                    continue;
                }
            }

            LocalDate todoDate = n.getMeta().getTodo().getDueDate();
            /*
            skip finished tasks from past
             */
            if (n.getMeta().getTodo().isFinished()) {
                if (today.compareTo(todoDate) > 0) {
                    continue;
                }
            }


            if (todoDate == null || today.compareTo(todoDate) >= 0) {
                currentKey = LocalDate.now();
            } else {
                currentKey = todoDate;
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
                groupLabel = "Today";
            } else {
                groupLabel = d.format(formatter);
            }

            addListview(groupLabel, groups.get(d));
        }

    }

    private void addListview(String header, ArrayList<Note> group) {

        Label label = new Label();
        label.setText(header);
        label.getStyleClass().add("summary");
        listviewContainer.getChildren().add(label);
        for (Note n : group) {
            addNote(n);
        }

    }

    private void addNote(Note note) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TodoNoteCell.fxml"));
            AnchorPane pane = loader.load();

            TodoNoteCellController ctrl = loader.getController();
            ctrl.setNote(note, app);

            listviewContainer.getChildren().add(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
