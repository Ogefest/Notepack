package notepack.app.listener;

import notepack.app.domain.EventListener;
import notepack.app.domain.Workspace;

public interface WorkspaceListener extends EventListener {
    public void onOpen(Workspace workspace);
    public void onClose(Workspace workspace);
}
