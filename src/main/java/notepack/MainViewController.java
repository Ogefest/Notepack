package notepack;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import notepack.app.domain.*;
import notepack.app.domain.exception.GuiNotReadyError;
import notepack.app.storage.JsonWorkspaceRepository;
import notepack.app.storage.PreferencesSettings;
import notepack.app.task.*;
import notepack.encrypt.SimpleAES;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setScene(Scene scene) {
        this.mainScene = scene;
    }
    
    public void appStart() {
        appSettings = new PreferencesSettings();
        SessionStorage sessionStorage = new JsonWorkspaceRepository(new SimpleAES(), appSettings);

        app = new App(sessionStorage, appSettings);
        app.getMessageBus().registerGuiListener((task) -> Platform.runLater(() -> {
            try {
                task.guiWork(taskUtil, app);
            } catch (GuiNotReadyError guiNotReadyError) {
                if (task instanceof TypeRecurring) {
                    ((TypeRecurring) task).startTaskAfterSecondsFromNow(5);
                }
                app.addTask((Task) task);
            }
        }));
    }
    
    public void windowRestore() {

        parentStage = (Stage) mainPane.getScene().getWindow();

        SplitPane notePaneBackground;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/NotePaneBackground.fxml"));
            notePaneBackground = loader.load();
            NotePaneBackgroundController ctrl = loader.getController();
            notePaneBackground.setUserData(ctrl);
            ctrl.setApp(app);

            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/notepack/SearchPaneBackground.fxml"));
            AnchorPane searchPaneBackground = loader2.load();
            SearchPaneController ctrl2 = loader2.getController();
            searchPaneBackground.setUserData(ctrl2);
            ctrl2.setApp(app);

            parentPane.getChildren().addAll(notePaneBackground, searchPaneBackground);

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

        for (Workspace workspace : app.getAvailableWorkspaces()) {
            if (workspace.getParam("is_archived").equals("1")) {
                continue;
            }
            app.openWorkspace(workspace);
        }
        
        if (app.getLastNotes().size() > 0) {
            for (Note note : app.getLastNotes()) {
                app.openNote(note);
            }
        }
        taskUtil = new TaskUtil(app, parentStage);

        String cssFile = appSettings.get("color-definition", "color-definition.css");

        app.getTheme().set(cssFile, mainScene);
        app.addTask(new InitializeShortcuts());
        app.addTask(new NewVersionCheck(hostServices));
        app.startDispatcher();
    }

    @FXML
    private void onNoteSearch(ActionEvent event) {
        app.addTask(new SearchPaneShow());
    }

    @FXML
    private void onWorkspaceCreate(ActionEvent event) {
        app.addTask(new WorkspacePopup());
    }

    @FXML
    private void onApplicationInfo(ActionEvent event) {
        app.addTask(new ShowApplicationInfo(hostServices));
    }

    @FXML
    private void onSwitchTheme(ActionEvent event) {
        app.addTask(new ToggleTheme(mainScene));
    }

    @FXML
    private void onClipboard(ActionEvent event) {
        app.addTask(new ClipboardPopup());
    }

    @FXML
    private void onShowTodo(ActionEvent event) {
        app.addTask(new ShowTodoView());
    }

    @FXML
    private void onShowNotes(ActionEvent event) {
        app.addTask(new ShowNotesView());
    }
}
