package notepack.gui;


import org.json.JSONObject;

import java.io.IOException;

public class MaterialIconLoader {

    private static JSONObject icons;

    public static String getSVG(String name) {
        if (icons == null) {
            load();
        }

        if (icons.has(name)) {
            return icons.getString(name);
        }
        return "";
    }

    private static void load() {

        try {
            String content = new String(MaterialIconLoader.class.getResourceAsStream("/notepack/material.icons.json").readAllBytes());

            icons = new JSONObject(content);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
