package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.SearchNoteTabController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;

public class ShowSearchForNoteDialog extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public ShowSearchForNoteDialog() {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        TabPane container = (TabPane) taskUtil.getStage().getScene().lookup("#notepadTabContainer");
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
