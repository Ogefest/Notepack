/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Note;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class SearchForNoteController implements Initializable {

    @FXML
    private TextField queryInput;
    @FXML
    private ListView<Note> noteList;

    private SearchForNoteCallback clbk;

    private App app;
    
    private ObservableList<Note> programList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setApp(App app) {
        this.app = app;
    }

    public void setCallback(SearchForNoteCallback clbk) {
        this.clbk = clbk;
        
        noteList.setItems(programList);
    }

    public void setQuery(String q) {
        queryInput.setText(q);
        queryInput.requestFocus();
    }

    @FXML
    private void updateSearchResult(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ESCAPE)) {
            Stage stage = (Stage) queryInput.getScene().getWindow();
            stage.close();
            return;
        }
        
        if (event.getCode().equals(KeyCode.ENTER)) {
            clbk.openNote(noteList.getSelectionModel().getSelectedItem());
            
            Stage stage = (Stage) queryInput.getScene().getWindow();
            stage.close();
            return;            
        }

        if (event.getCode().isLetterKey()) {
            
            String q = queryInput.getText();
            
            ArrayList<Note> tmp = app.searchForNote(q);
            
            programList.clear();
            programList.addAll(tmp);
            
            if (tmp.size() > 0) {
                noteList.getSelectionModel().select(0);
            }
        }
        
        if (event.getCode().isArrowKey()) {
            int selectedIndex = noteList.getSelectionModel().getSelectedIndex();
            
            if (event.getCode().equals(KeyCode.UP) && selectedIndex > 0) {
                noteList.getSelectionModel().select(selectedIndex - 1);
            }
            if (event.getCode().equals(KeyCode.DOWN) && selectedIndex < noteList.getItems().size()) {
                noteList.getSelectionModel().select(selectedIndex + 1);
            }

        }
    }

}
