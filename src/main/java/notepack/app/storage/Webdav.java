/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and loadContent the template in the editor.
 */
package notepack.app.storage;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
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

/**
 *
 * @author lg
 */
public class Webdav implements NoteStorage {

    private NoteStorageConfiguration nsc;

    private ArrayList<NoteStorageItem> items = new ArrayList<>();

    private NoteStorageItem rootItem;

    private int deep = 0;

    private Sardine sardine;

//    public Webdav() {
//        nsc = new NoteStorageConfiguration();
//        nsc.set("directory", System.getProperty("java.io.tmpdir"));
//    }
//
//    public Webdav(String path) {
//        nsc = new NoteStorageConfiguration();
//        nsc.set("directory", path);
//    }
    public Webdav(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
        sardine = SardineFactory.begin(nsc.get("username"), nsc.get("password"));
    }

    private String getUrlWithPath(String path) {
        return nsc.get("url") + path;
    }

//    private String getBasePath() {
//        return nsc.get("directory");
//    }
    @Override
    public String loadContent(String path) {

        String result = "";

        try {
            InputStream is = sardine.get(path);
            result = new String(is.readAllBytes());

        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    @Override
    public NoteStorageItem getItemsInStorage() {

        if (rootItem == null) {
//            File f = new File(getBasePath());
            rootItem = new NoteStorageItem(".", "WebDav");
        }

        return rootItem;
    }

    @Override
    public void refreshItemsInStorage() {

        List<DavResource> resources;
        try {
            resources = sardine.list(nsc.get("url"));

            for (DavResource res : resources) {
                NoteStorageItem leaf = new NoteStorageItem(getUrlWithPath(res.getPath()), res.getName(), res.getContentLength(), res.getModified().getTime());
                rootItem.add(leaf);
            }

        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private NoteStorageItem addItems(NoteStorageItem parent, String startPath, int deep) {
        if (deep > 5) {
            return parent;
        }

        ArrayList<String> supportedExtensions = new ArrayList<>();
        supportedExtensions.add("txt");
        supportedExtensions.add("ini");
        supportedExtensions.add("json");
        supportedExtensions.add("xml");
        supportedExtensions.add("md");
        supportedExtensions.add("csv");
        supportedExtensions.add("yaml");
        supportedExtensions.add("log");

        File f = new File(startPath);
        for (String p : f.list()) {
            if (p.substring(0, 1).equals(".")) {
                continue;
            }

            File ff = new File(startPath + File.separator + p);
            long lastModified = 0;
            try {
                FileTime ft = Files.getLastModifiedTime(ff.toPath());
                lastModified = ft.toMillis();
            } catch (IOException ex) {
                Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            String extension = "";

            int i = p.lastIndexOf('.');
            int pos = Math.max(p.lastIndexOf(File.separator), p.lastIndexOf('\\'));

            if (i > pos) {
                extension = p.substring(i + 1);
            }

            if (supportedExtensions.contains(extension)) {

                parent.add(new NoteStorageItem(startPath + File.separator + p, p, ff.length(), lastModified));

            } else if (ff.isDirectory()) {

                NoteStorageItem newDirectoryParent = new NoteStorageItem(startPath + File.separator + p, p, ff.length(), lastModified);

                newDirectoryParent = addItems(newDirectoryParent, startPath + File.separator + p, deep + 1);
                if (newDirectoryParent.get().size() > 0) {
                    parent.add(newDirectoryParent);
                }
            }
        }

//        Collections.sort(parent.get(), (NoteStorageItem o1, NoteStorageItem o2) -> {
//            if (o1.isLeaf()) {
//                return 1;
//            }
//            if (o1.getSize() > o2.getSize()) {
//                return 1;
//            } else if (o1.getSize() == o2.getSize()) {
//                return 0;
//            } else {
//                return -1;
//            }
//        });
        return parent;
    }

    @Override
    public void saveContent(String content, String path) {

        try {
            sardine.put(path, content.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setConfiguration(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
    }

    @Override
    public NoteStorageConfiguration getConfiguration() {
        return nsc;
    }

    @Override
    public void rename(String oldPath, String newPath) {

        try {
            sardine.move(oldPath, newPath);
        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void delete(String path) {

        try {
            sardine.delete(path);
        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
