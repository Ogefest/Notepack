/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and loadContent the template in the editor.
 */
package notepack.app.storage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import notepack.app.domain.Note;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;

/**
 *
 * @author lg
 */
public class Filesystem implements NoteStorage {

    private NoteStorageConfiguration nsc;

    public Filesystem() {
        nsc = new NoteStorageConfiguration();
        nsc.set("directory", System.getProperty("java.io.tmpdir"));
    }

    public Filesystem(String path) {
        nsc = new NoteStorageConfiguration();
        nsc.set("directory", path);
    }

    public Filesystem(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
    }

    private String getBasePath() {
        return nsc.get("directory");
    }

    @Override
    public String loadContent(String path) {

        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(getBasePath() + File.separator + path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    @Override
    public ArrayList<String> list() {

        ArrayList<String> result = new ArrayList<>();

        ArrayList<String> supportedExtensions = new ArrayList<>();
        supportedExtensions.add("txt");
        supportedExtensions.add("ini");
        supportedExtensions.add("json");
        supportedExtensions.add("xml");
        supportedExtensions.add("md");
        supportedExtensions.add("csv");
        supportedExtensions.add("yaml");
        supportedExtensions.add("log");

        File f = new File(getBasePath());
        for (String p : f.list()) {

            String extension = "";

            int i = p.lastIndexOf('.');
            int pos = Math.max(p.lastIndexOf('/'), p.lastIndexOf('\\'));

            if (i > pos) {
                extension = p.substring(i + 1);
            }

            if (supportedExtensions.contains(extension)) {
                result.add(p);
            }
        }

        return result;
    }

    @Override
    public void saveContent(String content, String path) {

        try {
            Files.write(Paths.get(getBasePath() + File.separator + path), content.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Filesystem.class.getName()).log(Level.SEVERE, null, ex);
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

}
