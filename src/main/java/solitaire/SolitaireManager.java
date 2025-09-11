package solitaire;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

//Cambia entre escenas
public class SolitaireManager {
    private Stage stage;



    SolitaireManager(Stage stage) {
        this.stage = stage;
    }

    //Carga la escena creada para el menu
    public void iniciarEscenaMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/practica2" +
                "_solitaire_v2/SolitaireMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        SolitaireMenuController controller = fxmlLoader.getController();
        controller.setManager(this); // <--- esto es clave
        stage.setFullScreen(false);
        stage.setTitle("Solitario - Bienvenida");
        stage.setScene(scene);
        stage.show();
    }

    //Carga la escena del juego
    public void iniciarEscenaJuego() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/practica2" +
                "_solitaire_v2/SolitaireGame.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setFullScreen(false);
        stage.setTitle("Solitario - Juego");
        stage.setScene(scene);
        stage.show();
    }
}
