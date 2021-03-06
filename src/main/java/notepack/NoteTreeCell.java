package notepack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import notepack.gui.Icon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class NoteTreeCell extends TreeCell<NoteTreeViewItem> {

    private FXMLLoader loader;

    @FXML
    private AnchorPane nodeRow;
    @FXML
    private Label nodeIcon;
    @FXML
    private Label nodeName;
    @FXML
    private Label lastModified;
    @FXML
    private Label noteSize;

    private ResourceBundle bundle;
    @FXML
    private VBox centerPart;
    @FXML
    private AnchorPane directoryRow;
    @FXML
    private Label directoryIcon;
    @FXML
    private VBox centerPart1;
    @FXML
    private Label directoryName;
    @FXML
    private AnchorPane parentRow;
    @FXML
    private Label notepadIcon;
    @FXML
    private VBox centerPart11;
    @FXML
    private Label workspaceName;

    public NoteTreeCell() {

        if (loader == null) {
            try {
                loader = new FXMLLoader(getClass().getResource("WorkspaceTreeViewCell.fxml"));
                loader.setController(this);
                loader.load();

            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    protected void updateItem(NoteTreeViewItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
        } else {
            
            if (item.isRoot()) {
                workspaceName.setText(item.getLabel());
                setGraphic(parentRow);
                return;
            }
            
            if (!item.getNoteStorageItem().isLeaf()) {

                directoryName.setText(item.getLabel());
                directoryIcon = Icon.get("mi-folder-outline");

                setGraphic(directoryRow);

            } else {

                nodeName.setText(item.getLabel());

                Date date = new Date(item.getNoteStorageItem().getModified());
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                lastModified.setText(formatter.format(date));
                noteSize.setText(formatFileSize(item.getNoteStorageItem().getSize()));

                setGraphic(nodeRow);
            }

        }
    }

    private String formatFileSize(long bytes) {
        int u = 0;
        for (; bytes > 1024 * 1024; bytes >>= 10) {
            u++;
        }
        if (bytes > 1024) {
            u++;
        }
        return String.format("%.1f %cB", bytes / 1024f, " kMGTPE".charAt(u));
    }

}
