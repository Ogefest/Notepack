/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.Notepad;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class NotebookTabController implements Initializable {

    @FXML
    private TreeView<NoteTreeViewItem> notepadStructure;

    private Notepad notepad;
    @FXML
    private AnchorPane tabBackground;

    private App app;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public TreeView<NoteTreeViewItem> getTreeView() {
        return notepadStructure;
    }

    public Note getSelectedNote() {
        TreeItem<NoteTreeViewItem> it = notepadStructure.getSelectionModel().getSelectedItem();

        if (it == null) {
            return null;
        }

        if (it.isLeaf()) {

            NoteTreeViewItem item = it.getValue();

            return it.getValue().getNote();
        }

        return null;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;

        tabBackground.setStyle("-fx-background-color: " + notepad.getBackgroundColor());
        notepadStructure.setStyle("cell-selection-color: " + notepad.getBackgroundColor());

        notepadStructure.setCellFactory((p) -> {
            return new NoteTreeCell();
        });
    }

    public Notepad getNotepad() {
        return notepad;
    }

    public void refreshTreeView() {
        NoteStorageItem items = notepad.getStorage().getItemsInStorage();

        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
            if (o1.isLeaf()) {
                return 1;
            }
            if (o1.getModified() > o2.getModified()) {
                return -1;
            } else if (o1.getModified() == o2.getModified()) {
                return 0;
            } else {
                return 1;
            }
        });

        NoteTreeViewItem rootItem = new NoteTreeViewItem(notepad.getName());
        TreeItem root = new TreeItem(rootItem);

        root = addChildren(root, items);
        root.setExpanded(true);

        notepadStructure.setRoot(root);
        notepadStructure.setShowRoot(false);
    }

    private TreeItem addChildren(TreeItem parent, NoteStorageItem items) {

        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
            if (o1.isLeaf() && !o2.isLeaf()) {
                return 1;
            }
            if (o1.getModified() > o2.getModified()) {
                return -1;
            } else if (o1.getModified() == o2.getModified()) {
                return 0;
            } else {
                return 1;
            }
        });

        for (NoteStorageItem it : items.get()) {

            if (it.isLeaf()) {

                Note note = new Note(it.getPath(), notepad, it.getName());
                NoteTreeViewItem noteTreeViewItem = new NoteTreeViewItem(note, it);
                TreeItem<NoteTreeViewItem> n = new TreeItem<>(noteTreeViewItem);

                parent.getChildren().add(n);

            } else {

                NoteTreeViewItem noteTreeViewItem = new NoteTreeViewItem(it);
                TreeItem<NoteTreeViewItem> n = new TreeItem<>(noteTreeViewItem);
                parent.getChildren().add(n);

                n = addChildren(n, it);
            }
        }

        return parent;
    }

    @FXML
    private void treeViewOnOpen(ActionEvent event) {
        Note n = notepadStructure.getSelectionModel().getSelectedItem().getValue().getNote();
        app.openNote(n);
    }

    @FXML
    private void treeViewOnClose(ActionEvent event) {
        Note n = notepadStructure.getSelectionModel().getSelectedItem().getValue().getNote();
        app.closeNote(n);

    }

    @FXML
    private void treeViewOnRefresh(ActionEvent event) {
        app.refreshNotepad(notepad);
    }

    @FXML
    private void treeViewOnDelete(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Do you really want to delete selected note?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Note n = notepadStructure.getSelectionModel().getSelectedItem().getValue().getNote();
            app.deleteNote(n);
        }

    }

    @FXML
    private void treeViewOnRename(ActionEvent event) {

        Note n = notepadStructure.getSelectionModel().getSelectedItem().getValue().getNote();

        TextInputDialog dialog = new TextInputDialog(n.getPath());
        dialog.setTitle("Rename");
        dialog.setHeaderText(null);
        dialog.setContentText("Please enter note name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            app.renameNote(n, result.get());
        }

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
                public void ready(Notepad notepad) {
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

    public void openNotepadEdit(Notepad notepad) {

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

    }

    @FXML
    private void onFileNew(ActionEvent event) {

        app.newNote(notepad);

    }

    @FXML
    private void onNotepadEdit(ActionEvent event) {
        openNotepadEdit(notepad);
    }

    @FXML
    private void onNotepadClose(ActionEvent event) {
        app.closeNotepad(notepad);
    }

}
