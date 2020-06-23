package notepack.app.task;

import java.util.Optional;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import notepack.app.domain.App;
import notepack.app.domain.Notepad;
import notepack.app.domain.Task;
import notepack.app.listener.GuiListener;

public class ShowGPGPasswordDialog implements Task, TypeGui {

    @Override
    public void dispatch() {
    }

    private Notepad notepad;

    public ShowGPGPasswordDialog(Notepad notepad) {
        this.notepad = notepad;

    }

    @Override
    public void proceed(Stage stage, App app) {

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Dialog");
        dialog.setHeaderText("Your GPG key need password to open");
//        dialog.setGraphic(new Circle(15, Color.RED)); // Custom graphic
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Password"), pwd);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                notepad.getGpg().setPassword(pwd.getText());
                dialog.close();
//                if (notepad.getGpg().isPrivateKeyLoaded()) {
//                }

            }
            return null;
        });
        dialog.showAndWait();

    }

}
