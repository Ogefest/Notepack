package notepack.gui;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.task.TodoRefresh;
import notepack.noterender.NoteRenderController;

import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class TaskUtil {

    private App app;
    private Stage stage;

    private HashMap<Notepad, Tab> notepads;

    public TaskUtil(App app, Stage stage) {
        this.app = app;
        this.stage = stage;

        refreshNotepads();
        initEventListener();
    }

    private void initEventListener() {

        /*
        thread to initialize event listener to refresh notepads
        on application startup #notepadTabContainer not exists
        so thread with wait 1000ms until ui element occur
         */
        TimerTask task = new TimerTask() {
            public void run() {
                do {
                    TabPane container = (TabPane) stage.getScene().lookup("#notepadTabContainer");
                    if (container == null) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    container.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (oldValue.equals(newValue)) {
                            return;
                        }
                        refreshNotepads();
                    });
                    break;

                } while (true);
            }
        };
        Timer timer = new Timer("Timer");
        long delay = 1000L;
        timer.schedule(task, delay);
    }

    private void refreshNotepads() {
        notepads = new HashMap<>();

        Scene scene = stage.getScene();

        TabPane container = (TabPane) scene.lookup("#notepadTabContainer");
        if (container == null) {
            return;
        }

        for (Tab tab : container.getTabs()) {
            if (tab instanceof TabNotepad) {
                NotebookTabController ctrl = (NotebookTabController) tab.getUserData();

                notepads.put(ctrl.getNotepad(), tab);
            }
        }
    }

    public Tab getNotepadTab(Notepad notepad) {
        if (!notepads.containsKey(notepad)) {
            return null;
        }
        return notepads.get(notepad);
    }

    public Tab getNoteTab(Note note) {
        TabPane tabContainer = getNotesContainer();

        for (Tab t : tabContainer.getTabs()) {
            NoteRenderController ctrl = (NoteRenderController) t.getUserData();
            if (ctrl.getNote().getIdent().equals(note.getIdent())) {

                return t;
            }
        }
        return null;
    }

    public TabPane getNotepadContainer() {
        return (TabPane) stage.getScene().lookup("#notepadTabContainer");
    }

    public TabPane getNotesContainer() {
        return (TabPane) stage.getScene().lookup("#noteTabContainer");
    }

    public StackPane getParentPane() {
        return (StackPane) stage.getScene().lookup("#parentPane");
    }

    public BorderPane getTodoPane() {
        return (BorderPane) stage.getScene().lookup("#todoBackground");
    }

    public SplitPane getNotePane() {
        return (SplitPane) stage.getScene().lookup("#mainBackground");
    }

    public void showTodoPane() {

        if (getParentPane().getChildren().get(0).getId().equals("todoBackground")) {
            app.addTask(new TodoRefresh());
            getParentPane().getChildren().get(0).toFront();
        }
    }

    public void showNotesPane() {

        if (getParentPane().getChildren().get(0).getId().equals("mainBackground")) {
            getParentPane().getChildren().get(0).toFront();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Notepad getCurrentNotepad() {
        TabPane container = getNotepadContainer();
        Tab t = container.getSelectionModel().getSelectedItem();
        return ((NotebookTabController) t.getUserData()).getNotepad();
    }

    public Note getCurrentNote() {
        TabPane container = getNotesContainer();
        Tab t = container.getSelectionModel().getSelectedItem();
        return ((NoteRenderController) t.getUserData()).getNote();
    }

    public void openPopup(Node node) {

        StackPane parent = getParentPane();

        BorderPane bPane = new BorderPane();
        bPane.setStyle("-fx-background-color: rgba(50, 50, 50, 0.8);");
        bPane.setId("popup-parent");
        node.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                parent.getChildren().remove(bPane);
            }
        });

        bPane.setCenter(node);
        bPane.prefWidthProperty().bind(parent.prefWidthProperty());
        bPane.prefHeightProperty().bind(parent.prefHeightProperty());

        parent.getChildren().add(bPane);
        node.requestFocus();

        TabPane container = getNotesContainer();
        Tab t = container.getSelectionModel().getSelectedItem();
        ((NoteRenderController) t.getUserData()).noteDeactivated();
    }

    synchronized public void closePopup() {
        StackPane parent = getParentPane();

        Optional<Node> popupNode = parent.getChildren().stream().filter(node1 -> node1.getId().equals("popup-parent")).findFirst();
        if (popupNode.isPresent()) {
            parent.getChildren().remove(popupNode.get());
        }

        TabPane container = getNotesContainer();
        Tab t = container.getSelectionModel().getSelectedItem();
        ((NoteRenderController) t.getUserData()).noteActivated();

    }

}
