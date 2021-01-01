/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;

/**
 * FXML Controller class
 *
 * @author lg
 */
public class SearchNoteTabController implements Initializable {

    @FXML
    private ListView<Note> searchResult;

    @FXML
    private TextField queryInput;

    @FXML
    private AnchorPane tabBackground;

    private App app;

    private ObservableList<Note> programList = FXCollections.observableArrayList();


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        queryInput.textProperty().addListener((observable, oldValue, newValue) -> {
            filterList(newValue);
        });

        searchResult.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Note note = searchResult.getSelectionModel().getSelectedItem();
                if (note != null) {
                    app.openNote(note);
                }
            }
        });
        queryInput.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    Note note = searchResult.getSelectionModel().getSelectedItem();
                    if (note != null) {
                        app.openNote(note);
                    }
                }

                if (event.getCode().isArrowKey()) {
                    int selectedIndex = searchResult.getSelectionModel().getSelectedIndex();

                    if (event.getCode().equals(KeyCode.UP) && selectedIndex > 0) {
                        searchResult.getSelectionModel().select(selectedIndex - 1);
                    }
                    if (event.getCode().equals(KeyCode.DOWN) && selectedIndex < searchResult.getItems().size()) {
                        searchResult.getSelectionModel().select(selectedIndex + 1);
                    }

                }
            }
        });

    }

    public void focusSearchQuery() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                queryInput.requestFocus();
            }
        });

    }

    public void setApp(App app) {
        this.app = app;
        searchResult.setItems(programList);
    }

    private void filterList(String q) {
        ArrayList<Note> tmp = app.searchForNote(q);

        programList.clear();
        programList.addAll(tmp);
        if (tmp.size() > 0) {
            searchResult.getSelectionModel().select(0);
        }
    }


    
}
