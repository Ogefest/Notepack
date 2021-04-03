package notepack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import notepack.app.domain.*;
import notepack.app.storage.Filesystem;
import notepack.app.storage.Webdav;
import notepack.app.task.WorkspaceArchivePopup;
import notepack.engine.EngineController;
import notepack.engine.EngineType;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 */
public class WorkspaceCreateController extends PopupController implements Initializable {

    @FXML
    private TextField workspaceName;

    private WorkspaceCreateCallback clbk;
    @FXML
    private Button btnCancel;

    private Workspace workspace;
    @FXML
    private ToggleButton btnUserColor6;
    @FXML
    private ToggleButton btnUserColor5;
    @FXML
    private ToggleButton btnUserColor4;
    @FXML
    private ToggleButton btnUserColor3;
    @FXML
    private ToggleButton btnUserColor2;
    @FXML
    private ToggleButton btnUserColor1;

    private ToggleGroup tg;
    @FXML
    private Button btnSave;
    private TextField webDavUrl;
    private TextField webDavUsername;
    private PasswordField webDavPassword;
    @FXML
    private ComboBox<EngineType> engineSelection;
    @FXML
    private AnchorPane engineForm;

    private EngineController currentFormController;

    @FXML
    private CheckBox encryptionCheckbox;
    @FXML
    private PasswordField encryptionPassword;
    @FXML
    private Button generatePassword;
    @FXML
    private Button copyPasswordBtn;
    @FXML
    private Label passwordLabel;

    @FXML
    private CheckBox utfContentCheckbox;
    @FXML
    private Label contentUtfLabel;
    @FXML
    private Button openArchivedWorkspaceBtn;

    private App app;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tg = new ToggleGroup();
        btnUserColor1.setToggleGroup(tg);
        btnUserColor2.setToggleGroup(tg);
        btnUserColor3.setToggleGroup(tg);
        btnUserColor4.setToggleGroup(tg);
        btnUserColor5.setToggleGroup(tg);
        btnUserColor6.setToggleGroup(tg);

        btnUserColor1.setUserData("#356fcc");
        btnUserColor2.setUserData("#23a7cc");
        btnUserColor3.setUserData("#2db56d");
        btnUserColor4.setUserData("#92c738");
        btnUserColor5.setUserData("#d9c741");
        btnUserColor6.setUserData("#d12121");

