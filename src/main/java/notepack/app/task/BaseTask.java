package notepack.app.task;

import notepack.app.domain.MessageBus;
import notepack.app.domain.Task;

abstract public class BaseTask {

    protected MessageBus messageBus;
    protected Task afterDispatchTask;

    public void setMessageBus(MessageBus mb) {
        this.messageBus = mb;
    }

    public void setAfterDispatchTask(Task t) {
        afterDispatchTask = t;
    }

    protected void runNextTaskInChain() {
        if (messageBus != null && afterDispatchTask != null) {
            messageBus.addTask(afterDispatchTask);
        }
    }

    protected void addTaskToQueue(Task t) {
        messageBus.addTask(t);
    }
}
