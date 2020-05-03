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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.Settings;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.storage.Filesystem;
import notepack.app.storage.PreferencesSettings;

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

    private MainViewGuiAction guiAction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void appStart() {
        app = new App();
        appSettings = new PreferencesSettings();

        app.getMessageBus().registerNoteListener(new NoteListener() {
            @Override
            public void onOpen(Note note) {

                for (Tab t : tabContainer.getTabs()) {
                    NoteTabContentController ctrl = (NoteTabContentController) t.getUserData();
                    if (ctrl.getNote().getIdent().equals(note.getIdent())) {
                        Platform.runLater(() -> {
                            tabContainer.getSelectionModel().select(t);
                        });

                        return;
                    }
                }

                final Tab newTab = new Tab();
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("NoteTabContent.fxml"));
                    loader.setResources(ResourceBundle.getBundle("notepack.fonts.FontAwesome"));
                    Node tabContent = loader.load();
                    NoteTabContentController ctrl = loader.getController();
                    ctrl.setNote(note);
                    ctrl.getTextArea().textProperty().addListener((ov, oldValue, newValue) -> {
                        app.changeNote(note, newValue);
                    });
                    ctrl.setNoteTabContentCallback(new NoteTabContentCallback() {
                        @Override
                        public void onSaveNote(Note n) {
                            saveNote(n);
                        }

                        @Override
                        public void onOpenNote() {
                        }

                        @Override
                        public void onCloseNote(Note n) {
                            app.closeNote(n);
                        }
                    });

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem closeNoteMenu = new MenuItem("Close");
                    closeNoteMenu.setOnAction((t) -> {
                        app.closeNote(note);
                    });
                    MenuItem saveNoteMenu = new MenuItem("Save");
                    saveNoteMenu.setOnAction((t) -> {
                        app.saveNote(note);
                    });
                    contextMenu.getItems().addAll(saveNoteMenu, closeNoteMenu);
                    newTab.setContextMenu(contextMenu);

                    newTab.setContent(tabContent);
                    newTab.setUserData(ctrl);
                    newTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event t) {

                            String taText = ctrl.getTextArea().getText();
                            String noteText = note.getContent();

                            if (!note.isSaved()) {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation");
                                alert.setHeaderText(null);
                                alert.setContentText("Changes not saved, do you want to save document?");

                                ButtonType buttonSave = new ButtonType("Save");
                                ButtonType buttonClose = new ButtonType("Close without saving");

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == buttonSave) {
                                    app.saveNote(note);
                                    app.closeNote(note);
                                } else if (result.get() == buttonClose) {
                                    app.closeNote(note);
                                }
                            } else {
                                app.closeNote(note);
                            }
                        }
                    });

                    String notepadColor = note.getNotepad().getBackgroundColor();
                    newTab.setStyle("-fx-background-color: " + notepadColor + ";-fx-border-color:" + notepadColor);
                    if (note.getName().length() > 0) {
                        newTab.setText(note.getName());
                    }
                    newTab.setGraphic(new Label(""));

                    Platform.runLater(() -> {
                        tabContainer.getTabs().add(newTab);
                        tabContainer.getSelectionModel().select(newTab);

                        ctrl.getTextArea().requestFocus();
                    });

                } catch (IOException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onClose(Note n) {

                Platform.runLater(() -> {
                    for (Tab t : tabContainer.getTabs()) {
                        NoteTabContentController ctrl = (NoteTabContentController) t.getUserData();
                        if (ctrl.getNote().getIdent().equals(n.getIdent())) {
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

                        NoteTabContentController ctrl = (NoteTabContentController) t.getUserData();

                        if (ctrl.getNote().getIdent().equals(n.getIdent())) {
                            t.setText(n.getName());

                            Label l = (Label) t.getGraphic();

                            if (ctrl.getTextArea().getText().equals(n.getContent())) {

                                ResourceBundle bundle = ResourceBundle.getBundle("notepack.fonts.FontAwesome");
                                l.setText(bundle.getString("fa.pencil_square_o"));
                                l.setStyle("-fx-font-family: FontAwesome; -fx-font-size: 16;");

                            } else {
                                l.setText("");
                            }
                        }
                    }
                });
            }

            @Override
            public void onSave(Note n) {
                Platform.runLater(() -> {
                    for (Tab t : tabContainer.getTabs()) {
                        NoteTabContentController ctrl = (NoteTabContentController) t.getUserData();
                        if (ctrl.getNote().getIdent().equals(n.getIdent())) {
                            t.setText(n.getName());

                            Label l = (Label) t.getGraphic();
                            l.setText("");
                        }
                    }
                });
            }
        });

        app.getMessageBus().registerNotepadListener(new NotepadListener() {
            @Override
            public void onOpen(Notepad notepad) {
                Tab tab = new Tab();
                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("NotepadTabListView.fxml"));
                    loader.setResources(ResourceBundle.getBundle("notepack.fonts.FontAwesome"));
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
                    closeNotepadMenu.setOnAction((t) -> {
                        app.closeNotepad(notepad);
                    });
                    MenuItem refreshNotepadMenu = new MenuItem("Refresh");
                    refreshNotepadMenu.setOnAction((t) -> {
                        app.refreshNotepad(notepad);
                    });
                    MenuItem configureNotepadMenu = new MenuItem("Settings");
                    configureNotepadMenu.setOnAction((t) -> {

                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(getClass().getResource("NotepadCreate.fxml"));

                        Scene scene;
                        try {
                            Parent root = fxmlLoader.load();

                            NotepadCreateController nctrl = (NotepadCreateController) fxmlLoader.getController();
                            nctrl.setNotepadToEdit(notepad);
                            nctrl.setNotepadCreateCallback(new NotepadCreateCallback() {
                                @Override
                                public void ready(Notepad notepad) {
                                    app.closeNotepad(notepad);
                                    app.openNotepad(notepad);
                                }
                            });

                            scene = new Scene(root);
                            Stage stage = new Stage();
                            stage.setTitle("Edit notepad");
                            stage.setScene(scene);
                            stage.show();

                        } catch (IOException ex) {
                            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });

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

                    });

                } catch (IOException ex) {
                    Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onClose(Notepad notepad) {

                Platform.runLater(() -> {
                    for (Tab t : notepadContainer.getTabs()) {

                        NotebookTabController ctrl = (NotebookTabController) t.getUserData();
                        if (ctrl.getNotepad().getIdent().equals(notepad.getIdent())) {
                            notepadContainer.getTabs().remove(t);
                            break;
                        }
                    }
                });
            }

            @Override
            public void onNotesListUpdated(Notepad notepad) {

                Platform.runLater(() -> {

                    for (Tab t : notepadContainer.getTabs()) {

                        NotebookTabController ctrl = (NotebookTabController) t.getUserData();
                        if (ctrl.getNotepad().equals(notepad)) {
                            ctrl.refreshTreeView();
                        }
                    }

                });

            }
        });

        guiAction = new MainViewGuiAction(stage, app);

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

        if (app.getLastNotes().size() == 0) {
//            app.newNote(new Filesystem());
        } else {
            for (Note note : app.getLastNotes()) {
                app.openNote(note);
            }
        }

        
        KeyCombination kcCloseNote = new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(kcCloseNote, () -> {
            app.closeNote(getCurrentNote());
        });
        
        KeyCombination kcSave = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(kcSave, () -> {
            saveNote(getCurrentNote());
        });

        KeyCombination kcNewNote = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(kcNewNote, () -> {
            app.newNote(getCurrentNotepad());
        });

        KeyCombination kcSearchNote = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        stage.getScene().getAccelerators().put(kcSearchNote, () -> {
            guiAction.showSearchForNoteDialog();
        });
        
        
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
        return ((NoteTabContentController) t.getUserData()).getTextArea();
    }

    private Note getCurrentNote() {
        Tab t = tabContainer.getSelectionModel().getSelectedItem();
        return ((NoteTabContentController) t.getUserData()).getNote();
    }

    private Notepad getCurrentNotepad() {
        Tab t = notepadContainer.getSelectionModel().getSelectedItem();
        return ((NotebookTabController) t.getUserData()).getNotepad();
    }

    private void onFileOpen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File f = chooser.showOpenDialog(stage);
        if (f != null) {
//            Note note = new Note(f.getAbsolutePath(), new Filesystem());
//            app.getMessageBus().addTask(new OpenNote(note));
        }
    }

    private void onFileSave(ActionEvent event) {
        Note n = getCurrentNote();
        saveNote(n);
    }

    private void saveNote(Note n) {

        if (n.getPath() == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save note");

            String dir = getCurrentNotepad().getStorage().getConfiguration().get("directory");
            fileChooser.setInitialDirectory(new File(dir));

            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                n.setPath(file.getAbsolutePath());
                app.saveNote(n);
            }
        } else {
            app.saveNote(n);
        }
        app.refreshNotepad(n.getNotepad());
    }

    private void onFileClose(ActionEvent event) {
        app.closeNote(getCurrentNote());
    }

    private void onFileNew(ActionEvent event) {
        app.newNote(getCurrentNotepad());
    }


