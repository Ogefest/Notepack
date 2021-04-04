package notepack.app.task;

import notepack.app.domain.Note;
import notepack.app.domain.Task;
import notepack.app.domain.Workspace;
import notepack.app.domain.exception.MessageError;
import notepack.app.listener.WorkspaceListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class WorkspaceUploadFile extends BaseTask implements Task, TypeWorkspace {

    private File fileSource;
    private Workspace workspace;

    public WorkspaceUploadFile(Workspace workspace, File fileSource) {
        this.workspace = workspace;
        this.fileSource = fileSource;
    }

    @Override
    public void notify(WorkspaceListener listener) {
    }

    @Override
    public void backgroundWork() throws MessageError {

        byte[] bytesToSave;
        try {
            bytesToSave = Files.readAllBytes(fileSource.toPath());

            Note n = new Note(workspace);
            n.setPath(workspace.getStorage().getBasePath() + File.separator + fileSource.getName());
            n.setContents(bytesToSave);
            n.saveToStorage();

            addTaskToQueue(new WorkspaceRefresh(workspace));

        } catch (IOException e) {
            e.printStackTrace();
            throw new MessageError(e.getMessage(), e);
        }


    }
}
