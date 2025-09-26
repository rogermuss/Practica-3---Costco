package costco_interfaz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class InterfazController {

    @FXML
    private AnchorPane padre;

    @FXML
    private SplitPane mainSplit;

    @FXML
    private AnchorPane panelSuperior;

    @FXML
    private AnchorPane panelInferior;

    // Referencias a los controladores de cada panel
    private SimPandemiaController panelSuperiorController;
    private SimActualidadController panelInferiorController;

    @FXML
    private void initialize() {
        configurarSplitPane();
        cargarPaneles();
    }

    private void configurarSplitPane() {
        // Hacer que el divisor no se pueda mover
        mainSplit.getDividers().forEach(divider -> {
            divider.positionProperty().addListener((obs, oldPos, newPos) -> {
                divider.setPosition(0.5); // Mantener 50/50
            });
        });
    }

    private void cargarPaneles() {
        cargarPanelSuperior();
        cargarPanelInferior();
    }

    private void cargarPanelSuperior() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/practica3_costco"
                    + "/simulacionPandemia.fxml"));
            AnchorPane contenidoSuperior = loader.load();
            panelSuperiorController = loader.getController();

            // Hacer que el contenido llene todo el panel
            AnchorPane.setTopAnchor(contenidoSuperior, 0.0);
            AnchorPane.setBottomAnchor(contenidoSuperior, 0.0);
            AnchorPane.setLeftAnchor(contenidoSuperior, 0.0);
            AnchorPane.setRightAnchor(contenidoSuperior, 0.0);

            panelSuperior.getChildren().add(contenidoSuperior);

        } catch (IOException e) {
            System.err.println("Error cargando panel superior: " + e.getMessage());
        }
    }

    private void cargarPanelInferior() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/practica3_costco"
                    +"/simulacionActualidad.fxml"));
            AnchorPane contenidoInferior = loader.load();
            panelInferiorController = loader.getController();

            // Hacer que el contenido llene todo el panel
            AnchorPane.setTopAnchor(contenidoInferior, 0.0);
            AnchorPane.setBottomAnchor(contenidoInferior, 0.0);
            AnchorPane.setLeftAnchor(contenidoInferior, 0.0);
            AnchorPane.setRightAnchor(contenidoInferior, 0.0);

            panelInferior.getChildren().add(contenidoInferior);

        } catch (IOException e) {
            System.err.println("Error cargando panel inferior: " + e.getMessage());
        }
    }

    // Métodos públicos para acceder a los controladores desde tu código de simulación
    public SimPandemiaController getPanelSuperiorController() {
        return panelSuperiorController;
    }

    public SimActualidadController getPanelInferiorController() {
        return panelInferiorController;
    }
}