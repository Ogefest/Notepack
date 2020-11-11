package notepack;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;

public class MainViewGuiAction {

    private App app;
    private Stage stage;

    public MainViewGuiAction(Stage mainStage, App app) {
        this.app = app;
        this.stage = mainStage;
    }

    public void showSearchForNoteDialog() {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("SearchForNote.fxml"));
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
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void closeNotepad(Notepad notepad) {

    }
    
    public void closeCurrentNote() {
        
    }

}
