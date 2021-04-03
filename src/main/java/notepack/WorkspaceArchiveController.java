package notepack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import notepack.app.domain.App;
import notepack.app.domain.PopupController;
import notepack.app.domain.Workspace;

public class WorkspaceArchiveController extends PopupController {

    @FXML
    private Button openSelectedBtn;

    @FXML
    private ListView<Workspace> workspaceList;

    @FXML
    private Button deleteSelectedBtn;

    private App app;
    private ObservableList<Workspace> items = FXCollections.observableArrayList();

    public void setApp(App app) {
        this.app = app;

        workspaceList.setItems(items);

        for (Workspace w : app.getAvailableWorkspaces()) {
            if (w.getParam("is_archived").equals("1")) {
                items.add(w);
            }
        }

        openSelectedBtn.setDisable(true);
        deleteSelectedBtn.setDisable(true);

        workspaceList.getSelectionModel().selectedItemProperty().addListener((observableValue, workspace, t1) -> {
            if (workspaceList.getSelectionModel().getSelectedItems().isEmpty()) {
                openSelectedBtn.setDisable(true);
                deleteSelectedBtn.setDisable(true);
            } else {
                openSelectedBtn.setDisable(false);
                deleteSelectedBtn.setDisable(false);
            }
        });

    }

    @FXML
    void onOpenSelected(ActionEvent event) {
        app.openWorkspace(workspaceList.getSelectionModel().getSelectedItem());
        workspaceList.getSelectionModel().getSelectedItem().setParam("is_archived", "");
        getTaskUtil().closePopup();
    }

    @FXML
    void onCloseSelected(ActionEvent event) {
        app.closeWorkspace(workspaceList.getSelectionModel().getSelectedItem());
        getTaskUtil().closePopup();
    }

    @FXML
    void onClose(ActionEvent event) {
        getTaskUtil().closePopup();
    }

}
