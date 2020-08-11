package notepack.app.task;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.MainViewController;
import notepack.SearchForNoteController;
import notepack.app.domain.App;
import notepack.app.domain.Task;

public class ShowSearchForNoteDialog extends BaseTask implements Task, TypeGui {

    @Override
    public void dispatch() {
    }

    public ShowSearchForNoteDialog() {
    }

    @Override
    public void proceed(Stage parentStage, App app) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/notepack/SearchForNote.fxml"));
        fxmlLoader.setResources(ResourceBundle.getBundle("notepack.fonts.FontAwesome"));

        Scene scene;
        try {
            Parent root = fxmlLoader.load();

            SearchForNoteController ctrl = (SearchForNoteController) fxmlLoader.getController();
            ctrl.setCallback((note) -> {
                app.openNote(note);
            });
            ctrl.setApp(app);

            scene = new Scene(root);
            Stage stage = new Stage();
            stage.setAlwaysOnTop(true);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initOwner(parentStage.getScene().getWindow());
            stage.setScene(scene);
            stage.showAndWait();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
