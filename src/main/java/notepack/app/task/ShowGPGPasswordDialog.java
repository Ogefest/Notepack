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

public class ShowGPGPasswordDialog extends BaseTask implements Task, TypeGui {

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
        dialog.setHeaderText("Your GPG key need password to open " + notepad.getName());
//        dialog.setGraphic(new Circle(15, Color.RED)); // Custom graphic
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PasswordField pwd = new PasswordField();
        HBox content = new HBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setSpacing(10);
        content.getChildren().addAll(new Label("Password"), pwd);
        dialog.getDialogPane().setContent(content);
        dialog.setOnCloseRequest((t) -> {
            if (!notepad.getGpg().isPrivateKeyLoaded()) {

                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("GPG encrypted notepad is not opened. Do you want to close it?");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");

                alert.getButtonTypes().setAll(yesButton, noButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == yesButton) {
                    app.closeNotepad(notepad);
                }

            }
        });
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {

                notepad.getGpg().setPassword(pwd.getText());
                if (notepad.getGpg().isPrivateKeyLoaded()) {
                    dialog.close();
                } else {
                    Alert errorAlert = new Alert(AlertType.ERROR);
                    errorAlert.setHeaderText("Invalid password");
                    errorAlert.setContentText("Could not open private key using this password");
                    errorAlert.showAndWait();
                }
            }

            return null;
        });
        dialog.showAndWait();

    }

}
