package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.task.ShowUserMessage;

public interface GuiListener extends EventListener {
    public void onMessage(String message, ShowUserMessage.TYPE type);
}
