package notepack;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.PopupController;
import notepack.app.task.NoteRename;
import notepack.app.task.NoteSave;
import notepack.app.task.WorkspaceRefresh;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public class SetNameController extends PopupController {

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Label workspaceLabel;

    @FXML
    private TextField noteName;

    private Note note;
    private App app;

    @FXML
    void onCancelBtn(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @FXML
    void onSaveBtn(ActionEvent event) {

        String name = noteName.getText();
        String oryginalName = note.getName();
        String extension = "";

        int indexPos = name.lastIndexOf('.');
        int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));

        if (indexPos > pos) {
            extension = name.substring(indexPos + 1);
        }
        if (extension.length() == 0) {

            if (oryginalName != null && oryginalName.length() > 0) {

                indexPos = oryginalName.lastIndexOf('.');
                pos = Math.max(oryginalName.lastIndexOf(File.separator), oryginalName.lastIndexOf('\\'));
                if (indexPos > pos) {
                    extension = oryginalName.substring(indexPos + 1);
                }
            }

            if (extension.length() > 0) {
                name = name + "." + extension;
            } else {
                name = name + ".txt";
            }

        }
        try {
            Paths.get(name);
        } catch (InvalidPathException ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Invalid filename");
            errorAlert.showAndWait();

            return;
        }


        if (note.getPath() != null) {
            app.addTask(new NoteRename(note, note.getWorkspace().getStorage().getBasePath() + File.separator + name));
        } else {
            note.setPath(note.getWorkspace().getStorage().getBasePath() + File.separator + name);
            app.addTask(new NoteSave(note));
        }

        app.addTask(new WorkspaceRefresh(note.getWorkspace()));

        getTaskUtil().closePopup();
    }

    public void initView(Note note, App app) {
        this.app = app;
        this.note = note;
        if (note.getName().length() > 0) {
            noteName.setText(note.getName());
        }

        workspaceLabel.setStyle("-fx-background-color: " + note.getWorkspace().getBackgroundColor());
        workspaceLabel.setText(note.getWorkspace().getName());

        Platform.runLater(() -> {
            noteName.requestFocus();
        });
    }


}
