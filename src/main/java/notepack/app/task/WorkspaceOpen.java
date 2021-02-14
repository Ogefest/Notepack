package notepack.app.task;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.MainViewController;
import notepack.WorkspaceTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.listener.WorkspaceListener;
import notepack.gui.TabWorkspace;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkspaceOpen implements Task, TypeWorkspace,TypeGui {

    private Workspace workspace;
    
    public WorkspaceOpen(Workspace workspace) {
        this.workspace = workspace;
    }
    
    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(WorkspaceListener listener) {
        listener.onOpen(workspace);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        TabPane workspaceContainer = taskUtil.getWorkspaceContainer();

        Tab tab = new TabWorkspace();
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/WorkspaceTabListView.fxml"));
            Node tabContent = loader.load();
            WorkspaceTabController ctrl = loader.getController();
            tab.setContent(tabContent);
            ctrl.setWorkspace(workspace);
            ctrl.setApp(app);
            ctrl.getTreeView().setOnMouseClicked((t) -> {
                if (t.getClickCount() == 2) {
                    Note note = ctrl.getSelectedNote();
                    if (note != null) {
                        app.openNote(note);
                    }
                }
            });

            ContextMenu contextMenu = new ContextMenu();
            MenuItem closeWorkspaceMenu = new MenuItem("Close");
            closeWorkspaceMenu.setOnAction((t) -> app.closeWorkspace(workspace));

            MenuItem refreshWorkspaceMenu = new MenuItem("Refresh");
            refreshWorkspaceMenu.setOnAction((t) -> app.refreshWorkspace(workspace));

            MenuItem configureWorkspaceMenu = new MenuItem("Settings");
            configureWorkspaceMenu.setOnAction((t) -> ctrl.openWorkspaceEdit(workspace));

            contextMenu.getItems().addAll(closeWorkspaceMenu, refreshWorkspaceMenu, configureWorkspaceMenu);
            tab.setContextMenu(contextMenu);

            tab.setUserData(ctrl);
            tab.setText(workspace.getName());

            String workspaceColor = workspace.getBackgroundColor();
            tab.setStyle("-fx-background-color: " + workspaceColor + ";-fx-border-color:" + workspaceColor);

            app.refreshWorkspace(workspace);

            Platform.runLater(() -> {

                workspaceContainer.getTabs().add(tab);
                workspaceContainer.getSelectionModel().select(tab);
                ctrl.refreshTreeView();

            });

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
