package notepack.app.domain;

import notepack.app.domain.exception.MessageError;

public interface Task {

    public void backgroundWork() throws MessageError;
}
