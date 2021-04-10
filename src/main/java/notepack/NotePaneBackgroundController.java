package notepack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.noterender.NoteRenderController;

public class NotePaneBackgroundController {

    @FXML
    private TabPane workspaceContainer;

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

                app.selectNoteInWorkspace(activatedTab.getNote());

            }

        });

    }

}
