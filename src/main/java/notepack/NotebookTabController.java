/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
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

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;

        tabBackground.setStyle("-fx-background-color: " + notepad.getBackgroundColor());

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

        TreeItem root = new TreeItem(null);

        root = addChildren(root, items);
        root.setExpanded(true);

        notepadStructure.setRoot(root);
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

                Note note = new Note(it.getPath(), notepad);
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
//        notepadStructure.getSelectionModel().getSelectedItem().getValue().getNote();
    }

    @FXML
    private void treeViewOnClose(ActionEvent event) {
    }

    @FXML
    private void treeViewOnRefresh(ActionEvent event) {
    }

    @FXML
    private void treeViewOnDelete(ActionEvent event) {
    }

}
