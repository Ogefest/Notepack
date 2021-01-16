package notepack.app.task;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.gui.TaskUtil;
import notepack.noterender.NoteRenderController;

public class ToggleTheme extends BaseTask implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    Scene scene;

    public ToggleTheme(Scene scene) {
        this.scene = scene;
    }

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        app.getTheme().toggle(scene);

        TabPane tabContainer = taskUtil.getNotesContainer();
        Tab t = tabContainer.getSelectionModel().getSelectedItem();
        ((NoteRenderController) t.getUserData()).noteActivated();

    }
}
