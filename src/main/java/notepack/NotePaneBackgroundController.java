package notepack;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.gui.Icon;
import notepack.noterender.NoteRenderController;

import java.io.IOException;

public class NotePaneBackgroundController {

    @FXML
    private TabPane notepadContainer;

    @FXML
    private TabPane tabContainer;

    private App app;


    public void setApp(App app) {
        this.app = app;

        tabContainer.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                if (t == null || t1 == null) {
                    return;
                }

                NoteRenderController activatedTab = (NoteRenderController) t1.getUserData();
                activatedTab.noteActivated();

                NoteRenderController deactivatedTab = (NoteRenderController) t.getUserData();
                deactivatedTab.noteDeactivated();

                app.selectNoteInNotepad(activatedTab.getNote());

            }

        });

        initDefaultTab();
    }

    private void initDefaultTab() {

        Tab searchNoteTab = new Tab();
        searchNoteTab.setId("searchNoteTab");

        Node tabContent;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchNoteTab.fxml"));

            tabContent = loader.load();

            SearchNoteTabController ctrl = loader.getController();
            searchNoteTab.setContent(tabContent);
            searchNoteTab.setUserData(ctrl);
            ctrl.setApp(app);

            searchNoteTab.setGraphic(Icon.get("mi-magnify"));
            searchNoteTab.setStyle("-fx-background-color: card-background; -fx-border-color: card-background" );
            searchNoteTab.getStyleClass().add("button");

            Platform.runLater(() -> notepadContainer.getTabs().add(searchNoteTab));

            notepadContainer.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals(searchNoteTab)) {
                    SearchNoteTabController c = (SearchNoteTabController) searchNoteTab.getUserData();
                    c.focusSearchQuery();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
