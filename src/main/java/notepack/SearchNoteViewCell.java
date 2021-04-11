package notepack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import notepack.app.domain.Note;
import notepack.gui.TagColor;

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

    @FXML
    private HBox tagContainer;

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

        noteSizeLabel.setText(formatFileSize(note.getSize()));
        pathLabel.setText(note.getPath());
        noteNameLabel.setText(note.getName());
        workspaceLabel.setText(note.getWorkspace().getName());

        workspaceLabel.setStyle("-fx-background-color: " + note.getWorkspace().getBackgroundColor());


        tagContainer.getChildren().clear();
        for (String tag : note.getMeta().getTags()) {

            Label tagLabel = new Label();
            tagLabel.setText(tag);
            tagLabel.getStyleClass().add("tag-label");

            String color = TagColor.get(tag);
            tagLabel.setStyle("-fx-background-color: " + color);

            tagContainer.getChildren().add(tagLabel);
        }

        setGraphic(searchNoteCell);
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
