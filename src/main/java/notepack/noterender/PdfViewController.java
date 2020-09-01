package notepack.noterender;

import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import notepack.NoteTabContentCallback;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        
        String viewerUrl = getClass().getResource("/notepack/noterender/pdfjs/web/viewer.html").toExternalForm();
        webEngine.load(viewerUrl);
        
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
        
        webEngine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    
                    if (newValue == Worker.State.SUCCEEDED) {
                        
                        PdfViewJsCallback clbk = new PdfViewJsCallback(note.getContent());
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("app", clbk);
                        
                        webEngine.executeScript("console.log = function(message){ app.log(message); };");
                        webEngine.executeScript("window.loadPdf()");
                    }
                    
                });
        
    }
    
    @Override
    public void noteActivated() {
    }

    @Override
    public void noteDeactivated() {
    }
    
}
