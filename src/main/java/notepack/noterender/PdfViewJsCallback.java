package notepack.noterender;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PdfViewJsCallback {

    private byte[] pdfContent;
    private int currentPage = 0;
    private PdfViewJsPageTracker tracker;

    public PdfViewJsCallback(byte[] content, PdfViewJsPageTracker tracker) {
        this.pdfContent = content;
        this.tracker = tracker;
    }

    public String getBase64Content() {
        return Base64.getEncoder().encodeToString(pdfContent);
    }

    public void log(String message) {
        Logger.getLogger(PdfViewController.class.getName()).log(Level.SEVERE, message);
    }

    public void setCurrentPageInfo(int currentPage, int maxPage) {
        if (this.currentPage == currentPage) {
            return;
        }

        this.currentPage = currentPage;
        tracker.pageChanged(currentPage, maxPage);
    }
}