        tg.selectToggle(btnUserColor1);
        tg.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                oldVal.setSelected(true);
            }
        });

        encryptionCheckbox.selectedProperty().addListener((o) -> {
            if (encryptionCheckbox.isSelected()) {
                encryptionPassword.setDisable(false);
                generatePassword.setDisable(false);
                passwordLabel.setDisable(false);
            } else {
                encryptionPassword.setDisable(true);
                generatePassword.setDisable(true);
                passwordLabel.setDisable(true);
            }
        });

        engineSelection.getItems().add(new EngineType("Filesystem", "engine/Filesystem.fxml"));
        engineSelection.getItems().add(new EngineType("WebDav", "engine/Webdav.fxml"));

        engineSelection.getSelectionModel().selectedIndexProperty().addListener((ov, t, t1) -> {

            EngineType et = engineSelection.getSelectionModel().getSelectedItem();

            engineForm.getChildren().clear();

            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(et.getFxml()));

            try {
                Parent root = fxmlLoader.load();

                currentFormController = (EngineController) fxmlLoader.getController();

                engineForm.getChildren().add(root);

            } catch (IOException ex) {
                Logger.getLogger(WorkspaceCreateController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

        String systemEncoding = Charset.defaultCharset().displayName();
        if (systemEncoding.equals("UTF-8")) {
            contentUtfLabel.setVisible(false);
            utfContentCheckbox.setVisible(false);
        }

    }

    public void setApp(App app) {
        this.app = app;

        openArchivedWorkspaceBtn.setVisible(false);
        for (Workspace w : app.getAvailableWorkspaces()) {
            if (w.getParam("is_archived").equals("1")) {
                openArchivedWorkspaceBtn.setVisible(true);
                break;
            }
        }

    }

    public void setWorkspaceCreateCallback(WorkspaceCreateCallback clbk) {
        this.clbk = clbk;
    }

    public void setWorkspaceToEdit(Workspace workspace) {
        this.workspace = workspace;

        workspaceName.setText(workspace.getName());

        engineSelection.getItems().clear();

        NoteStorage parentStorage = ((NoteStorageMiddleware) workspace.getStorage()).getParentStorage();

        if (Filesystem.class.isInstance(parentStorage)) {
            engineSelection.getItems().add(new EngineType("Filesystem", "engine/Filesystem.fxml"));
        }
        if (Webdav.class.isInstance(parentStorage)) {
            engineSelection.getItems().add(new EngineType("WebDav", "engine/Webdav.fxml"));
        }
        engineSelection.getSelectionModel().select(0);
        engineSelection.setDisable(true);

        if (workspace.getParam("encryption-enabled").equals("1")) {
            encryptionPassword.setText(workspace.getParam("encryption-password"));
            encryptionCheckbox.setSelected(true);
            copyPasswordBtn.setDisable(false);
        }

        encryptionCheckbox.setDisable(true);
        encryptionPassword.setDisable(true);
        generatePassword.setDisable(true);
        passwordLabel.setDisable(true);

        openArchivedWorkspaceBtn.setVisible(false);

        currentFormController.setStorage(workspace.getStorage());

        String currentWorkspaceColor = workspace.getParam("color");
        if (btnUserColor1.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor1);
        }
        if (btnUserColor2.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor2);
        }
        if (btnUserColor3.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor3);
        }
        if (btnUserColor4.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor4);
        }
        if (btnUserColor5.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor5);
        }
        if (btnUserColor6.getUserData().equals(currentWorkspaceColor)) {
            tg.selectToggle(btnUserColor6);
        }

        String defaultEncoding = workspace.getParam("convert-to-utf8");
        String systemEncoding = Charset.defaultCharset().displayName();
        if (systemEncoding.equals("UTF-8")) {
            utfContentCheckbox.setVisible(false);
            contentUtfLabel.setVisible(false);
            utfContentCheckbox.setSelected(false);
        } else {
            contentUtfLabel.setVisible(true);
            utfContentCheckbox.setVisible(true);

            if (defaultEncoding.equals("1")) {
                utfContentCheckbox.setSelected(true);
            }
        }

        btnSave.setText("Save");
    }

    @FXML
    private void onAdd(ActionEvent event) {

        String name = workspaceName.getText();
        if (name.trim().length() == 0) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Workspace name is required");
            a.show();
            return;
        }

        if (workspace == null) {
            workspace = new Workspace(currentFormController.getStorage(), name);
        } else {
            workspace.getStorage().setConfiguration(currentFormController.getStorage().getConfiguration());
            workspace.setParam("name", name);
        }

        if (tg.getSelectedToggle() == null) {
            Alert a = new Alert(Alert.AlertType.ERROR, "Workspace color is required");
            a.show();
            return;
        }

        if (encryptionCheckbox.isSelected()) {
            workspace.setParam("encryption-enabled", "1");

            String pwd = encryptionPassword.getText();

            if (pwd.length() < 5) {
                Alert a = new Alert(Alert.AlertType.ERROR, "Password must have at least 5 characters");
                a.show();
                return;
            }

            workspace.setParam("encryption-password", pwd);

        } else {
            workspace.setParam("encryption-enabled", "0");
        }

        String color = (String) tg.getSelectedToggle().getUserData();
        workspace.setParam("color", color);
        workspace.setParam("convert-to-utf8", utfContentCheckbox.isSelected() ? "1" : "");

        workspace.registerProcessors();

        clbk.ready(workspace);

        getTaskUtil().closePopup();

    }

    @FXML
    private void onCancel(ActionEvent event) {
        getTaskUtil().closePopup();
    }

    @FXML
    public void generateRandomPassword(ActionEvent actionEvent) {
        String input = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+,./<>?;':[]{}";
        String pwd = "";
        for (int i = 0; i < 20; i++) {
            List<String> result = Arrays.asList(input.split(""));
            Collections.shuffle(result);
            pwd = pwd + result.get(0);
        }
        encryptionPassword.setText(pwd);
        copyPasswordBtn.setDisable(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Copy and save generated password, this is possible only now!");

        alert.showAndWait();
    }

    @FXML
    public void copyPassword(ActionEvent actionEvent) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(encryptionPassword.getText());
        clipboard.setContent(content);
    }


    @FXML
    void onOpenArchivedWorkspace(ActionEvent event) {
        getTaskUtil().closePopup();
        app.addTask(new WorkspaceArchivePopup());
    }

}
