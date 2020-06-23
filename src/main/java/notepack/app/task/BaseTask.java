package notepack.app.task;

import notepack.app.domain.MessageBus;

abstract public class BaseTask {

    protected MessageBus messageBus;

    public void setMessageBus(MessageBus mb) {
        this.messageBus = mb;
    }
}
