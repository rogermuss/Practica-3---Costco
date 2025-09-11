package solitaire;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Solitaire extends Application {
    int x = 1;

    //Codigo que actua como el sistema que da inicio al codigo mediante el inicio de la escena menu
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/practica2" +
                "_solitaire_v2/SolitaireMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setFullScreen(false);
        stage.setTitle("Solitario - Bienvenida");
        stage.setScene(scene);
        stage.show();
        stage.resizableProperty().setValue(Boolean.FALSE);

        SolitaireManager manager = new SolitaireManager(stage);

        // Pasar el manager al controlador de bienvenida
        SolitaireMenuController controller = fxmlLoader.getController();
        controller.setManager(manager);
    }



    //MAIN
    public static void main(String[] args) {
        launch(args);
    }
}
