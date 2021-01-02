package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import notepack.SearchNoteTabController;
import notepack.app.domain.App;
import notepack.app.domain.Task;

public class ShowSearchForNoteDialog extends BaseTask implements Task, TypeGui {

    @Override
    public void dispatch() {
    }

    public ShowSearchForNoteDialog() {
    }

    @Override
    public void proceed(Stage parentStage, App app) {

        TabPane container = (TabPane) parentStage.getScene().lookup("#notepadTabContainer");
        for (Tab tab : container.getTabs()) {
            if (tab.getId().equals("searchNoteTab")) {
                container.getSelectionModel().select(tab);

                SearchNoteTabController c = (SearchNoteTabController) tab.getUserData();
                c.focusSearchQuery();
                break;
            }
        }

    }
}
