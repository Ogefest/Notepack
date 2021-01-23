package notepack.app.task;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import notepack.MainViewController;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.NoteListener;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;
import notepack.noterender.Render;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NoteOpen extends BaseTask implements Task, TypeNote, TypeGui {

    private Note note;

    public NoteOpen(Note note) {
        this.note = note;
    }

    @Override
    public void backgroundWork() throws MessageError {
        try {
            note.readFromStorage();
        } catch(MessageError ex) {
            messageBus.addTask(new NoteClose(note));
            
            throw ex;
        }
    }

    @Override
    public void notify(NoteListener listener) {
        listener.onOpen(note);
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        taskUtil.showNotesPane();
        TabPane tabContainer = taskUtil.getNotesContainer();

        Tab tab = taskUtil.getNoteTab(note);
        if (tab != null) {
            tabContainer.getSelectionModel().select(tab);
            return;
        }


        Tab newTab = new Tab();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(Render.getFxml(note)));
        Node tabContent;
        try {
            tabContent = loader.load();

            NoteRenderController ctrl = loader.getController();
            ctrl.setState(app, note);

            ContextMenu contextMenu = new ContextMenu();
            MenuItem closeNoteMenu = new MenuItem("Close");
            closeNoteMenu.setOnAction((t) -> app.closeNote(note));

            MenuItem saveNoteMenu = new MenuItem("Save");
            saveNoteMenu.setOnAction((t) -> app.saveNote(note));

            contextMenu.getItems().addAll(saveNoteMenu, closeNoteMenu);
            newTab.setContextMenu(contextMenu);

            newTab.setContent(tabContent);
            newTab.setUserData(ctrl);

            String notepadColor = note.getNotepad().getBackgroundColor();
            newTab.setStyle("-fx-background-color: " + notepadColor + ";-fx-border-color:" + notepadColor);
            if (note.getName().length() > 0) {
                newTab.setText(note.getName());
            }
            newTab.setGraphic(new Label(""));

            tabContainer.getTabs().add(newTab);
            tabContainer.getSelectionModel().select(newTab);
        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }



    }
}
