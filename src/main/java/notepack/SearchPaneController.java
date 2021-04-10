package notepack;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;
import notepack.app.task.NoteOpen;
import notepack.app.task.SearchPaneHide;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SearchPaneController implements Initializable {

    @FXML
    private ListView<Note> searchResult;

    @FXML
    private AnchorPane searchPanBackground;

    @FXML
    private TextField searchQueryInput;

    private ObservableList<Note> searchResultObservableList = FXCollections.observableArrayList();
    ;
    private App app;
    private String lastSearchQuery = "";

    public void setApp(App app) {
        this.app = app;
        searchResult.setItems(searchResultObservableList);
    }

    public void inputRequestFocus() {
        Platform.runLater(() -> searchQueryInput.requestFocus());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> searchQueryInput.requestFocus());

        searchResult.setCellFactory(noteListView -> new SearchNoteViewCell());

        searchQueryInput.setOnKeyReleased(keyEvent -> {

            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                app.addTask(new NoteOpen(searchResult.getSelectionModel().getSelectedItem()));
            }
            if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                app.addTask(new SearchPaneHide());
            }
            if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                int selectedIndex = searchResult.getSelectionModel().getSelectedIndex();
                if (searchResultObservableList.size() > selectedIndex) {
                    searchResult.getSelectionModel().select(selectedIndex + 1);
                }
            }
            if (keyEvent.getCode().equals(KeyCode.UP)) {
                int selectedIndex = searchResult.getSelectionModel().getSelectedIndex();
                if (selectedIndex > 0) {
                    searchResult.getSelectionModel().select(selectedIndex - 1);
                }
            }

            if (searchQueryInput.getText().length() > 1) {
                refreshSearchResult();
            }

        });
    }

    private void refreshSearchResult() {

        if (lastSearchQuery.equals(searchQueryInput.getText())) {
            return;
        }

        ArrayList<Note> result = app.searchForNote(searchQueryInput.getText());
        searchResultObservableList.clear();
        searchResultObservableList.addAll(result);
        if (result.size() > 0) {
            searchResult.getSelectionModel().select(0);
        }

        lastSearchQuery = searchQueryInput.getText();
    }

    @FXML
    void closeSearchPane(ActionEvent event) {
        app.addTask(new SearchPaneHide());
    }


}
