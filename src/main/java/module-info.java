module notepack {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.xml;
    requires java.sql;

    opens notepack to javafx.fxml;
    opens notepack.engine to javafx.fxml;
    exports notepack;
    exports notepack.engine;

    requires java.logging;
    requires java.prefs;
    requires org.json;
    requires java.base;
    requires org.bouncycastle.pg;
    requires org.bouncycastle.provider;
}
