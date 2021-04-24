package notepack.app.domain;

import java.util.ArrayList;
import java.util.List;

public class ClipboardManager {
    private ArrayList<String> contents = new ArrayList<>();
    private int clipboardSize = 10;

    public void add(String str) {
        int currentIndex = contents.indexOf(str);
        if (currentIndex >= 0) {
            return;
        }

        contents.add(str);
        if (contents.size() > clipboardSize) {
            int endIndex = contents.size();
            int startIndex = endIndex - clipboardSize;
            List<String> tmp = contents.subList(startIndex, endIndex);
            contents = new ArrayList<>(tmp);
        }
    }

    public ArrayList<String> getContents() {
        return contents;
    }

    public void clearAll() {
        contents.clear();
    }


}
