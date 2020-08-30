/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.noterender;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import notepack.NoteTabContentCallback;
import notepack.app.domain.Note;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class ImageViewController implements Initializable, NoteRenderController {

    @FXML
    private AnchorPane tabBackground;
    @FXML
    private ImageView imageRender;

    private Note note;
    @FXML
    private ScrollPane imageBackground;

    private boolean isResized = false;
    private Image currentImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onCloseNote(ActionEvent event) {
    }

    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setNote(Note note) {
        this.note = note;

        currentImage = new Image(new ByteArrayInputStream(note.getContent()));

        imageRender.setFitHeight(imageBackground.heightProperty().doubleValue());
        imageRender.setFitWidth(imageBackground.widthProperty().doubleValue());

//        imageRender.fitWidthProperty().bind(imageBackground.widthProperty());
//        imageRender.fitHeightProperty().bind(imageBackground.heightProperty());
//        imageRender.setFitHeight(currentImage.getHeight());
//        imageRender.setFitHeight(800);
//        imageRender.setFitWidth(currentImage.getWidth());
//        imageRender.setFitWidth(tabBackground.widthProperty().get());
        imageRender.setImage(currentImage);

    }

    @Override
    public void setNoteTabContentCallback(NoteTabContentCallback clbk) {

    }

    @FXML
    private void onImageResize(ActionEvent event) {
        if (!isResized) {

            imageRender.setFitHeight(currentImage.getHeight());
            imageRender.setFitWidth(currentImage.getWidth());

            isResized = true;
        } else {
            imageRender.setFitHeight(imageBackground.heightProperty().doubleValue());
            imageRender.setFitWidth(imageBackground.widthProperty().doubleValue());

            isResized = false;
        }
    }

}
