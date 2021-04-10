package notepack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.Note;

import java.io.IOException;

public class SearchNoteViewCell extends ListCell<Note> {

    @FXML
    private Label noteNameLabel;

    @FXML
    private Label workspaceLabel;

    @FXML
    private AnchorPane searchNoteCell;

    private FXMLLoader mLLoader;

    public SearchNoteViewCell() {

        mLLoader = new FXMLLoader(getClass().getResource("/notepack/SearchNoteCellView.fxml"));
        mLLoader.setController(this);
        try {
            mLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void updateItem(Note note, boolean empty) {
        super.updateItem(note, empty);

        if (empty || note == null) {
            return;
        }

        noteNameLabel.setText(note.getName());
        workspaceLabel.setText(note.getWorkspace().getName());
//        String workspaceColor = note.getWorkspace().getBackgroundColor();
//        String fontColor = note.getWorkspace().getFontColor();
//        workspaceLabel.setStyle("-fx-border-color:"+workspaceColor+"; -fx-background-color: "+workspaceColor+";");
        workspaceLabel.setStyle("-fx-background-color: " + note.getWorkspace().getBackgroundColor());

        setGraphic(searchNoteCell);
    }

}
