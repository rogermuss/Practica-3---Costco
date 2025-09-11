package solitaire;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;


public class SolitaireMenuController {

    @FXML
    private StackPane playCard;

    @FXML
    private StackPane exitCard;

    SolitaireManager manager;


    @FXML
    private void initialize() {

        setupHoverEffects();

        setupClickEvents();

        // Muestra el label jugar y salir respectivamente
        playCard.getChildren().get(3).setVisible(false);
        exitCard.getChildren().get(3).setVisible(false);
    }

    //Ayuda a gestionar los stage
    public void setManager(SolitaireManager manager) {
        this.manager = manager;
    }


    //Genera los click events de los botones del menu.
    public void setupClickEvents() {
        playCard.setOnMouseClicked(e -> {
            try {
                manager.iniciarEscenaJuego();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        exitCard.setOnMouseClicked(event -> {
            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Salir");
            alerta.setHeaderText("¿Estás seguro de que quieres salir?");
            alerta.setContentText("Se cerrará la aplicación.");

            if (alerta.showAndWait().get() == ButtonType.OK) {
                System.exit(0);
            }
        });
    }




    //Doy aspecto grafico de animacion al ingresar el mouse en el area de los
    // stack de opciones y los regreso al diseño original al salir.
    private void setupHoverEffects() {
        // Efecto hover para la carta de jugar
        playCard.setOnMouseEntered(e -> {
            ScaleTransition escala = new ScaleTransition(Duration.millis(200), playCard);
            escala.setToX(1.1);
            escala.setToY(1.1);
            escala.play();
            playCard.setTranslateY(-15);
            // Mostrar etiqueta
            if (playCard.getChildren().size() > 3) {
                playCard.getChildren().get(3).setVisible(true);
            }
        });

        playCard.setOnMouseExited(e -> {
            ScaleTransition escala = new ScaleTransition(Duration.millis(200), playCard);
            escala.setToX(1.0);
            escala.setToY(1.0);
            escala.play();
            playCard.setTranslateY(0);
            // Ocultar etiqueta
            if (playCard.getChildren().size() > 3) {
                playCard.getChildren().get(3).setVisible(false);
            }
        });

        // Efecto hover para la carta de salir
        exitCard.setOnMouseEntered(e -> {
            ScaleTransition escala = new ScaleTransition(Duration.millis(200), exitCard);
            escala.setToX(1.1);
            escala.setToY(1.1);
            escala.play();
            exitCard.setTranslateY(-15);
            // Mostrar etiqueta
            if (exitCard.getChildren().size() > 3) {
                exitCard.getChildren().get(3).setVisible(true);
            }
        });

        exitCard.setOnMouseExited(e -> {
            ScaleTransition escala = new ScaleTransition(Duration.millis(200), exitCard);
            escala.setToX(1.0);
            escala.setToY(1.0);
            escala.play();
            exitCard.setTranslateY(0);
            // Ocultar etiqueta
            if (exitCard.getChildren().size() > 3) {
                exitCard.getChildren().get(3).setVisible(false);
            }
        });
    }


}