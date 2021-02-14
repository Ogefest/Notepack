package notepack.app.task;

import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TaskUtil;

public class TodoRefresh extends BaseTask implements Task,TypeGui {


    @Override
    public void backgroundWork() throws MessageError {

    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
//        TodoPaneBackgroundController ctrl = (TodoPaneBackgroundController) taskUtil.getTodoPane().getUserData();
//        if (ctrl != null) {
//            ctrl.refreshTodolist();
//        }
    }

}
