/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack.noterender;

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

/**
 * FXML Controller class
 *
 * @author lg
 */
public class MarkdownController extends TextAreaController {

    @FXML
    private AnchorPane tabBackground;
    @FXML
    private MenuItem menuUndo;
    @FXML
    private MenuItem menuRedo;
    @FXML
    private MenuItem menuCut;
    @FXML
    private MenuItem menuCopy;
    @FXML
    private CheckMenuItem wordWrapMenu;
    @FXML
    private TextArea textArea;
    @FXML
    private WebView markdownWebRender;

    private Parser parser;
    private HtmlRenderer renderer;

//    @FXML
//    private AnchorPane tabBackground;
//    @FXML
//    private MenuItem menuUndo;
//    @FXML
//    private MenuItem menuRedo;
//    @FXML
//    private MenuItem menuCut;
//    @FXML
//    private MenuItem menuCopy;
//    @FXML
//    private CheckMenuItem wordWrapMenu;
//    @FXML
//    private TextArea textArea;
//    /**
//     * Initializes the controller class.
//     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        MutableDataSet options = new MutableDataSet();
        options.setFrom(ParserEmulationProfile.MULTI_MARKDOWN);
        
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();

        textArea.textProperty().addListener((obs,old,niu) -> {
            Node document = parser.parse(niu);
            String html = renderer.render(document);
            markdownWebRender.getEngine().loadContent(html);

        });
    }

    @FXML
    private void onSaveNote(ActionEvent event) {
    }

    @FXML
    private void onSearchInNote(ActionEvent event) {
    }

    @FXML
    private void onUndo(ActionEvent event) {
    }

    @FXML
    private void onRedo(ActionEvent event) {
    }

    @FXML
    private void onCut(ActionEvent event) {
    }

    @FXML
    private void onCopy(ActionEvent event) {
    }

    @FXML
    private void onPaste(ActionEvent event) {
    }

    @FXML
    private void onSelectAll(ActionEvent event) {
    }

    @FXML
    private void onWordWrap(ActionEvent event) {
    }

    @FXML
    private void onCloseNote(ActionEvent event) {
    }

}
