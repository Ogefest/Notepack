package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;

public class ShowTodoView extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        taskUtil.showTodoPane();

    }
}
