package notepack.app.task;

import javafx.scene.layout.AnchorPane;
import notepack.SearchPaneController;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;

public class SearchPaneShow extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public SearchPaneShow() {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {

        taskUtil.showSearchPane();

        AnchorPane container = (AnchorPane) taskUtil.getStage().getScene().lookup("#searchPaneBackground");
        SearchPaneController ctrl = (SearchPaneController) container.getUserData();
        ctrl.inputRequestFocus();

    }
}
