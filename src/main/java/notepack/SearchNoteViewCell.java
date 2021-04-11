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

    @FXML
    private Label noteSizeLabel;

    @FXML
    private Label pathLabel;

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
            setGraphic(null);
            return;
        }

//        noteSizeLabel.setText(note.);
        pathLabel.setText(note.getPath());
        noteNameLabel.setText(note.getName());
        workspaceLabel.setText(note.getWorkspace().getName());

        workspaceLabel.setStyle("-fx-background-color: " + note.getWorkspace().getBackgroundColor());

        setGraphic(searchNoteCell);
    }

}
