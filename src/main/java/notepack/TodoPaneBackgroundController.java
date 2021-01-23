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
import java.util.ResourceBundle;

public class TodoPaneBackgroundController implements Initializable {

    @FXML
    private BorderPane todoBackground;

    @FXML
    private VBox listviewContainer;

    @FXML
    private TextField filterInput;

    private App app;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setApp(App app) {
        this.app = app;
    }

    public void refreshTodolist() {

        listviewContainer.getChildren().clear();

        addListview("Dzisiejsze");
        addListview("Soon");

    }

    private void addListview(String header) {

        Label label = new Label();
        label.setText(header);
//        label.setStyle("-fx-font-size: 30");
        label.getStyleClass().add("summary");
        listviewContainer.getChildren().add(label);
        for (Note n : app.getNotesWithTodo()) {
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
