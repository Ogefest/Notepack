package notepack.noterender;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import notepack.app.domain.Note;

import netscape.javascript.JSObject;
import notepack.app.domain.App;

//import javafx.scene.web.WebView;
public class PdfViewController implements Initializable, NoteRenderController {
    
    @FXML
    private AnchorPane tabBackground;
    
    private Note note;
    private App app;
    
    @FXML
    private WebView webView;
    
    private WebEngine webEngine;
    @FXML
    private TextField pageNumber;
    @FXML
    private Label labelMaxPage;
    @FXML
    private ComboBox<String> zoomSelect;
    
    private String lastPageValue = "1";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        
        String viewerUrl = getClass().getResource("/notepack/noterender/pdfjs/web/viewer.html").toExternalForm();
        webEngine.load(viewerUrl);
        
        ObservableList<String> list = zoomSelect.getItems();
        list.add("Auto");
        list.add("Page fit");
        list.add("Page width");
        list.add("25%");
        list.add("50%");
        list.add("100%");
        list.add("200%");
        
        pageNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            
            if (newValue.equals(lastPageValue)) {
                return;
            }
            
            try {
                int number = Integer.parseInt(newValue);
                webEngine.executeScript("PDFViewerApplication.pdfViewer.currentPageNumber = " + number);
                lastPageValue = newValue;
            } catch (Exception e) {
                pageNumber.setText(oldValue);
            }
            
        });
    }
    
    @FXML
    private void onCloseNote(ActionEvent event) {
        app.closeNote(note);
    }
    
    @Override
    public Note getNote() {
        return note;
    }
    
    @Override
    public void setState(App app,Note note) {
        this.note = note;
        this.app = app;
        
        tabBackground.setStyle("-fx-background-color: " + note.getNotepad().getBackgroundColor());
        
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    
                    if (newValue == Worker.State.SUCCEEDED) {
                        
                        PdfViewJsCallback clbk = new PdfViewJsCallback(note.getContent(), (page, maxPage) -> {
                            pageNumber.setText(Integer.toString(page));
                            labelMaxPage.setText("of " + Integer.toString(maxPage));
                            labelMaxPage.setVisible(true);
                        });
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("app", clbk);
                        
                        webEngine.executeScript("console.log = function(message){ app.log(message); };");
                        webEngine.executeScript("window.loadPdf()");
                        
                        noteActivated();
                    }
                    
                });
        
    }
    
    @Override
    public void noteActivated() {
        
        String cssDef = app.getSettings().get("color-definition", "color-definition.css");
        
        if (cssDef.equals("color-definition.css")) {
            webEngine.executeScript("document.body.style.background = \"white\";");
        } else {
            webEngine.executeScript("document.body.style.background = \"#24242e\";");
        }
        
    }

    @Override
    public void noteDeactivated() {
    }

    @FXML
    private void onPrevPage(ActionEvent event) {
        webEngine.executeScript("PDFViewerApplication.pdfViewer.currentPageNumber -= 1");
    }

    @FXML
    private void onNextPage(ActionEvent event) {
        webEngine.executeScript("PDFViewerApplication.pdfViewer.currentPageNumber += 1");
    }

    @FXML
    private void onZoomChanged(ActionEvent event) {
        String currentZoom = zoomSelect.getValue();
        
        String cmd = "auto";
        
        switch (currentZoom) {
            case "Auto":
                cmd = "auto";
                break;
            case "Page fit":
                cmd = "page-fit";
                break;
            case "Page width":
                cmd = "page-width";
                break;
            case "25%":
                cmd = "0.25";
                break;
            case "50%":
                cmd = "0.5";
                break;
            case "75%":
                cmd = "0.75";
                break;
            case "100%":
                cmd = "1";
                break;
            case "200%":
                cmd = "2";
                break;
            default:
                cmd = "auto";
        }
        
        webEngine.executeScript("PDFViewerApplication.pdfViewer._setScale('"+cmd+"')");
    }
    
}
