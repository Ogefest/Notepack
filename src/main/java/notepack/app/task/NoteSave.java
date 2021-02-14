package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.MainViewController;
import notepack.SaveAsController;
import notepack.Theme;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoteSave extends BaseTask implements Task, TypeNote,TypeGui {

    private Note note;

    public NoteSave(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() throws MessageError {
        if (note.getPath() != null) {
            note.saveToStorage();
            addTaskToQueue(new NoteMarkAsSaved(note));
            addTaskToQueue(new WorkspaceRefresh(note.getWorkspace()));
        }
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onSave(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        if (note.getPath() == null) {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/notepack/SaveAs.fxml"));

            Scene scene;
            try {
                Parent root = fxmlLoader.load();

                SaveAsController ctrl = fxmlLoader.getController();

                ctrl.setSaveAsCallback((name) -> {
                    note.setPath(name);
                    app.addTask(new NoteSave(note));

                });

                ctrl.setNote(note);

                scene = new Scene(root);

                scene.getStylesheets().clear();
                scene.getStylesheets().add(Theme.class.getResource("/notepack/color-definition.css" ).toExternalForm());


                Stage stage = new Stage();
                stage.setTitle("Set name");
                stage.setScene(scene);
                stage.initOwner(taskUtil.getStage().getScene().getWindow());
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initStyle(StageStyle.UTILITY);
                stage.setResizable(false);

                stage.showAndWait();

            } catch (IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        Tab t = taskUtil.getNoteTab(note);
        t.setText(note.getName());
        Label l = (Label) t.getGraphic();
        l.setText("");


//        app.refreshNotepad(note.getNotepad());
    }
}
