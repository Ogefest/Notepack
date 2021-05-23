package notepack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import notepack.gui.MaterialIcon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class NoteTreeCell extends TreeCell<NoteTreeViewItem> {

    private FXMLLoader loader;

    @FXML
    private AnchorPane nodeRow;
    @FXML
    private MaterialIcon nodeIcon;
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
    private MaterialIcon directoryIcon;
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
                setGraphic(directoryRow);
            } else {
                Optional<String> tmpExt = getExtensionByStringHandling(item.getLabel());
                if (tmpExt.isPresent()) {
                    if (tmpExt.get().equals("pdf")) {
                        nodeIcon.setName("file-pdf-outline");
                    } else if (tmpExt.get().equals("jpg")) {
                        nodeIcon.setName("file-image-outline");
                    } else if (tmpExt.get().equals("csv")) {
                        nodeIcon.setName("file-table-outline");
                    } else if (tmpExt.get().equals("png")) {
                        nodeIcon.setName("file-image-outline");
                    } else if (tmpExt.get().equals("jpeg")) {
                        nodeIcon.setName("file-image-outline");
                    } else if (tmpExt.get().equals("ics")) {
                        nodeIcon.setName("file-check-outline");
                    } else {
                        nodeIcon.setName("file-outline");
                    }
                }

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

    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
