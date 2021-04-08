package notepack.processor;

import notepack.app.domain.exception.MessageError;
import notepack.noterender.Render;

import java.io.File;
import java.util.ArrayList;

abstract public class NoteProcessor {

    abstract public byte[] run(byte[] input) throws MessageError;

    public boolean isAvailableForPath(String name) {

        ArrayList<String> exts = new ArrayList<>();

        String extension = "";

        int indexPos = name.lastIndexOf('.');
        int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));

        if (indexPos > pos) {
            extension = name.substring(indexPos + 1);
        }

        for (String ext : getSupportedExtensions()) {
            if (ext.equals(extension)) {
                return true;
            }
        }

        return false;
    }

    protected String[] getSupportedExtensions() {
        return Render.getSupportedExtensions().toArray(new String[0]);
    }
}
