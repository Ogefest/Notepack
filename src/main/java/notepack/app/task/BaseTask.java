package notepack.app.task;

import notepack.app.domain.MessageBus;
import notepack.app.domain.Task;

abstract public class BaseTask {

    protected MessageBus messageBus;
    protected Task afterDispatchTask;
    private long startNotEarlierThan = 0;

    public void setMessageBus(MessageBus mb) {
        this.messageBus = mb;
    }

    public void setAfterDispatchTask(Task t) {
        afterDispatchTask = t;
    }

    public void startTaskAfterSecondsFromNow(int seconds) {
        startNotEarlierThan = System.currentTimeMillis() + (seconds * 1000);
    }

    public boolean isTaskReadyToStart() {
        return startNotEarlierThan < System.currentTimeMillis();
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
