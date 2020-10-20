/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.noterender;

import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
//import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SplitPane;
import notepack.app.domain.App;
import notepack.app.domain.Note;

public class MarkdownController extends TextAreaController {

    @FXML
    private WebView markdownWebRender;

    private Parser parser;
    private HtmlRenderer renderer;

    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane rightPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSearch;
    @FXML
    private MenuButton btnActions;

    private String currentCssValue = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                //                FootnoteExtension.create(),
                TablesExtension.create(),
                TypographicExtension.create()
        ));

        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();

        markdownWebRender.setContextMenuEnabled(false);
        markdownWebRender.getEngine().setJavaScriptEnabled(false);

        textArea.textProperty().addListener((obs, old, niu) -> {
            refreshWebview(niu);
        });

    }

    private void refreshWebview(String value) {
        Node document = parser.parse(value);
        String html = renderer.render(document);
        String htmlDoc = "<html><head><style>" + currentCssValue + "</style></head><body>" + html + "</body>";
        markdownWebRender.getEngine().loadContent(htmlDoc);
    }

    @Override
    public void setState(App app, Note note) {
        super.setState(app, note);

        Platform.runLater(() -> {
            refreshPaneView(app.getSettings().get("markdown.view", "both"));
        });
    }

    @Override
    public void noteActivated() {
        super.noteActivated();

        refreshPaneView(app.getSettings().get("markdown.view", "both"));

        String currentThemeCss = app.getSettings().get("color-definition", "color-definition.css");
        String cssToSet = "/notepack/noterender/markdown-light.css";
        if (!currentThemeCss.equals("color-definition.css")) {
            cssToSet = "/notepack/noterender/markdown-dark.css";
        }

        try {
            currentCssValue = new String(getClass().getResourceAsStream(cssToSet).readAllBytes());
        } catch (IOException ex) {
            Logger.getLogger(MarkdownController.class.getName()).log(Level.SEVERE, null, ex);
        }

        refreshWebview(textArea.getText());
    }

    @FXML
    private void onChangeViewMode(ActionEvent event) {

        String currentView = app.getSettings().get("markdown.view", "both");
        String newValue = "";
        if (currentView.equals("both")) {
            newValue = "editor";
        } else if (currentView.equals("editor")) {
            newValue = "renderer";
        } else {
            newValue = "both";
        }
        app.getSettings().set("markdown.view", newValue);
        refreshPaneView(newValue);
    }

    private void refreshPaneView(String value) {

        boolean isVisible = true;

        if (value.equals("both")) {
            splitPane.getItems().clear();
            splitPane.getItems().addAll(leftPane, rightPane);
            isVisible = true;
        }
        if (value.equals("editor")) {
            splitPane.getItems().clear();
            splitPane.getItems().add(leftPane);
            isVisible = true;
        }
        if (value.equals("renderer")) {
            splitPane.getItems().clear();
            splitPane.getItems().add(rightPane);
            isVisible = false;
        }

        btnSave.setVisible(isVisible);
        btnSearch.setVisible(isVisible);
        btnActions.setVisible(isVisible);

        btnSave.setManaged(isVisible);
        btnSearch.setManaged(isVisible);
        btnActions.setManaged(isVisible);

    }

}
