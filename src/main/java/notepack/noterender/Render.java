package notepack.noterender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import notepack.app.domain.Note;

public class Render {

    private static final ArrayList<String> txtExtensions = new ArrayList<>(Arrays.asList("txt", "json", "xml", "log", "ini", "csv", "yaml"));
    private static final ArrayList<String> mdExtension = new ArrayList<>(Arrays.asList("md"));
    private static final ArrayList<String> imageExtension = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png", "bmp"));
    private static final ArrayList<String> pdfExtension = new ArrayList<>(Arrays.asList("pdf"));

    public static String getFxml(Note note) {
        String name = note.getName();
        
        String extension = "";

        int indexPos = name.lastIndexOf('.');
        int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));

        if (indexPos > pos) {
            extension = name.substring(indexPos + 1);
        }
        
        return getFxml(extension);
    }

    public static String getFxml(String ext) {

        if (txtExtensions.contains(ext)) {
            return "/notepack/noterender/Textarea.fxml";
        }

        if (imageExtension.contains(ext)) {
            return "/notepack/noterender/ImageView.fxml";
        }
        
        if (pdfExtension.contains(ext)) {
            return "/notepack/noterender/PdfView.fxml";
        }
        
        if (mdExtension.contains(ext)) {
            return "/notepack/noterender/Markdown.fxml";
        }        

        return "/notepack/noterender/Textarea.fxml";
    }

    public static ArrayList<String> getSupportedExtensions() {

        ArrayList<String> result = new ArrayList<>();
        result.addAll(txtExtensions);
        result.addAll(imageExtension);
        result.addAll(pdfExtension);
        result.addAll(mdExtension);

        return result;
    }

}
