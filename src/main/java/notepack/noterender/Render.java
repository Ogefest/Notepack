package notepack.noterender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import notepack.app.domain.Note;

public class Render {

    private static final ArrayList<String> txtExtensions = new ArrayList<>(Arrays.asList("txt", "json", "xml", "log", "ini", "md", "csv", "yaml"));
    private static final ArrayList<String> imageExtension = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png", "bmp"));

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
            return "noterender/Textarea.fxml";
        }

        if (imageExtension.contains(ext)) {
            return "noterender/ImageView.fxml";
        }

        return "noterender/Textarea.fxml";
    }

    public static ArrayList<String> getSupportedExtensions() {

        ArrayList<String> result = new ArrayList<>();
        result.addAll(txtExtensions);
        result.addAll(imageExtension);

        return result;

    }

}
