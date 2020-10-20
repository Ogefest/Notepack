module notepack {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.net.http;
    requires java.xml;
    requires java.sql;

    opens notepack to javafx.fxml;
    opens notepack.engine to javafx.fxml;
    opens notepack.noterender to javafx.fxml;
    exports notepack;
    exports notepack.engine;
    exports notepack.noterender;

    requires java.logging;
    requires java.prefs;
    requires org.json;
    requires java.base;
    requires org.bouncycastle.pg;
    requires org.bouncycastle.provider;

    requires flexmark;
    
    requires flexmark.ext.abbreviation;
    requires flexmark.ext.autolink;
    requires flexmark.ext.definition;
    requires flexmark.ext.gfm.strikethrough;
    requires flexmark.ext.tables;
    requires flexmark.ext.typographic;
    requires flexmark.util.ast;
    requires flexmark.util.data;
    requires flexmark.util.misc;

}
