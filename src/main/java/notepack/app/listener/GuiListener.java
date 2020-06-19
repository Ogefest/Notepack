package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.task.ShowUserMessage;
import notepack.app.task.TypeGui;

public interface GuiListener extends EventListener {
    public void proceed(TypeGui task);
}
