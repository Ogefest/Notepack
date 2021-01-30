package notepack;

import java.security.Security;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.fortuna.ical4j.util.MapTimeZoneCache;
import notepack.app.domain.Settings;
import notepack.app.storage.PreferencesSettings;

public class Main extends Application {

    public static void main(String[] args) {

        System.setProperty("net.fortuna.ical4j.timezone.cache.impl", MapTimeZoneCache.class.getName());

        Settings s = new PreferencesSettings();
        s.set("session.directory", System.getProperty("user.home"));
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--session-directory") && !args[i + 1].isEmpty()) {
                s.set("session.directory", args[i + 1]);
            }
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));

        Image icon = new Image(getClass().getResourceAsStream("logo.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Notepack");

        Parent root = loader.load();

        MainViewController ctrl = loader.getController();

        ctrl.hostServices = getHostServices();
        ctrl.appStart();

        Scene scene = new Scene(root);

        ctrl.setScene(scene);

        String regularMode = getClass().getResource("color-definition.css").toExternalForm();
        String darkMode = getClass().getResource("color-definition-dark.css").toExternalForm();

        stage.setScene(scene);
        stage.setOnShowing((t) -> ctrl.windowRestore());

        stage.setTitle("NotePack");

        stage.show();
    }

}
