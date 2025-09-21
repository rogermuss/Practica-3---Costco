package cola_interfaz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/practica3_costco" +
                "/interfaz.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setFullScreen(false);
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setResizable(false);
        stage.setTitle("Practica 3 costco");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
