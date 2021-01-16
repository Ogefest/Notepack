package notepack;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.SessionStorage;
import notepack.app.domain.Settings;
import notepack.app.listener.NoteListener;
import notepack.app.storage.JsonNotepadRepository;
import notepack.app.storage.PreferencesSettings;
import notepack.app.task.*;
import notepack.gui.Icon;
import notepack.encrypt.SimpleAES;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;
import notepack.noterender.Render;
import notepack.noterender.TextAreaController;

/**
 * FXML Controller class
 *
 */
public class MainViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private App app;
    private Stage parentStage;
    
    private Settings appSettings;
    
    @FXML
    private AnchorPane mainPane;

    @FXML
    private StackPane parentPane;
    
    public HostServices hostServices;
    
    private Scene mainScene;

    private TaskUtil taskUtil;
    
    private Theme theme;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setScene(Scene scene) {
        this.mainScene = scene;
    }
    
    public void appStart() {
        appSettings = new PreferencesSettings();

        SessionStorage sessionStorage = new JsonNotepadRepository(new SimpleAES(), appSettings);
        
        app = new App(sessionStorage, appSettings);
        theme = app.getTheme();

        app.getMessageBus().registerGuiListener((task) -> Platform.runLater(() -> task.guiWork(taskUtil, app)));
    }
    
    public void windowRestore() {

        parentStage = (Stage) mainPane.getScene().getWindow();

        SplitPane notePaneBackground;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/NotePaneBackground.fxml"));
            notePaneBackground = loader.load();
            NotePaneBackgroundController ctrl = loader.getController();
            ctrl.setApp(app);

            parentPane.getChildren().add(notePaneBackground);

        } catch (IOException e) {
            e.printStackTrace();
        }


        parentStage.setOnCloseRequest((t) -> {
            app.terminate();
            
            appSettings.set("window.x", Double.toString(parentStage.getX()));
            appSettings.set("window.y", Double.toString(parentStage.getY()));
            appSettings.set("window.width", Double.toString(parentStage.getWidth()));
            appSettings.set("window.height", Double.toString(parentStage.getHeight()));
            appSettings.set("window.is_maximized", parentStage.isMaximized() ? "1" : "0");
        });
        
        double x = Double.parseDouble(appSettings.get("window.x", "90"));
        double y = Double.parseDouble(appSettings.get("window.y", "70"));
        double width = Double.parseDouble(appSettings.get("window.width", "900"));
        double height = Double.parseDouble(appSettings.get("window.height", "700"));
        
        parentStage.setX(x);
        parentStage.setY(y);
        parentStage.setWidth(width);
        parentStage.setHeight(height);
        
        if (appSettings.get("window.is_maximized", "0").equals("1")) {
            parentStage.setMaximized(true);
        }

        for (Notepad notepad : app.getAvailableNotepads()) {
            app.openNotepad(notepad);
        }
        
        if (app.getLastNotes().size() > 0) {
            for (Note note : app.getLastNotes()) {
                app.openNote(note);
            }
        }

        taskUtil = new TaskUtil(app, parentStage);


        String cssFile = appSettings.get("color-definition", "color-definition.css");
        theme.set(cssFile, mainScene);

        app.addTask(new InitializeShortcuts());
        app.startDispatcher();
    }

    @FXML
    private void onNoteSearch(ActionEvent event) {
        app.addTask(new ShowSearchForNoteDialog());
    }
    
    @FXML
    private void onApplicationInfo(ActionEvent event) {
        app.addTask(new ShowApplicationInfo(hostServices));
    }
    
    @FXML
    private void onSwitchTheme(ActionEvent event) {
        app.addTask(new ToggleTheme(mainScene));
    }

}
