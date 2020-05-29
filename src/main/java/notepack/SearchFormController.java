package notepack;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 */
public class SearchFormController implements Initializable {


    @FXML
    private Button btnCancel;
    @FXML
    private TextField searchForInput;
    @FXML
    private TextField replaceWithInput;
    @FXML
    private Button searchReplaceBtn;
    
    private SearchFormCallback searchFormCallback;
    @FXML
    private CheckBox replaceAllCheckbox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        searchForInput.textProperty().addListener((o) -> {
            if (searchForInput.getText().length() == 0) { 
                searchReplaceBtn.setDisable(true);
            } else {
                searchReplaceBtn.setDisable(false);
            }
        });
        
        replaceWithInput.textProperty().addListener((o) -> {
            if (replaceWithInput.getText().length() == 0) {
                searchReplaceBtn.setText("Search");
            } else {
                searchReplaceBtn.setText("Replace");
            }
        });
    }    
    
    public void setCallback(SearchFormCallback clbk) {
        searchFormCallback = clbk;
    }
    
    @FXML
    private void onSearchKeyUp(KeyEvent event) {
    }

    @FXML
    private void onReplaceKeyUp(KeyEvent event) {
    }

    @FXML
    private void onSearchReplace(ActionEvent event) {
        
        String search = searchForInput.getText();
        String replace = replaceWithInput.getText();
        boolean replaceAll = replaceAllCheckbox.isSelected();
        
        if (replace.length() > 0 && search.length() > 0) {
            searchFormCallback.replace(search, replace, replaceAll);
            if (replaceAll) {
                closeForm();
            }
        } else if (search.length() > 0) {
            searchFormCallback.search(search);
        }
        
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeForm();
    }
    
    private void closeForm() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();        
    }

}