//    private void onFileNotepadRemove(ActionEvent event) {
//        Note n = (Note) tabContainer.getSelectionModel().getSelectedItem().getUserData();
//        app.closeNote(n);
//    }
//    private void onFileSaveAs(ActionEvent event) {
//
//        Note n = getCurrentNote();
//        if (n.getPath() == null) {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save note");
//
//            File file = fileChooser.showSaveDialog(stage);
//            if (file != null) {
//                n.setPath(file.getAbsolutePath());
//                app.saveNote(n);
//            }
//        }
//
//    }
//    private void onExitApplication(ActionEvent event) {
//        app.terminate();
//        stage.close();
//    }
//    private void onNotepadMenuClose(ActionEvent event) {
//        Tab t = (Tab) event.getTarget();
//        Notepad n = (Notepad) t.getUserData();
//        app.closeNotepad(n);
//    }
//    private void onFileNotepadClose(ActionEvent event) {
//        NotebookTabController ctrl = (NotebookTabController) notepadContainer.getSelectionModel().getSelectedItem().getUserData();
//        Notepad n = ctrl.getNotepad();
//
//        app.closeNotepad(n);
//    }
    @FXML
    private void onNoteSearch(ActionEvent event) {
        guiAction.showSearchForNoteDialog();
    }

    @FXML
    private void onApplicationInfo(ActionEvent event) {
    }

}
