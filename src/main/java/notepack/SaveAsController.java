package notepack;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;

import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;

/**
 * FXML Controller class
 *
 */
public class SaveAsController implements Initializable {

    @FXML
    private AnchorPane tabBackground;
    @FXML
    private TreeView<NoteTreeViewItem> notepadStructure;
    @FXML
    private TextField noteName;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private SaveAsCallback clbk;

    private Note note;
    @FXML
    private Label parentDirectory;

    private String directoryName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setNote(Note note) {
        this.note = note;
        
        directoryName = note.getStorage().getBasePath();
        if (!directoryName.endsWith("/")) {
            directoryName += "/";
        }
        parentDirectory.setText(directoryName);
        
        refreshTreeView();

        notepadStructure.setCellFactory((p) -> {
            return new NoteTreeCell();
        });

        notepadStructure.setOnMouseClicked((t) -> {

            TreeItem<NoteTreeViewItem> it = notepadStructure.getSelectionModel().getSelectedItem();

            if (it.getValue().getNoteStorageItem().isLeaf()) {
                noteName.setText(it.getValue().getNoteStorageItem().getName());
                directoryName = it.getValue().getNoteStorageItem().getDirectory();
            } else {
                directoryName = it.getValue().getNoteStorageItem().getPath() + File.separator;
            }

            parentDirectory.setText(directoryName);
        });

    }

    public void setSaveAsCallback(SaveAsCallback clbk) {
        this.clbk = clbk;
    }

    @FXML
    private void onSaveBtn(ActionEvent event) {

        String name = noteName.getText();
        if (name.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Name could not be empty");
            alert.showAndWait();
            return;
        }

        clbk.save(directoryName + name);

        closeWindow();
    }

    @FXML
    private void onCancelBtn(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void refreshTreeView() {
        NoteStorageItem items = note.getNotepad().getStorage().getItemsInStorage();

//        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
//            if (o1.isLeaf()) {
//                return 1;
//            }
//            if (o1.getModified() > o2.getModified()) {
//                return -1;
//            } else if (o1.getModified() == o2.getModified()) {
//                return 0;
//            } else {
//                return 1;
//            }
//        });
        NoteTreeViewItem rootItem = new NoteTreeViewItem(note.getNotepad().getName());
        TreeItem root = new TreeItem(rootItem);

        root = addChildren(root, items);
        root.setExpanded(true);

        notepadStructure.setRoot(root);
        notepadStructure.setShowRoot(false);
    }

    private TreeItem addChildren(TreeItem parent, NoteStorageItem items) {

//        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
//            if (o1.isLeaf() && !o2.isLeaf()) {
//                return 1;
//            }
//            if (o1.getModified() > o2.getModified()) {
//                return -1;
//            } else if (o1.getModified() == o2.getModified()) {
//                return 0;
//            } else {
//                return 1;
//            }
//        });
        for (NoteStorageItem it : items.get()) {

            if (it.isLeaf()) {

                Note cnote = new Note(it.getPath(), note.getNotepad(), it.getName());
                NoteTreeViewItem noteTreeViewItem = new NoteTreeViewItem(cnote, it);
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

}
