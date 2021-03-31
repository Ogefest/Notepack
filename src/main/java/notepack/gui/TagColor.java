package notepack.gui;

public class TagColor {
    /**
     * @TODO create more sophisticated algo
     */
    public static String get(String input) {
        return String.format("#%06X", (0xFFFFFF & input.hashCode()));
    }
}
