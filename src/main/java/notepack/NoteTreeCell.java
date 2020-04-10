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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;

/**
 *
 * @author lg
 */
public class NoteTreeCell extends TreeCell<NoteTreeViewItem> {

//    private HBox hbox;
    private FXMLLoader loader;

    @FXML
    private HBox nodeRow;
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
            nodeName.setText(item.getLabel());

            Date date = new Date(item.getNoteStorageItem().getModified());
            DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            lastModified.setText(formatter.format(date));
            
            noteSize.setText(formatFileSize(item.getNoteStorageItem().getSize()));
            
            if (!item.getNoteStorageItem().isLeaf()) {
                noteSize.setVisible(false);
                
                centerPart.getChildren().remove(noteSize);
                
                lastModified.setMaxHeight(0);
                lastModified.setVisible(false);
                nodeIcon.setText(bundle.getString("fa.folder_o"));
                
            }

            setGraphic(nodeRow);
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
