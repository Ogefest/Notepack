package notepack.app.domain;

import notepack.app.storage.JsonMeta;
import notepack.processor.ConvertFromUtf8;
import notepack.processor.ConvertToUtf8;
import notepack.processor.ZipDecrypt;
import notepack.processor.ZipEncrypt;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Workspace {

    private NoteStorageMiddleware storage;
    private NoteMetaStorage meta;

    private String name;
    private String ident;

    private HashMap<String, String> params = new HashMap<>();

    public Workspace(NoteStorage storage, String name) {
        this.storage = new NoteStorageMiddleware(storage);
        this.ident = UUID.randomUUID().toString();

        params.put("name", name);
    }

    public Workspace(NoteStorage storage, String name, String ident) {
        this.storage = new NoteStorageMiddleware(storage);
        this.name = name;
        this.ident = ident;

        params.put("name", name);
    }

    public void registerProcessors() {
        if (getParam("encryption-enabled").equals("1")) {
            String pwd = getParam("encryption-password");
            storage.registerAfterLoad(new ZipDecrypt(pwd));
            storage.registerBeforeSave(new ZipEncrypt(pwd));
        }
        if (getParam("convert-to-utf8").equals("1")) {
            storage.registerAfterLoad(new ConvertFromUtf8());
            storage.registerBeforeSave(new ConvertToUtf8());
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

    public NoteMeta getMetaForNote(Note note) {
        return new NoteMeta(new JsonMeta(getStorage(), note.getIdent()));
    }

    public String toString() {
        return getName();
    }

}
