package notepack.app.task;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import notepack.NotebookTabController;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.domain.exception.MessageError;
import notepack.gui.TabNotepad;
import notepack.gui.TaskUtil;

import java.util.ArrayList;

public class SaveSession extends BaseTask implements Task, TypeRecurring, TypeGui {
    @Override
    public void backgroundWork() throws MessageError {

    }

    @Override
    public int getInterval() {
        return 10;
    }

    private String notepadCacheString = "";

    @Override
    public void guiWork(TaskUtil taskUtil, App app) {
        TabPane tabs = taskUtil.getNotepadContainer();

        ArrayList<Notepad> notepadsToSave = new ArrayList<>();
        String tmpKey = "";
        for (Tab tab : tabs.getTabs()) {
            if (tab instanceof TabNotepad) {
                NotebookTabController ctrl = (NotebookTabController) tab.getUserData();

                notepadsToSave.add(ctrl.getNotepad());
                tmpKey += ctrl.getNotepad().getIdent();
            }
        }
        if (!tmpKey.equals(notepadCacheString)) {
            app.getSessionStorage().setNotepadList(notepadsToSave);
            notepadCacheString = tmpKey;
        }

    }
}
