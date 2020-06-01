package notepack.app.storage;

//import com.github.sardine.DavResource;
//import com.github.sardine.Sardine;
//import com.github.sardine.SardineFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;

public class Webdav implements NoteStorage {

    private NoteStorageConfiguration nsc;

    private ArrayList<NoteStorageItem> items = new ArrayList<>();

    private NoteStorageItem rootItem;

    private int deep = 0;

//    private Sardine sardine;
    private ArrayList<String> added = new ArrayList<String>();

    HttpClient client;

    public Webdav() {
        createHttpClient();
    }

    public Webdav(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
        createHttpClient();
    }

    private void createHttpClient() {

        client = HttpClient.newBuilder().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        nsc.get("username"),
                        nsc.get("password").toCharArray());
            }
        }).build();

    }

    private String getUrlWithPath(String path) {
        return nsc.get("url") + path;
    }

    @Override
    public String loadContent(String path) {

        String result = "";
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        rootItem = addItems(rootItem, nsc.get("url"), 0);

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

//        List<DavResource> resources;
        try {
//            resources = sardine.list(startPath);

            String propfind = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                    + "<D:propfind xmlns:D=\"DAV:\">\n"
                    + "  <D:allprop/>\n"
                    + "</D:propfind>";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(startPath)).header("Depth", "1").method("PROPFIND", BodyPublishers.ofString(propfind)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String xmlResult = response.body();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlResult)));
            String ns = doc.getNamespaceURI();
            
            doc.getDocumentElement().normalize();
            
            NodeList nList = doc.getElementsByTagNameNS("DAV:", "response");
            if (nList.getLength() == 0) {
                return parent;
            }
            
            for (int i = 0; i < nList.getLength(); i++) {
                
                
                Node nNode = nList.item(i);
                
                Element eElement = (Element) nNode;
                String path = eElement.getElementsByTagNameNS("DAV:", "href").item(0).getTextContent();

                String lastModified = "";
                if (eElement.getElementsByTagNameNS("DAV:", "getlastmodified").getLength() > 0) {
                    lastModified = eElement.getElementsByTagNameNS("DAV:", "getlastmodified").item(0).getTextContent();
                }
                String contentType = "";
                if (eElement.getElementsByTagNameNS("DAV:", "getcontenttype").getLength() > 0) {
                    contentType = eElement.getElementsByTagNameNS("DAV:", "getcontenttype").item(0).getTextContent();
                }
                String getcontentlength = "";
                if (eElement.getElementsByTagNameNS("DAV:", "getcontentlength").getLength() > 0) {
                    getcontentlength = eElement.getElementsByTagNameNS("DAV:", "getcontentlength").item(0).getTextContent();
                }
                
                
                System.out.println("\nCurrent Element :" + eElement.getElementsByTagName("D:href").item(0).getTextContent());
                
                
            }
            

//            DOMParser parser = new DOMParser();
//            if (resources.size() == 0) {
//                return parent;
//            }
//            String hostnameWithSchema = startPath.replace(resources.get(0).getPath(), "");
//            resources.remove(0);
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
        } catch (IOException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

        return parent;
    }

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
        this.nsc = nsc;
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
