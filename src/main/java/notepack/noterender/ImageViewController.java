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
        
        Image img = new Image(new ByteArrayInputStream(note.getContent()));
        
        imageRender.setFitHeight(img.getHeight());
        imageRender.setFitWidth(img.getWidth());
        imageRender.setImage(img);
        
    }

    @Override
    public void setNoteTabContentCallback(NoteTabContentCallback clbk) {
        
    }

}
