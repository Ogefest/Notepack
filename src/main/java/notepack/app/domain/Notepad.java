package notepack.app.domain;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import notepack.encrypt.GPG;
import notepack.processor.GPGDecrypt;
import notepack.processor.GPGEncrypt;

public class Notepad {

    private NoteStorageMiddleware storage;
    private String name;
    private String ident;
    private GPG gpg;
    
    private HashMap<String, String> params = new HashMap<>();

    public Notepad(NoteStorage storage, String name) {
        this.storage = new NoteStorageMiddleware(storage);
        this.ident = UUID.randomUUID().toString();
        
        params.put("name", name);
    }

    public Notepad(NoteStorage storage, String name, String ident) {
        this.storage = new NoteStorageMiddleware(storage);
        this.name = name;
        this.ident = ident;

        params.put("name", name);
    }
    
    public void registerProcessors() {
        
        if (getParam("gpg-enabled").equals("1")) {
            gpg = new GPG(getParam("gpg-public-key"), getParam("gpg-private-key"), "");
            storage.registerAfterLoad(new GPGDecrypt(gpg));
            storage.registerBeforeSave(new GPGEncrypt(gpg));
        }
        
    }
    
    public String getName() {
        return params.get("name");
    }

    public NoteStorage getStorage() {

        return storage;
    }

    public String getIdent() {
        return ident;
    }

    public String getBackgroundColor() {

        if (!params.containsKey("color")) {

            Random rand = new Random();
            int r = (int) (rand.nextDouble() * 255);
            String rr = Integer.toHexString(r);
            int g = (int) (rand.nextDouble() * 255);
            String gg = Integer.toHexString(g);
            int b = (int) (rand.nextDouble() * 255);
            String bb = Integer.toHexString(b);

            setParam("color", "#" + rr + gg + bb);

            return "#" + rr + gg + bb;
        }

        return params.get("color");
    }

    public String getFontColor() {
        String bgColor = getBackgroundColor();

        int r = Integer.parseInt(bgColor.substring(1, 3));
        int g = Integer.parseInt(bgColor.substring(3, 5));
        int b = Integer.parseInt(bgColor.substring(5, 7));

        double y = (299 * r + 587 * g + 114 * b) / 1000;

        return y >= 138 ? "#000" : "#fff";
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
        if (key.equals("name")) {
            name = value;
        }
    }

    public String getParam(String key) {
        return params.getOrDefault(key, "");
    }

}
