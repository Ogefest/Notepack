package notepack.app.storage;

public class Validator {

    public static boolean isNameValid(String name) {

        char[] invalid = {'<', '>', ':', '"', '/', '\\', '|', '?', '*'};
        for (char ch : invalid) {
            if (name.indexOf(ch) > -1) {
                return false;
            }
        }

        return true;
    }

}
