package notepack.app.storage;

import notepack.app.domain.NoteStorage;
import notepack.app.domain.NoteStorageConfiguration;
import notepack.app.domain.NoteStorageItem;
import notepack.app.domain.exception.MessageError;
import notepack.noterender.Render;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.HttpPropfind;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        ArrayList<String> supportedExtensions = Render.getSupportedExtensions();
        try {
            URI uri = new URI(startPath);
            int port = uri.getPort();
            if (port < 0) {
                port = 80;
            }
            urlHostnameWithSchema = uri.getScheme() + "://" + uri.getHost() + ":" + port;

            CloseableHttpClient httpClient = HttpClients.createDefault();

            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(nsc.get("username"), nsc.get("password")));

            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credentialsProvider);

            HttpPropfind httpPropfind = new HttpPropfind(uri, DavConstants.PROPFIND_ALL_PROP_INCLUDE, DavConstants.DEPTH_1);
            try (CloseableHttpResponse response = httpClient.execute(httpPropfind, context)) {
                MultiStatus multiStatus = httpPropfind.getResponseBodyAsMultiStatus(response);

                for (MultiStatusResponse resp : multiStatus.getResponses()) {

                    DavPropertySet props = resp.getProperties(200);

                    long length = 0;
                    Object tmplength = props.get(DavPropertyName.GETCONTENTLENGTH);
                    if (tmplength != null) {
                        length = Long.parseLong((String) props.get(DavPropertyName.GETCONTENTLENGTH).getValue());
                    }

                    LocalDateTime lastModified = null;
                    Timestamp timestamp = null;
                    Object tmplastmodified = props.get(DavPropertyName.GETLASTMODIFIED);
                    if (tmplastmodified != null) {
                        DateTimeFormatter df = DateTimeFormatter.RFC_1123_DATE_TIME;

                        lastModified = LocalDateTime.parse((String) props.get(DavPropertyName.GETLASTMODIFIED).getValue(), df);
                        timestamp = Timestamp.valueOf(lastModified);
                    } else {
                        timestamp = Timestamp.valueOf(LocalDateTime.now());
                    }

                    String extension = "";
                    String path = resp.getHref();
                    String tmp = URLDecoder.decode(path, StandardCharsets.UTF_8);
                    String[] segments = tmp.split("/");
                    String name = segments[segments.length - 1];

                    int indexPos = name.lastIndexOf('.');
                    int pos = Math.max(name.lastIndexOf(File.separator), name.lastIndexOf('\\'));

                    if (indexPos > pos) {
                        extension = name.substring(indexPos + 1);
                    }

                    if (supportedExtensions.contains(extension)) {
                        parent.add(new NoteStorageItem(urlHostnameWithSchema + path, name, length, timestamp.getTime()));
                    }

                    DavProperty<?> iscollection = props.get(DavPropertyName.RESOURCETYPE);
                    if (iscollection != null) {
                        if (iscollection.getValue() != null && !uri.getPath().equals(path)) {
                            NoteStorageItem newDirectoryParent = new NoteStorageItem(urlHostnameWithSchema + path, name, length, timestamp.getTime());

                            newDirectoryParent = addItems(newDirectoryParent, urlHostnameWithSchema + path, deep + 1);
                            if (newDirectoryParent.get().size() > 0) {
                                parent.add(newDirectoryParent);
                            }
                        }
                    }

                }

            }


        } catch (URISyntaxException | IOException | DavException ex) {
            Logger.getLogger(Webdav.class.getName()).log(Level.SEVERE, null, ex);
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
        loadMetaFromStorage();
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
    synchronized public void setMeta(JSONObject content, String namespace) {
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

    @Override
    public void deleteMeta(String noteIdent) throws MessageError {
        if (meta.has(noteIdent)) {
            meta.remove(noteIdent);
            saveMetaToStorage();
        }
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

        byte[] content;
        try {
            content = loadContent(getBasePath() + File.separator + metadataPath);
            meta = new JSONObject(content);
        } catch (MessageError messageError) {
            meta = new JSONObject();
        }

    }

}
