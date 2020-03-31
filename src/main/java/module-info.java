module notepack {
    requires javafx.controls;
    requires javafx.fxml;

    opens notepack to javafx.fxml;
    exports notepack;
    requires java.logging;
    requires java.prefs;
    requires org.json;
}