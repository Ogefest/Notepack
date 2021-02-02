package notepack.app.domain;

import notepack.gui.TaskUtil;

abstract public class PopupController {
    private TaskUtil taskUtil;

    public void setTaskUtil(TaskUtil taskUtil) {
        this.taskUtil = taskUtil;
    }

    public TaskUtil getTaskUtil() {
        return taskUtil;
    }
}
