package notepack;

import java.security.Security;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setResources(ResourceBundle.getBundle("notepack.fonts.FontAwesome"));

        Image icon = new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Notepack");

        Parent root = loader.load();

        MainViewController ctrl = loader.getController();

        ctrl.hostServices = getHostServices();
        ctrl.appStart();

        Scene scene = new Scene(root);

        String regularMode = getClass().getResource("color-definition.css").toExternalForm();
        String darkMode = getClass().getResource("color-definition-dark.css").toExternalForm();

        scene.getStylesheets().add(regularMode);

        stage.setScene(scene);
        stage.setOnShowing((t) -> {
            ctrl.windowRestore();
        });

        stage.setTitle("NotePack");

        stage.show();
    }

}
