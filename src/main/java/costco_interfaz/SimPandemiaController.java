package costco_interfaz;

import costco_simulaciones.Caja;
import costco_simulaciones.Cliente;
import costco_simulaciones.SimulacionActualidad;
import costco_simulaciones.SimulacionPandemia;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class SimPandemiaController {

    @FXML Label fila1;
    @FXML Label fila2;
    @FXML Label fila3;
    @FXML Label fila4;
    @FXML Label fila5;
    @FXML Label fila6;
    @FXML Label fila7;
    @FXML Label fila8;
    @FXML Label fila9;
    @FXML Label fila10;
    @FXML Label fila11;
    @FXML Label fila12;

    @FXML Label filaUnica;

    @FXML Circle circulo_0_0;
    @FXML Circle circulo_0_1;
    @FXML Circle circulo_0_2;
    @FXML Circle circulo_1_0;
    @FXML Circle circulo_1_1;
    @FXML Circle circulo_1_2;
    @FXML Circle circulo_2_0;
    @FXML Circle circulo_2_1;
    @FXML Circle circulo_2_2;
    @FXML Circle circulo_3_0;
    @FXML Circle circulo_3_1;
    @FXML Circle circulo_3_2;

    @FXML Label labelTiempo;

    Timeline timeline;
    boolean terminado = false;
    double tiempo;
    SimulacionPandemia s;
    ArrayList<Label> labelsFilas = new ArrayList<>();
    ArrayList<Circle> circleCajeros = new ArrayList<>();

    @FXML
    public void initialize() {
        s = new SimulacionPandemia();
        inicializarArrays();

        actualizacionGUI();
    }

    public void inicializarArrays(){
        labelsFilas.add(fila1);
        labelsFilas.add(fila2);
        labelsFilas.add(fila3);
        labelsFilas.add(fila4);
        labelsFilas.add(fila5);
        labelsFilas.add(fila6);
        labelsFilas.add(fila7);
        labelsFilas.add(fila8);
        labelsFilas.add(fila9);
        labelsFilas.add(fila10);
        labelsFilas.add(fila11);
        labelsFilas.add(fila12);

        circleCajeros.add(circulo_0_2);
        circleCajeros.add(circulo_1_2);
        circleCajeros.add(circulo_2_2);
        circleCajeros.add(circulo_3_2);
        circleCajeros.add(circulo_0_1);
        circleCajeros.add(circulo_1_1);
        circleCajeros.add(circulo_2_1);
        circleCajeros.add(circulo_3_1);
        circleCajeros.add(circulo_0_0);
        circleCajeros.add(circulo_1_0);
        circleCajeros.add(circulo_2_0);
        circleCajeros.add(circulo_3_0);

    }

    private void actualizacionGUI() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    //Termina la simulacion
                    tiempo += 0.1;
                    terminado = s.isTerminado();

                    //LOGICA VISUAL
                    labelTiempo.setText(Double.toString(tiempo));

                    //Cajas GUI
                    estadoCajasGUI();

                    //Cambio visual de los labels
                    estadoLabelsGUI();

                    //Fila unica GUI
                    filaUnica.setText(Integer.toString(s.getFilaUnica().size()));

                    if(terminado){
                        // Detener timeline
                        timeline.stop();

                        Platform.runLater(() -> {
                            // Crear un Stage nuevo para mostrar el resumen
                            Stage stage = new Stage();
                            stage.setTitle("Resumen de Clientes y Cajas - Simulación CON Fila Unica");

                            // ListView con datos
                            ListView<String> listView = new ListView<>();
                            int numCaja = 1;
                            for (Caja c : s.getCajas()) {
                                listView.getItems().add(
                                        "Caja " + numCaja +
                                                " - Clientes Atendidos: " + c.getAtendidos() +
                                                " - Tiempo de Uso: " + c.getTiempoEnUso()
                                );
                                numCaja++;
                            }
                            for (Cliente c : s.getClientes()) {
                                if (c.getTiempoPagando() > 0) {
                                    listView.getItems().add(
                                            "ID: " + c.getNumeroCliente() +
                                                    " - Espera: " + c.getTiempoDeEspera() +
                                                    " - Pago: " + c.getTiempoPagando()
                                    );
                                }
                            }

                            VBox vbox = new VBox(listView);
                            vbox.setPrefSize(500, 300); // Tamaño fijo del VBox
                            vbox.setMinSize(500, 300);
                            vbox.setMaxSize(500, 300);

                            Scene scene = new Scene(vbox, 500, 300); // Tamaño fijo del Stage
                            stage.setScene(scene);

                            stage.setResizable(false);
                            stage.setScene(scene);

                            // Cierra la aplicación si se cierra esta ventana
                            stage.setOnCloseRequest(o -> o.consume());

                            //Posicionamiento manual
                            stage.setX(800);
                            stage.setY(200);
                            // Mostrar la ventana
                            stage.show();
                        });
                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void estadoLabelsGUI(){
        for (int i = 0; i < labelsFilas.size(); i++) {
            labelsFilas.get(i).setText(Integer.toString(s.getFilasCajeros().get(i).size()));
        }
    }

    public void estadoCajasGUI(){
        int i = 0;
        for(Circle c:circleCajeros){
            if(s.getCajas().get(i).getEstadoDeApertura() == Caja.OPEN) {
                c.setStyle("-fx-fill: #c8a2c8;");
            }
            i++;
        }
    }

}
