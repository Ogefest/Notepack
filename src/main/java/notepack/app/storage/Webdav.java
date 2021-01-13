package notepack.app.storage;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import notepack.app.domain.exception.MessageError;
import notepack.noterender.Render;
import org.json.JSONObject;
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

    private ArrayList<String> added = new ArrayList<String>();

    HttpClient client;

    private String metadataPath = ".metadata.json";
    private JSONObject meta;

    public Webdav() {
        createHttpClient();
        loadMetaFromStorage();
    }

    public Webdav(NoteStorageConfiguration nsc) {
        this.nsc = nsc;
        createHttpClient();
        loadMetaFromStorage();
    }

    private void createHttpClient() {

        client = HttpClient.newBuilder().authenticator(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        nsc.get("username"),
                        nsc.get("password").toCharArray());
            }
        }).connectTimeout(Duration.ofSeconds(5)).build();

    }

    private String getUrlWithPath(String path) {
        return nsc.get("url") + path;
    }

    @Override
    public byte[] loadContent(String path) throws MessageError {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path)).setHeader("User-Agent", "Notepack WebDAV").GET().build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            return response.body();

        } catch (IOException | InterruptedException ex) {
            throw new MessageError(ex.getMessage(), ex);
        }
    }

    @Override
    public NoteStorageItem getItemsInStorage() {

        if (rootItem == null) {
            rootItem = new NoteStorageItem(nsc.get("url"), "WebDav");
        }

        return rootItem;
    }

    @Override
    public void refreshItemsInStorage() throws MessageError {

        added.clear();

        rootItem = new NoteStorageItem(".", "WebDav");
        rootItem = addItems(rootItem, nsc.get("url"), 0);

    }

    private NoteStorageItem addItems(NoteStorageItem parent, String startPath, int deep) throws MessageError {
        if (deep > 5) {
            return parent;
        }

        String urlHostnameWithSchema = "";
        try {
            URI uri = new URI(startPath);
            urlHostnameWithSchema = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<String> supportedExtensions = Render.getSupportedExtensions();

        try {

            String propfind = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n"
                    + "<D:propfind xmlns:D=\"DAV:\">\n"
                    + "  <D:allprop/>\n"
                    + "</D:propfind>";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(startPath)).setHeader("User-Agent", "Notepack WebDAV").header("Depth", "1").method("PROPFIND", BodyPublishers.ofString(propfind)).build();

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

                boolean isDir = false;
                if (eElement.getElementsByTagNameNS("DAV:", "resourcetype").item(0).getChildNodes().getLength() > 0) {
                    isDir = eElement.getElementsByTagNameNS("DAV:", "resourcetype").item(0).getChildNodes().item(0).getNodeName().contains("collection");
                }

                String path = eElement.getElementsByTagNameNS("DAV:", "href").item(0).getTextContent();
                if (startPath.equals(urlHostnameWithSchema + path)) {
                    continue;
                }

                LocalDateTime lastModified = null;
                Timestamp timestamp = null;
                if (eElement.getElementsByTagNameNS("DAV:", "getlastmodified").getLength() > 0) {
                    DateTimeFormatter df = DateTimeFormatter.RFC_1123_DATE_TIME;

                    lastModified = LocalDateTime.parse(eElement.getElementsByTagNameNS("DAV:", "getlastmodified").item(0).getTextContent(), df);
                    timestamp = Timestamp.valueOf(lastModified);
                }
                String contentType = "";
                if (eElement.getElementsByTagNameNS("DAV:", "getcontenttype").getLength() > 0) {
                    contentType = eElement.getElementsByTagNameNS("DAV:", "getcontenttype").item(0).getTextContent();
                }
                long getcontentlength = 0;
                if (eElement.getElementsByTagNameNS("DAV:", "getcontentlength").getLength() > 0) {
                    String contentLength = eElement.getElementsByTagNameNS("DAV:", "getcontentlength").item(0).getTextContent();
                    if (contentLength.length() > 0) {
                        getcontentlength = Long.parseLong(contentLength);
                    }
                }

                String tmp = URLDecoder.decode(path, "UTF-8");
                String[] segments = tmp.split("/");
                String name = segments[segments.length - 1];

                String extension = "";

                int indexPos = name.lastIndexOf('.');
                int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));

                if (indexPos > pos) {
                    extension = name.substring(indexPos + 1);
                }

                if (supportedExtensions.contains(extension)) {
                    parent.add(new NoteStorageItem(urlHostnameWithSchema + path, name, getcontentlength, timestamp.getTime()));
                } else if (isDir) {

                    NoteStorageItem newDirectoryParent = new NoteStorageItem(urlHostnameWithSchema + path, name, getcontentlength, timestamp.getTime());

                    newDirectoryParent = addItems(newDirectoryParent, urlHostnameWithSchema + path, deep + 1);
                    if (newDirectoryParent.get().size() > 0) {
                        parent.add(newDirectoryParent);
                    }

                }

            }

        } catch (IOException | InterruptedException | ParserConfigurationException | SAXException ex) {
            throw new MessageError(ex.getMessage(), ex);
        }

        return parent;
    }

    @Override
    public void saveContent(byte[] content, String path) throws MessageError {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path)).PUT(BodyPublishers.ofByteArray(content)).build();

            HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
        } catch (IOException | InterruptedException ex) {
            throw new MessageError(ex.getMessage(), ex);
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(oldPath)).header("Destination", newPath).method("MOVE", BodyPublishers.noBody()).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(String path) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(path)).DELETE().build();

            HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getBasePath() {
        return nsc.get("url");
    }

    @Override
    synchronized public void setMeta(JSONObject content, String namespace) throws MessageError {
        meta.put(namespace, content);

        saveMetaToStorage();
    }

    @Override
    public JSONObject getMeta(String namespace) throws MessageError {
        if (meta.has(namespace)) {
            return meta.getJSONObject(namespace);
        }
        return new JSONObject();
    }

    synchronized private void saveMetaToStorage() {
        byte[] bytesToSave = meta.toString().getBytes();

        try {
            saveContent(bytesToSave, getBasePath() + File.separator + metadataPath);
        } catch (MessageError messageError) {
            messageError.printStackTrace();
        }
    }

    private void loadMetaFromStorage() {
        File f = new File(getBasePath() + File.separator + metadataPath);
        if (!f.exists()) {
            meta = new JSONObject();
            return;
        }

        byte[] content = new byte[0];
        try {
            content = loadContent(getBasePath() + File.separator + metadataPath);
            meta = new JSONObject(content);
        } catch (MessageError messageError) {
            meta = new JSONObject();
        }

    }

}
