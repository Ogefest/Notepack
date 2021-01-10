package notepack.app.task;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.MainViewController;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.listener.NotepadListener;
import notepack.gui.TabNotepad;
import notepack.gui.TaskUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotepadOpen implements Task,TypeNotepad,TypeGui {

    private Notepad notepad;
    
    public NotepadOpen(Notepad notepad) {
        this.notepad = notepad;
    }
    
    @Override
    public void backgroundWork() {
    }

    @Override
    public void notify(NotepadListener listener) {
        listener.onOpen(notepad);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        TabPane notepadContainer = taskUtil.getNotepadContainer();

        Tab tab = new TabNotepad();
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notepack/NotepadTabListView.fxml"));
            Node tabContent = loader.load();
            NotebookTabController ctrl = loader.getController();
            tab.setContent(tabContent);
            ctrl.setNotepad(notepad);
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
            MenuItem closeNotepadMenu = new MenuItem("Close");
            closeNotepadMenu.setOnAction((t) -> app.closeNotepad(notepad));

            MenuItem refreshNotepadMenu = new MenuItem("Refresh");
            refreshNotepadMenu.setOnAction((t) -> app.refreshNotepad(notepad));

            MenuItem configureNotepadMenu = new MenuItem("Settings");
            configureNotepadMenu.setOnAction((t) -> ctrl.openNotepadEdit(notepad));

            contextMenu.getItems().addAll(closeNotepadMenu, refreshNotepadMenu, configureNotepadMenu);
            tab.setContextMenu(contextMenu);

            tab.setUserData(ctrl);
            tab.setText(notepad.getName());

            String notepadColor = notepad.getBackgroundColor();
            tab.setStyle("-fx-background-color: " + notepadColor + ";-fx-border-color:" + notepadColor);

            app.refreshNotepad(notepad);

            Platform.runLater(() -> {

                notepadContainer.getTabs().add(tab);
                notepadContainer.getSelectionModel().select(tab);
                ctrl.refreshTreeView();

            });

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
