package notepack.app.storage;

//import com.github.sardine.DavResource;
//import com.github.sardine.Sardine;
//import com.github.sardine.SardineFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;

public class Webdav implements NoteStorage {

    private NoteStorageConfiguration nsc;

    private ArrayList<NoteStorageItem> items = new ArrayList<>();

    private NoteStorageItem rootItem;

    private int deep = 0;

//    private Sardine sardine;

    private ArrayList<String> added = new ArrayList<String>();

    public Webdav() {
    }

    public Webdav(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
//        sardine = SardineFactory.begin(nsc.get("username"), nsc.get("password"));
    }

    private String getUrlWithPath(String path) {
        return nsc.get("url") + path;
    }

    @Override
    public String loadContent(String path) {

        String result = "";

//        try {
//            InputStream is = sardine.get(path);
//            result = new String(is.readAllBytes());
//
//        } catch (IOException ex) {
//            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
//        }

        return result;
    }

    @Override
    public NoteStorageItem getItemsInStorage() {

        if (rootItem == null) {
            rootItem = new NoteStorageItem(nsc.get("url"), "WebDav");
        }

        return rootItem;
    }

    @Override
    public void refreshItemsInStorage() {

        added.clear();

        rootItem = new NoteStorageItem(".", "WebDav");
//        rootItem = addItems(rootItem, nsc.get("url"), 0);

    }

//    private NoteStorageItem addItems(NoteStorageItem parent, String startPath, int deep) {
//        if (deep > 5) {
//            return parent;
//        }
//
//        ArrayList<String> supportedExtensions = new ArrayList<>();
//        supportedExtensions.add("txt");
//        supportedExtensions.add("ini");
//        supportedExtensions.add("json");
//        supportedExtensions.add("xml");
//        supportedExtensions.add("md");
//        supportedExtensions.add("csv");
//        supportedExtensions.add("yaml");
//        supportedExtensions.add("log");
//
//        List<DavResource> resources;
//
//        try {
//            resources = sardine.list(startPath);
//            if (resources.size() == 0) {
//                return parent;
//            }
//
//            String hostnameWithSchema = startPath.replace(resources.get(0).getPath(), "");
//            resources.remove(0);
//
//            for (DavResource res : resources) {
//
//                if ((hostnameWithSchema + res.getHref().toASCIIString()).equals(startPath)) {
//                    continue;
//                }
//
//                if (added.contains(hostnameWithSchema + res.getHref().toASCIIString())) {
//                    continue;
//                }
//
//                added.add(hostnameWithSchema + res.getHref().toASCIIString());
//
//                String name = res.getName();
//                String extension = "";
//
//                int i = name.lastIndexOf('.');
//                int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));
//
//                if (i > pos) {
//                    extension = name.substring(i + 1);
//                }
//                if (supportedExtensions.contains(extension)) {
//                    parent.add(new NoteStorageItem(hostnameWithSchema + res.getHref().toASCIIString(), res.getName(), res.getContentLength(), res.getModified().getTime()));
//                } else if (res.isDirectory()) {
//
////                    continue;
//                    NoteStorageItem newDirectoryParent = new NoteStorageItem(hostnameWithSchema + res.getHref().toASCIIString(), res.getName(), res.getContentLength(), res.getModified().getTime());
//
//                    newDirectoryParent = addItems(newDirectoryParent, hostnameWithSchema + res.getHref().toASCIIString(), deep + 1);
//                    if (newDirectoryParent.get().size() > 0) {
//                        parent.add(newDirectoryParent);
//                    }
//
//                }
//
//            }
//
//        } catch (IOException ex) {
//            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return parent;
//    }

    @Override
    public void saveContent(String content, String path) {

//        try {
//            sardine.put(path, content.getBytes());
//        } catch (IOException ex) {
//            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void setConfiguration(NoteStorageConfiguration nsc) {
//        this.nsc = nsc;
//        sardine = SardineFactory.begin(nsc.get("username"), nsc.get("password"));
    }

    @Override
    public NoteStorageConfiguration getConfiguration() {
        return nsc;
    }

    @Override
    public void rename(String oldPath, String newPath) {

//        try {
//            sardine.move(oldPath, newPath);
//        } catch (IOException ex) {
//            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    @Override
    public void delete(String path) {

//        try {
//            sardine.delete(path);
//        } catch (IOException ex) {
//            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public String getBasePath() {
        return nsc.get("url");
    }

}
