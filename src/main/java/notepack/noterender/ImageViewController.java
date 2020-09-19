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
import notepack.app.domain.App;
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
    private App app;

    @FXML
    private ScrollPane imageBackground;

    private boolean isResized = false;
    private Image currentImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void onCloseNote(ActionEvent event) {
        app.closeNote(note);
    }

    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setState(App app, Note note) {
        this.note = note;
        this.app = app;

        currentImage = new Image(new ByteArrayInputStream(note.getContent()));

        imageRender.setImage(currentImage);
        
        tabBackground.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());
        
        refreshImageScale();
    }

    @FXML
    private void onImageResize(ActionEvent event) {
        app.getSettings().set("image-view-scale", "full");
        refreshImageScale();
    }
    
    @FXML
    private void onImageFitVertical(ActionEvent event) {
        app.getSettings().set("image-view-scale", "vertical");
        refreshImageScale();
    }

    @FXML
    private void onImageFitHorizontal(ActionEvent event) {
        app.getSettings().set("image-view-scale", "horizontal");
        refreshImageScale();
    }

    private void refreshImageScale() {
        String val = app.getSettings().get("image-view-scale", "vertical");
        
        if (val.equals("full")) {
            imageRender.setFitHeight(currentImage.getHeight());
            imageRender.setFitWidth(currentImage.getWidth());
            return;
        }
        
        if (val.equals("vertical")) {
            imageRender.setFitHeight(imageBackground.heightProperty().doubleValue() * 0.99);
//            imageRender.setFitWidth(currentImage.getWidth());
            imageRender.setFitWidth(5000);
            return;
        }
        
        if (val.equals("horizontal")) {
            imageRender.setFitWidth(imageBackground.widthProperty().doubleValue() * 0.99);
//            imageRender.setFitHeight(currentImage.getHeight());
            imageRender.setFitHeight(5000);
            return;
        }
    }

    @Override
    public void noteActivated() {
    }

    @Override
    public void noteDeactivated() {
    }



}
