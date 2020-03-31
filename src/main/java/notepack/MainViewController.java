/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

//import notepack.gui.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Settings;
import notepack.app.event.NoteChanged;
import notepack.app.event.NoteOpened;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.storage.Filesystem;
import notepack.app.storage.PreferencesSettings;
import notepack.app.task.OpenNote;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class MainViewController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private App app;
    private Stage stage;

    private Settings appSettings;

    @FXML
    private TabPane tabContainer;
    @FXML
    private AnchorPane mainPane;

    private Filesystem recentFiles;
    @FXML
    private TabPane notepadContainer;
    @FXML
    private MenuItem menuEditUndo;
    @FXML
    private MenuItem menuEditRedo;
    @FXML
    private MenuItem menuEditCut;
    @FXML
    private MenuItem menuEditCopy;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        app = new App();
        appSettings = new PreferencesSettings();

        app.getMessageBus().registerNoteListener(new NoteListener() {
            @Override
            public void onOpen(Note note) {
                final Tab newTab;
                try {
                    newTab = FXMLLoader.load(getClass().getResource("NoteTab.fxml"));
                    newTab.setUserData(note);
                    newTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event t) {
                            app.closeNote(note);
                        }
                    });
                    if (note.getName().length() > 0) {
                        newTab.setText(note.getName());
                    }

                    Platform.runLater(() -> {
                        tabContainer.getTabs().add(newTab);
                        tabContainer.getSelectionModel().select(newTab);
                        Parent p = (Parent) newTab.getContent();
                        TextArea ta = (TextArea) p.getChildrenUnmodifiable().get(1);
                        ta.setText(note.getContent());
                        ta.textProperty().addListener((ov, oldValue, newValue) -> {
                            app.changeNote(note, newValue);
                            
                            menuEditUndo.setDisable(true);
                            if (ta.isUndoable()) {
                                menuEditUndo.setDisable(false);
                            }
                            menuEditRedo.setDisable(true);
                            if (ta.isRedoable()) {
                                menuEditRedo.setDisable(false);
                            }
                            

                        });
                        ta.selectionProperty().addListener((o) -> {
                            menuEditCut.setDisable(true);
                            menuEditCopy.setDisable(true);
                            if (ta.getSelectedText().length() > 0) {
                                menuEditCut.setDisable(false);
                                menuEditCopy.setDisable(false);
                            }                            
                        });
                        
                        ta.requestFocus();
                    });

                } catch (IOException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onClose(Note n) {

                Platform.runLater(() -> {
                    for (Tab t : tabContainer.getTabs()) {
                        if (t.getUserData().equals(n)) {
                            tabContainer.getTabs().remove(t);

                            break;
                        }
                    }
                });

            }

            @Override
            public void onChange(Note n) {

                Platform.runLater(() -> {
                    for (Tab t : tabContainer.getTabs()) {
                        if (t.getUserData().equals(n)) {
                            if (n.getContent().equals(getTextAreaForNote(n).getText())) {
                                t.setStyle("-fx-text-base-color: green;");
                            } else {
                                t.setStyle("-fx-text-base-color: red;");
                            }
                        }
                    }
                });

            }

            @Override
            public void onSave(Note n) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        app.getMessageBus().registerNotepadListener(new NotepadListener() {
            @Override
            public void onOpen(Notepad notepad) {
                Tab tab;
                try {
                    tab = FXMLLoader.load(getClass().getResource("NotepadTabListView.fxml"));
                    tab.setUserData(notepad);
                    tab.setText(notepad.getName());

                    Platform.runLater(() -> {

                        notepadContainer.getTabs().add(tab);
                        notepadContainer.getSelectionModel().select(tab);

                    });

                    MenuItem closeNotepad = new MenuItem("Close");
                    closeNotepad.setUserData(notepad);
                    closeNotepad.setOnAction((t) -> {
                        Notepad contexNotepad = (Notepad) ((MenuItem) t.getSource()).getParentPopup().getUserData();
                        app.closeNotepad(contexNotepad);
                    });
                    tab.getContextMenu().getItems().add(closeNotepad);

                    ArrayList<String> noteNames = notepad.getStorage().list();
                    Parent p = (Parent) tab.getContent();
                    ListView<Note> n = (ListView<Note>) p.getChildrenUnmodifiable().get(0);

                    for (String noteName : noteNames) {
                        n.getItems().add(new Note(noteName, notepad.getStorage()));
                    }

                    n.setOnMouseClicked((t) -> {
                        if (t.getClickCount() == 2) {
                            app.openNote(n.getSelectionModel().getSelectedItem());
                        }
                    });

                } catch (IOException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onClose(Notepad notepad) {

                Platform.runLater(() -> {
                    for (Tab t : notepadContainer.getTabs()) {
                        if (t.getUserData().equals(notepad)) {
                            notepadContainer.getTabs().remove(t);
                            break;
                        }
                    }
                });
            }

            @Override
            public void onNotesListUpdated(Notepad notepad) {
            }
        });

    }

    public void windowRestore() {
        stage = (Stage) mainPane.getScene().getWindow();

        stage.setOnCloseRequest((t) -> {
            app.terminate();

            appSettings.set("window.x", Double.toString(stage.getX()));
            appSettings.set("window.y", Double.toString(stage.getY()));
            appSettings.set("window.width", Double.toString(stage.getWidth()));
            appSettings.set("window.height", Double.toString(stage.getHeight()));
            appSettings.set("window.is_maximized", stage.isMaximized() ? "1" : "0");
        });

        double x = Double.parseDouble(appSettings.get("window.x", "90"));
        double y = Double.parseDouble(appSettings.get("window.y", "70"));
        double width = Double.parseDouble(appSettings.get("window.width", "900"));
        double height = Double.parseDouble(appSettings.get("window.height", "700"));

        stage.setX(x);
        stage.setY(y);
        stage.setWidth(width);
        stage.setHeight(height);

        if (appSettings.get("window.is_maximized", "0").equals("1")) {
            stage.setMaximized(true);
        }

        for (Notepad notepad : app.getAvailableNotepads()) {
            app.openNotepad(notepad);
        }

        app.newNote(new Filesystem());
    }

    private TextArea getTextAreaForNote(Note n) {
        for (Tab t : tabContainer.getTabs()) {
            if (t.getUserData().equals(n)) {
                Parent p = (Parent) t.getContent();
                return (TextArea) p.getChildrenUnmodifiable().get(1);
            }
        }
        return null;
    }

    private TextArea getCurrentTextArea() {
        Tab t = tabContainer.getSelectionModel().getSelectedItem();
        Parent p = (Parent) t.getContent();
        return (TextArea) p.getChildrenUnmodifiable().get(1);
    }

    private Note getCurrentNote() {
        Tab t = tabContainer.getSelectionModel().getSelectedItem();
        return (Note) t.getUserData();
    }

    @FXML
    private void onFileOpen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File f = chooser.showOpenDialog(stage);
        if (f != null) {
            Note note = new Note(f.getAbsolutePath(), new Filesystem());
            app.getMessageBus().addTask(new OpenNote(note));
        }
    }

    @FXML
    private void onFileSave(ActionEvent event) {

        Note n = getCurrentNote();
        if (n.getPath() == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save note");

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                n.setPath(file.getAbsolutePath());
                app.saveNote(n);
            }
        } else {
            app.saveNote(n);
        }

    }

    @FXML
    private void onFileClose(ActionEvent event) {
        app.closeNote(getCurrentNote());
    }

    @FXML
    private void onFileNew(ActionEvent event) {
        app.newNote(new Filesystem());
    }

    @FXML
    private void onEditUndo(ActionEvent event) {
        getCurrentTextArea().undo();
    }

    @FXML
    private void onEditRedo(ActionEvent event) {
        getCurrentTextArea().redo();
    }

    @FXML
    private void onEditCut(ActionEvent event) {
        getCurrentTextArea().cut();
    }

    @FXML
    private void onEditCopy(ActionEvent event) {
        getCurrentTextArea().copy();
    }

    @FXML
    private void onEditPaste(ActionEvent event) {
        getCurrentTextArea().paste();
    }

    @FXML
    private void onEditDelete(ActionEvent event) {
        getCurrentTextArea().deleteText(getCurrentTextArea().getSelection());
    }

    @FXML
    private void onEditSelectAll(ActionEvent event) {
        getCurrentTextArea().selectAll();
    }

    @FXML
    private void onFileNotepadAdd(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("NotepadCreate.fxml"));

        Scene scene;
        try {
            Parent root = fxmlLoader.load();

            NotepadCreateController ctrl = (NotepadCreateController) fxmlLoader.getController();
            ctrl.setNotepadCreateCallback(new NotepadCreateCallback() {
                @Override
                public void added(Notepad notepad) {
                    app.openNotepad(notepad);
                }
            });

            scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Add new notepad");
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void onFileNotepadRemove(ActionEvent event) {
        Note n = (Note) tabContainer.getSelectionModel().getSelectedItem().getUserData();
        app.closeNote(n);
    }

    @FXML
    private void onFileSaveAs(ActionEvent event) {

        Note n = getCurrentNote();
        if (n.getPath() == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save note");

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                n.setPath(file.getAbsolutePath());
                app.saveNote(n);
            }
        }

    }

    private void onExitApplication(ActionEvent event) {
        app.terminate();

        stage.close();
    }

//    private void onNotepadMenuClose(ActionEvent event) {
//        Tab t = (Tab) event.getTarget();
//        Notepad n = (Notepad) t.getUserData();
//        app.closeNotepad(n);
//    }
    @FXML
    private void onFileNotepadClose(ActionEvent event) {
        Tab t = notepadContainer.getSelectionModel().getSelectedItem();
        Notepad n = (Notepad) t.getUserData();
        
        app.closeNotepad(n);
    }

}
