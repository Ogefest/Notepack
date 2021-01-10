package notepack.app.task;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Task;
import notepack.app.listener.GuiListener;

public class ShowUserMessage implements Task, TypeGui {

    @Override
    public void backgroundWork() {
    }

    public enum TYPE {
        INFO, ERROR
    }

    private String message;
    private ShowUserMessage.TYPE messageType;

    public ShowUserMessage(String message, ShowUserMessage.TYPE type) {
        this.message = message;
        this.messageType = type;
    }

    @Override
    public void guiWork(Stage stage, App app) {

        AlertType atype = messageType == TYPE.ERROR ? AlertType.ERROR : AlertType.INFORMATION;

        Alert alert = new Alert(atype);
        alert.setTitle("Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

}
