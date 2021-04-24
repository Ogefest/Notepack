package notepack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import notepack.app.domain.App;
import notepack.app.domain.PopupController;

import java.net.URL;
import java.util.ResourceBundle;

public class ClipboardPopupController extends PopupController implements Initializable {

    private App app;
    ObservableList<String> items = FXCollections.observableArrayList();

    @FXML
    private ListView<String> itemList;
    @FXML
    private CheckBox isClipboardEnabledCheckbox;

    public void setApp(App app) {
        this.app = app;
        itemList.setItems(items);
        itemList.getSelectionModel().selectedItemProperty().addListener((observableValue, s, label) -> {
            setClipboardFromSelectedListView();
        });
        itemList.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                setClipboardFromSelectedListView();
                getTaskUtil().closePopup();
            }
        });

        for (String c : app.getClipboardManager().getContents()) {
            if (c.length() > 100) {
                items.add(c.substring(0, 100));
            } else {
                items.add(c);
            }
        }

        if (app.getSettings().get("clipboard-manager", "1").equals("1")) {
            isClipboardEnabledCheckbox.setSelected(true);
            itemList.setDisable(false);
        } else {
            isClipboardEnabledCheckbox.setSelected(false);
            itemList.setDisable(true);
        }
    }

    private void setClipboardFromSelectedListView() {


        int index = itemList.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            return;
        }
        Clipboard clipboard = Clipboard.getSystemClipboard();

        String contentToClipboard = app.getClipboardManager().getContents().get(index);

        ClipboardContent content = new ClipboardContent();
        content.putString(contentToClipboard);
        clipboard.setContent(content);
    }

    @FXML
    void changeClipboardActive(ActionEvent event) {

        if (app.getSettings().get("clipboard-manager", "1").equals("1")) {
            app.getSettings().set("clipboard-manager", "0");
            items.clear();
            app.getClipboardManager().clearAll();
        } else {
            app.getSettings().set("clipboard-manager", "1");
        }
    }

    @FXML
    void clearAll(ActionEvent event) {
        app.getClipboardManager().clearAll();
        items.clear();
    }

    @FXML
    void closePopup(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
