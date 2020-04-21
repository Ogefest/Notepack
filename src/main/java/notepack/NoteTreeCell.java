/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author lg
 */
public class NoteTreeCell extends TreeCell<NoteTreeViewItem> {

//    private HBox hbox;
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
    private Label notepadName;

    public NoteTreeCell() {

        if (loader == null) {
            try {
                bundle = ResourceBundle.getBundle("notepack.fonts.FontAwesome");
                loader = new FXMLLoader(getClass().getResource("NotepadTreeViewCell.fxml"));
                loader.setResources(bundle);
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
                notepadName.setText(item.getLabel());
                setGraphic(parentRow);
                return;
            }
            
            if (!item.getNoteStorageItem().isLeaf()) {

                directoryName.setText(item.getLabel());
                directoryIcon.setText(bundle.getString("fa.folder_o"));

                setGraphic(directoryRow);

            } else {

                nodeName.setText(item.getLabel());

                Date date = new Date(item.getNoteStorageItem().getModified());
                DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
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
