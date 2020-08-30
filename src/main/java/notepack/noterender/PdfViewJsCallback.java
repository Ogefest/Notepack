package notepack.noterender;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PdfViewJsCallback {
        private byte[] pdfContent;

        public PdfViewJsCallback(byte[] content) {
            this.pdfContent = content;
        }

        public String getBase64Content() {
            return Base64.getEncoder().encodeToString(pdfContent);
        }
        
        public void log(String message) {
            Logger.getLogger(PdfViewController.class.getName()).log(Level.SEVERE, message);
        }
}
