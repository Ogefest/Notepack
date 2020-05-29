package notepack;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.Notepad;
import notepack.app.domain.SessionStorage;
import notepack.app.domain.Settings;
import notepack.app.listener.NoteListener;
import notepack.app.listener.NotepadListener;
import notepack.app.storage.Filesystem;
import notepack.app.storage.JsonNotepadRepository;
import notepack.app.storage.PreferencesSettings;
import notepack.encrypt.Fake;
import notepack.encrypt.SimpleAES;

/**
 * FXML Controller class
 *
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

    @FXML
    private TabPane notepadContainer;

    private MainViewGuiAction guiAction;

    public HostServices hostServices;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void appStart() {
        appSettings = new PreferencesSettings();
        SessionStorage sessionStorage = new JsonNotepadRepository(new SimpleAES(), appSettings);

        app = new App(sessionStorage);

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
                        Note note = ctrl.getNote();
                        if (note.getIdent().equals(n.getIdent())) {

                            if (!note.isSaved()) {
                                Alert alert = new Alert(AlertType.CONFIRMATION);
                                alert.setTitle("Confirmation");
                                alert.setHeaderText(null);
                                alert.setContentText("Changes not saved, do you want to save document?");

                                ButtonType buttonSave = new ButtonType("Save");
                                ButtonType buttonClose = new ButtonType("Close without saving");
                                ButtonType buttonDontClose = new ButtonType("Don't close");

                                alert.getButtonTypes().setAll(buttonDontClose, buttonClose, buttonSave);

                                Optional<ButtonType> result = alert.showAndWait();
                                if (result.get() == buttonSave) {
                                    app.saveNote(note);

                                    tabContainer.getTabs().remove(t);
                                } else if (result.get() == buttonClose) {
                                    tabContainer.getTabs().remove(t);
                                }
                            } else {
                                tabContainer.getTabs().remove(t);
                            }

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
                        ctrl.openNotepadEdit(notepad);
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

        KeyCombination kcSearchReplaceString = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(kcSearchReplaceString, () -> {
            Tab t = tabContainer.getSelectionModel().getSelectedItem();
            Parent p = (Parent) t.getContent();
            ((NoteTabContentController) t.getUserData()).showSearchReplaceForm();
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

    private void onFileSave(ActionEvent event) {
        Note n = getCurrentNote();
        saveNote(n);
    }

    private void saveNote(Note n) {

        if (n.getPath() == null) {

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("SaveAs.fxml"));

            Scene scene;
            try {
                Parent root = fxmlLoader.load();

                SaveAsController ctrl = (SaveAsController) fxmlLoader.getController();

                ctrl.setSaveAsCallback((name) -> {
                    n.setPath(name);
                    app.saveNote(n);
                    app.refreshNotepad(n.getNotepad());
                });

                ctrl.setNote(n);

                scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Set name");
                stage.setScene(scene);
                stage.show();

            } catch (IOException ex) {
                Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
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

    @FXML
    private void onNoteSearch(ActionEvent event) {
        guiAction.showSearchForNoteDialog();
    }

    @FXML
    private void onApplicationInfo(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AboutDialog.fxml"));

        Scene scene;
        try {
            Parent root = fxmlLoader.load();

            AboutDialogController ctrl = (AboutDialogController) fxmlLoader.getController();
            ctrl.hostServices = hostServices;

            scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("About");
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(MainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
