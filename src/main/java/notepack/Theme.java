package notepack;

import javafx.scene.Scene;
import notepack.app.domain.Settings;

public class Theme {

    private Settings appSettings;

    public Theme(Settings settings) {
        this.appSettings = settings;
    }

    public void set(String name, Scene scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Theme.class.getResource("/notepack/" + name).toExternalForm());
    }

    public void toggle(Scene scene) {

        if (appSettings.get("color-definition", "color-definition.css").equals("color-definition.css")) {
            appSettings.set("color-definition", "color-definition-dark.css");
        } else {
            appSettings.set("color-definition", "color-definition.css");
        }

        setCurrent(scene);
    }

    public void setCurrent(Scene scene) {
        String cssToSet = appSettings.get("color-definition", "color-definition.css");
        set(cssToSet, scene);
    }

}
