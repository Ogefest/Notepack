/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.HBox;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorageItem;

/**
 *
 * @author lg
 */
public class NoteTreeCell extends TreeCell<NoteTreeViewItem> {

    private HBox hbox;

    public NoteTreeCell() {
        
        try {
            hbox = (HBox) FXMLLoader.load(getClass().getResource("NotepadTreeViewCell.fxml"));
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    protected void updateItem(NoteTreeViewItem item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setGraphic(null);
        } else {
            Label l = (Label) hbox.getChildren().get(0);
            l.setText(item.getLabel());
            // configure graphic with cell data etc...
            setGraphic(hbox);
        }
    }
    
}
