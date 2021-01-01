package notepack.app.utils;

import javafx.scene.control.Label;

public class Icon {
    public static Label get(String name) {
        Label l = new Label("");
        l.getStyleClass().addAll("icon-base", name);

        return l;
    }
}
