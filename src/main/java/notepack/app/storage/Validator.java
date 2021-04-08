package notepack.app.storage;

import java.io.File;

public class Validator {

    public static boolean isNameValid(String name) {

        File f = new File(name);

        String fname = f.getName();
        File directory = f.getParentFile();
        if (directory != null) {
            if (!directory.exists()) {
                return false;
            }
        }

        char[] invalid = {'<', '>', ':', '"', '/', '\\', '|', '?', '*'};
        for (char ch : invalid) {
            if (fname.indexOf(ch) > -1) {
                return false;
            }
        }

        return true;
    }

}
