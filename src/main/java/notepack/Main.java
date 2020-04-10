/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notepack;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import notepack.MainViewController;

/**
 *
 * @author lg
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        loader.setResources(ResourceBundle.getBundle("notepack.fonts.FontAwesome"));
        
//        App.class.getResource(fxml + ".fxml");
        
        Parent root = loader.load();

        MainViewController ctrl = loader.getController();
        ctrl.appStart();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setOnShowing((t) -> {
            ctrl.windowRestore();
        });
        
        stage.setTitle("NotePack");

        stage.show();
    }

}
