package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.domain.PopupController;

public class TagPopupController extends PopupController {

    @FXML
    private TextField tagInput;

    @FXML
    private HBox tagContainer;

    @FXML
    private AnchorPane reminderPaneBackground;

    @FXML
    private Label currentTagsLabel;

    @FXML
    private Label headerLabel;

    private App app;
    private Note note;

    public void setAppNote(App app, Note note) {
        this.app = app;
        this.note = note;

        refreshTagList();

        tagInput.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER) {
                String tagToAdd = tagInput.getText().trim();
                if (tagToAdd.length() > 0) {
                    note.getMeta().addTag(tagToAdd);

                    refreshTagList();

                    tagInput.setText("");
                    tagInput.requestFocus();
                }
            }
        });
    }

    @FXML
    void onCloseBtn(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    private void refreshTagList() {
        tagContainer.getChildren().clear();

        for (String tag : note.getMeta().getTags()) {

            Button tagBtn = new Button();
            tagBtn.setText(tag);
            tagBtn.getStyleClass().add("tag-btn");
            tagBtn.setStyle("-fx-background-color: green");
            tagBtn.setOnAction(event -> {
                note.getMeta().removeTag(tag);
                refreshTagList();
            });

            tagContainer.getChildren().add(tagBtn);

        }
    }

}
