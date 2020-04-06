/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import notepack.app.domain.Note;
import notepack.app.domain.NoteTreeItem;
import notepack.app.domain.Notepad;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class NotebookTabController implements Initializable {

    @FXML
    private TreeView<Note> notepadStructure;

    private Notepad notepad;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public TreeView<Note> getTreeView() {
        return notepadStructure;
    }

    public Note getSelectedNote() {
        TreeItem<Note> it = notepadStructure.getSelectionModel().getSelectedItem();
        
        if (it == null) {
            return null;
        }

        if (it.isLeaf()) {
            return it.getValue();
        }

        return null;
    }

    public void setNotepad(Notepad notepad) {
        this.notepad = notepad;

//                    n.setOnMouseClicked((t) -> {
//                        if (t.getClickCount() == 2) {
//                            app.openNote(n.getSelectionModel().getSelectedItem());
//                        }
//                    });        
//        notepadStructure.setOnMouseClicked((t) -> {
//            if (t.getClickCount() == 2) {
//
//            }
//        });

        NoteTreeItem items = notepad.getStorage().list();
        TreeItem root = new TreeItem(notepad.getName());

        root = addChildren(root, items);
        root.setExpanded(true);

        notepadStructure.setRoot(root);

    }

    private TreeItem addChildren(TreeItem parent, NoteTreeItem items) {

        for (NoteTreeItem it : items.get()) {

            if (it.isLeaf()) {

                Note note = new Note(it.getPath(), notepad);
                TreeItem<Note> n = new TreeItem<>(note);
                
                parent.getChildren().add(n);

            } else {

                TreeItem n = new TreeItem(it.getName());
                parent.getChildren().add(n);
                
                n = addChildren(n, it);
            }
        }

        return parent;
    }

}
