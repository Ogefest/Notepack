package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.Workspace;
import notepack.app.task.NoteNew;
import notepack.app.task.NoteSetNamePopup;
import notepack.app.task.TodoNew;
import notepack.app.task.WorkspacePopup;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 */
public class WorkspaceTabController implements Initializable {

    @FXML
    private TreeView<NoteTreeViewItem> workspaceStructure;

    /*
    Structure used for cache items to select note in fast way without
    searching whole tree structure in workspace
     */
    private HashMap<String, TreeItem<NoteTreeViewItem>> treeViewItemsCache = new HashMap<>();

    private Workspace workspace;
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
        return workspaceStructure;
    }

    public Note getSelectedNote() {
        TreeItem<NoteTreeViewItem> it = workspaceStructure.getSelectionModel().getSelectedItem();

        if (it == null) {
            return null;
        }

        if (it.isLeaf()) {

            NoteTreeViewItem item = it.getValue();

            return it.getValue().getNote();
        }

        return null;
    }

    public void selectNote(Note note) {

        TreeItem<NoteTreeViewItem> item = treeViewItemsCache.get(note.getIdent());
        if (item != null) {
            int nodeIndex = workspaceStructure.getRow(item);
            workspaceStructure.getSelectionModel().select(item);
            workspaceStructure.scrollTo(nodeIndex);
        }

    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;

        tabBackground.setStyle("-fx-background-color: " + workspace.getBackgroundColor());
        workspaceStructure.setStyle("cell-selection-color: " + workspace.getBackgroundColor());

        workspaceStructure.setCellFactory((p) -> {
            return new NoteTreeCell();
        });
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void refreshTreeView() {
        NoteStorageItem items = workspace.getStorage().getItemsInStorage();
        treeViewItemsCache = new HashMap<>();

        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
            if (o1.isLeaf() && !o2.isLeaf()) {
                return 1;
            }
            if (!o1.isLeaf() && o2.isLeaf()) {
                return -1;
            }

            if (o1.getModified() > o2.getModified()) {
                return -1;
            } else if (o1.getModified() == o2.getModified()) {
                return 0;
            } else {
                return 1;
            }
        });

        NoteTreeViewItem rootItem = new NoteTreeViewItem(workspace.getName());
        TreeItem root = new TreeItem(rootItem);

        root = addChildren(root, items);
        root.setExpanded(true);

        workspaceStructure.setRoot(root);
        workspaceStructure.setShowRoot(false);
    }

    private TreeItem addChildren(TreeItem parent, NoteStorageItem items) {

        Collections.sort(items.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
            if (o1.isLeaf() && !o2.isLeaf()) {
                return 1;
            }
            if (!o1.isLeaf() && o2.isLeaf()) {
                return -1;
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

                Note note = new Note(it.getPath(), workspace, it.getName());
                NoteTreeViewItem noteTreeViewItem = new NoteTreeViewItem(note, it);
                TreeItem<NoteTreeViewItem> n = new TreeItem<>(noteTreeViewItem);

                parent.getChildren().add(n);
                treeViewItemsCache.put(note.getIdent(), n);

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
        Note n = workspaceStructure.getSelectionModel().getSelectedItem().getValue().getNote();
        app.openNote(n);
    }

    @FXML
    private void treeViewOnClose(ActionEvent event) {
        Note n = workspaceStructure.getSelectionModel().getSelectedItem().getValue().getNote();
        app.closeNote(n);

    }

    @FXML
    private void treeViewOnRefresh(ActionEvent event) {
        app.refreshWorkspace(workspace);
    }

    @FXML
    private void treeViewOnDelete(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Do you really want to delete selected note?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Note n = workspaceStructure.getSelectionModel().getSelectedItem().getValue().getNote();
            app.deleteNote(n);
        }

    }

    @FXML
    private void treeViewOnRename(ActionEvent event) {

        Note n = workspaceStructure.getSelectionModel().getSelectedItem().getValue().getNote();

        app.addTask(new NoteSetNamePopup(n));

//        TextInputDialog dialog = new TextInputDialog(n.getPath());
//        dialog.setTitle("Rename");
//        dialog.setHeaderText(null);
//        dialog.setContentText("Please enter note name:");
//
//        Optional<String> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            app.renameNote(n, result.get());
//        }

    }

    @FXML
    private void onFileWorkspaceAdd(ActionEvent event) {

        app.addTask(new WorkspacePopup());

    }

    public void openWorkspaceEdit(Workspace workspace) {

        app.addTask(new WorkspacePopup(workspace));

    }

    @FXML
    private void onFileNew(ActionEvent event) {
        app.addTask(new NoteNew(workspace));
    }

    @FXML
    private void onWorkspaceEdit(ActionEvent event) {
        openWorkspaceEdit(workspace);
    }

    @FXML
    private void onWorkspaceClose(ActionEvent event) {
        app.closeWorkspace(workspace);
    }

    @FXML
    private void onWorkspaceArchive(ActionEvent event) {
        app.closeWorkspace(workspace);
    }

    public void onChecklistNew(ActionEvent actionEvent) {
        app.addTask(new TodoNew(workspace));
    }
}
