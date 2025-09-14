package solitaire;

import DeckOfCards.CartaInglesa;
import DeckOfCards.Mazo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class SolitaireVisualGameController {
    // Reemplazar estas variables de instancia
    private SolitaireManager manager;
    private StackPane cartaSeleccionada = null;
    private ArrayList<StackPane> cartasSeleccionadas = new ArrayList<>(); // Para múltiples cartas
    private javafx.scene.Parent contenedorOrigen = null; // Cambiar de VBox a Parent
    private double offsetX, offsetY;
    private Mazo mazo = new Mazo();
    private ArrayList<StackPane> cartasGraficas = new ArrayList<>();
    Pila<StackPane> pilaPozo = new Pila<>();
    Pila<StackPane> pilaDescarte = new Pila<>();

    // Undo
    @FXML Circle undoButton;

    // Foundations
    @FXML VBox f1;
    @FXML VBox f2;
    @FXML VBox f3;
    @FXML VBox f4;

    // Tableaus
    @FXML VBox t1;
    @FXML VBox t2;
    @FXML VBox t3;
    @FXML VBox t4;
    @FXML VBox t5;
    @FXML VBox t6;
    @FXML VBox t7;

    @FXML Button newGame;
    @FXML Button menu;
    // Pila
    @FXML StackPane pozo;

    // Descarte
    @FXML StackPane zonaDescarte;

    private ArrayList<VBox> tableaus = new ArrayList<>();
    private ArrayList<VBox> foundations = new ArrayList<>();

    // Definir tamaño fijo para las cartas
    private final double CARD_WIDTH = 64.0;  // Mismo que en FXML
    private final double CARD_HEIGHT = 94.0; // Mismo que en FXML
    private final double CARD_OFFSET = 1.0; // Desplazamiento para el efecto escalera

    @FXML
    private void initialize() throws IOException {
        pilaPozo = new Pila<>(52);
        pilaDescarte = new Pila<>(52);

        //Agregar Hover Botones.
        hoverBoton(menu);
        hoverBoton(newGame);

        menu.setOnAction(e -> {
            regrearAlMenu();
        });
        newGame.setOnAction(e -> {
            reiniciarJuego();
        });

        // Configurar VBoxes primero
        configurarVBoxes();

        // Generar los StackPane con su respectivo diseño
        generarCartas();

        // Crear arreglos para las referencias de mis VBox
        crearArreglos();

        // Agregar las cartas en forma descendente a mis Tableaus
        agregarCartasAlTableau();

        // Agregar las cartas restantes al pozo
        agregarCartasAlPozo();

        // Configurar eventos drag and drop
        configurarCartasAlTomarYSoltar();
    }

    //Configuro que tanto se espacian las cartas en vertical.
    public void configurarVBoxes() {
        for (VBox vbox : new VBox[]{t1, t2, t3, t4, t5, t6, t7}) {
            vbox.setFillWidth(false);
            vbox.setSpacing(-70); // Espaciado negativo para superponer cartas
        }

        for (VBox vbox : new VBox[]{f1, f2, f3, f4}) {
            vbox.setFillWidth(false);
            vbox.setSpacing(-95);
        }


    }

    //Genero los arreglos para los tableaus y los foundations
    public void crearArreglos() {
        tableaus.add(t1);
        tableaus.add(t2);
        tableaus.add(t3);
        tableaus.add(t4);
        tableaus.add(t5);
        tableaus.add(t6);
        tableaus.add(t7);

        foundations.add(f1);
        foundations.add(f2);
        foundations.add(f3);
        foundations.add(f4);
    }

    //Genero las cartas a partir del mazo de original y les imparto un diseño ya creado.
    public void generarCartas() throws IOException {

        //A partir de la clase mazo genero todas las cartas de la baraja
        for (CartaInglesa carta : mazo.getCartas()) {
            carta.makeFaceUp();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/practica2" +
                    "_solitaire_v2/card.fxml"));
            StackPane cartaStackPane = loader.load();


            //se les da un tamaño fijo a las cartas
            cartaStackPane.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
            cartaStackPane.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
            cartaStackPane.setMinSize(CARD_WIDTH, CARD_HEIGHT);


            StackPane visibleCard = (StackPane) cartaStackPane.lookup("#VisibleCard");
            Label labelTop = (Label) cartaStackPane.lookup("#LabelTop");
            Label labelCenter = (Label) cartaStackPane.lookup("#LabelCenter");
            Label labelBottom = (Label) cartaStackPane.lookup("#LabelBottom");

            // Configuro la carta según sus datos
                labelTop.setText(carta.toString());
                labelTop.setStyle("-fx-text-fill: " + carta.getColor() + "; -fx-font-weight: bold;");


                labelCenter.setText(carta.toString());
                labelCenter.setStyle("-fx-text-fill: " + carta.getColor() + "; -fx-font-weight: bold;");


                labelBottom.setText(carta.getPalo().getFigura());
                labelBottom.setStyle("-fx-text-fill: " + carta.getColor() + "; -fx-font-size: 40; -fx-font-weight: bold;");


                visibleCard.setVisible(false);

            cartaStackPane.setStyle("-fx-background-color: linear-gradient(to bottom, #1a6fc4, #0d4d8c); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: black; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 1;");

            cartasGraficas.add(cartaStackPane);
        }
    }

    //Voltea la carta mientras reestablece ciertas configuraciones de hover segun el caso.
    public void voltearCarta(StackPane cartaStackPane) throws IOException {

        StackPane visibleCard = (StackPane) cartaStackPane.lookup("#VisibleCard");

        cartaStackPane.setOnMouseEntered(null);
        cartaStackPane.setOnMouseExited(null);


        if(!visibleCard.isVisible()) {
            cartaStackPane.setOnMouseEntered(event -> {
                        cartaStackPane.setStyle("-fx-background-color: #f0f0f0;" +
                                "-fx-background-radius: 10;" +
                                "-fx-border-color: #0066cc;" +
                                "-fx-border-radius: 10;" +
                                "-fx-border-width: 2;" +
                                "-fx-effect: dropshadow(gaussian, #0066cc, 10, 0.5, 0, 0);");
                    });
            cartaStackPane.setOnMouseExited(event -> {
                    cartaStackPane.setStyle("-fx-background-color: white;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: black;" +
                            "-fx-border-radius: 10;" +
                            "-fx-border-width: 1;" +
                            "-fx-effect: null;");
            });
            visibleCard.setVisible(true);
            cartaStackPane.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: black;" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: 10;"
            );
        }
        else{
            visibleCard.setVisible(false);
            cartaStackPane.setStyle("-fx-background-color: linear-gradient(to bottom, #1a6fc4, #0d4d8c); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-color: black; " +
                    "-fx-border-radius: 10; " +
                    "-fx-border-width: 1;");
        }
    }


    //Agrega las cartas a cada VBox que representa al Tableau
    public void agregarCartasAlTableau() {
        int cartaIndex = 0;

        for (int i = 1; i <= 7; i++) {
            VBox vbox = tableaus.get(i - 1);

            for (int j = 1; j <= i; j++) {
                StackPane carta = cartasGraficas.get(cartaIndex);
                cartaIndex++;

                vbox.getChildren().add(carta);
                carta.setOpacity(1.0);
                //Genero un margen igual de diferencia entre cartas
                VBox.setMargin(carta, new Insets((j - 1) * CARD_OFFSET, 0, 0, 0));

                //Le doy acciones a la ultima carta, asi como hover
                if (j == i) {
                    StackPane visibleCard = (StackPane) carta.lookup("#VisibleCard");
                    if (visibleCard != null) {
                        visibleCard.setVisible(true);

                    }
                    carta.setStyle("-fx-background-color: white; " +
                            "-fx-background-radius: 10; " +
                            "-fx-border-color: black; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-width: 1;");

                    carta.setOnMouseEntered(event -> {
                        carta.setStyle("-fx-background-color: #f0f0f0; " + // Color más claro
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: #0066cc; " +      // Borde azul
                                "-fx-border-radius: 10; " +
                                "-fx-border-width: 2; " +            // Borde más grueso
                                "-fx-effect: dropshadow(gaussian, #0066cc, 10, 0.5, 0, 0);");
                    });

                    carta.setOnMouseExited(event -> {
                        carta.setStyle("-fx-background-color: white; " +   // Volver al estilo normal
                                "-fx-background-radius: 10; " +
                                "-fx-border-color: black; " +
                                "-fx-border-radius: 10; " +
                                "-fx-border-width: 1; " +
                                "-fx-effect: null;");
                    });
                }
            }
        }
    }



    //Agrego las restantes al pozo y les doy otro diseño para que parezca que estan de espaldas,
    public void agregarCartasAlPozo() {
        HashSet<StackPane> cartasTableau = new HashSet<>();
        for (VBox vbox : tableaus) {
            for (Node node : vbox.getChildren()) {
                if (node instanceof StackPane) {
                    cartasTableau.add((StackPane) node);
                }
            }
        }

        // Limpiar contenedores y pilas
        pozo.getChildren().clear();
        zonaDescarte.getChildren().clear();

        // Limpiar pilas
        while (!pilaPozo.isEmpty()) {
            pilaPozo.pop();
        }
        while (!pilaDescarte.isEmpty()) {
            pilaDescarte.pop();
        }

        // Agregar cartas restantes solo a la pila
        for (StackPane carta : cartasGraficas) {
            if (!cartasTableau.contains(carta)) {
                // Limpiar márgenes anteriores
                VBox.setMargin(carta, Insets.EMPTY);

                // Resetear posición
                carta.setLayoutX(0);
                carta.setLayoutY(0);

                pilaPozo.push(carta);

                // Asegurar que esté boca abajo
                StackPane visibleCard = (StackPane) carta.lookup("#VisibleCard");
                if (visibleCard != null) {
                    visibleCard.setVisible(false);
                }

                // Estilo de carta boca abajo
                carta.setStyle("-fx-background-color: linear-gradient(to bottom, #1a6fc4, #0d4d8c); " +
                        "-fx-background-radius: 10; " +
                        "-fx-border-color: black; " +
                        "-fx-border-radius: 10; " +
                        "-fx-border-width: 1;");

                // Limpiar eventos previos
                carta.setOnMouseEntered(null);
                carta.setOnMouseExited(null);
                carta.setOnMousePressed(null);
                carta.setOnMouseDragged(null);
                carta.setOnMouseReleased(null);
            }
        }

        // Actualizar visualización del pozo
        actualizarVisualizacionPozo();
    }

    //Le agrega hover a la ultima carta del pozo
    private void actualizarVisualizacionPozo() {
        pozo.getChildren().clear();

        if (!pilaPozo.isEmpty()) {
            StackPane cartaSuperior = pilaPozo.peek();
            pozo.getChildren().add(cartaSuperior);

            pozo.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
            pozo.setPrefSize(CARD_WIDTH, CARD_HEIGHT);

            agregarHoverPozo(cartaSuperior);
        }
    }

    //Configura los eventos drag and drop y a su vez configura la visualizacion
    private void actualizarVisualizacionDescarte() {
        zonaDescarte.getChildren().clear();

        if (!pilaDescarte.isEmpty()) {
            StackPane cartaSuperior = pilaDescarte.peek();
            zonaDescarte.getChildren().add(cartaSuperior);

            zonaDescarte.setMaxSize(CARD_WIDTH, CARD_HEIGHT);
            zonaDescarte.setPrefSize(CARD_WIDTH, CARD_HEIGHT);

            configurarEventosCarta(cartaSuperior);
        }
    }



    //En base a si se dejo una carta en un contenedor distinto
    // se vuelven a agregar las propiedades de hover
    public void refreshDraggableCards() {
        for (VBox columna : tableaus) {
            if (!columna.getChildren().isEmpty()) {
                StackPane ultima = (StackPane) columna.getChildren().getLast();
                if (draggableCard(ultima)) {
                    agregarHover(ultima);
                } else {
                    quitarHover(ultima);
                }
            }
        }

    }

    //Da hover a los botones de accion
    public void hoverBoton(Button boton) {
        String estiloBoton = boton.getStyle();
        boton.setOnMouseEntered(event -> {
            boton.setStyle("-fx-background-color: #1a7cd4;");
        });
        boton.setOnMouseExited(event -> {
            boton.setStyle(estiloBoton);
        });
    }




    // Devuelve el diseño original cuando se voltea una carta boca arriba.
    private void restaurarEstiloNormal(StackPane carta) {
        StackPane visibleCard = (StackPane) carta.lookup("#VisibleCard");

        if (visibleCard.isVisible()) {
            carta.setStyle("-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: null;");
        } else {
            carta.setStyle("-fx-background-color: linear-gradient(to bottom, #1a6fc4, #0d4d8c);" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: null;");
        }
    }


    // Indica si se cumplen las condiciones para colocar una carta en el Tableau
    //Se modifico el as para que dejara de tener un valor de 14.
    //Facilita las acciones de comprobacion.
    private boolean puedeColocarEnTableau(CartaInglesa carta, VBox tableau) {
        //Solo las k se colocan en slots vacios.
        if (tableau.getChildren().isEmpty()) {
            return carta.getValor() == 13; // Rey
        }

        //Se obtiene la ultima carta del slot al que se desea ingresar una carta.
        StackPane ultimaCartaPane = (StackPane) tableau.getChildren().get(tableau.getChildren().size() - 1);
        CartaInglesa ultimaCarta = obtenerCartaObjeto(ultimaCartaPane);

        // Debe ser de color opuesto y valor descendente.
        return !carta.getColor().equals(ultimaCarta.getColor()) &&
                carta.getValor() == ultimaCarta.getValor() - 1;
    }




    // Validación para colocar cartas en foundation
    private boolean puedeColocarEnFoundation(CartaInglesa carta, VBox foundation) {

        //Si no hay cartas en el foundation, solo regresara verdadero
        // si la carta tiene valor de 1
        if (foundation.getChildren().isEmpty()) {
            return carta.getValor() == 1;
        }

        StackPane ultimaCartaPane = (StackPane) foundation.getChildren().get(foundation.getChildren().size() - 1);
        CartaInglesa ultimaCarta = obtenerCartaObjeto(ultimaCartaPane);

        // Debe ser del mismo palo y valor ascendente
        return carta.getPalo().equals(ultimaCarta.getPalo()) &&
                carta.getValor() == ultimaCarta.getValor() + 1;
    }

    // Obtiene la carta original al comparar el index del
    // mazo para generar comparaciones
    private CartaInglesa obtenerCartaObjeto(StackPane cartaPane) {
        int index = cartasGraficas.indexOf(cartaPane);
        return mazo.getCartas().get(index);
    }




    // Busca el contenedor al que se ingresara la carta por su posicion en la escena
    private VBox encontrarContenedorDestino(double sceneX, double sceneY) {
        // Verificar tableaus
        for (VBox tableau : tableaus) {
            if (estaDentroDelContenedor(tableau, sceneX, sceneY)) {
                return tableau;
            }
        }

        // Verificar foundations
        for (VBox foundation : foundations) {
            if (estaDentroDelContenedor(foundation, sceneX, sceneY)) {
                return foundation;
            }
        }

        return null;
    }

    // Verifica si las coordenadas están dentro de un contenedor
    private boolean estaDentroDelContenedor(VBox contenedor, double sceneX, double sceneY) {
        return contenedor.getBoundsInParent().contains(
                contenedor.getParent().sceneToLocal(sceneX, sceneY)
        );
    }

    //Agrega hover
    private void agregarHover(StackPane carta) {
        carta.setOnMouseEntered(event -> {
            carta.setStyle("-fx-background-color: #f0f0f0;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: #0066cc;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 2;" +
                    "-fx-effect: dropshadow(gaussian, #0066cc, 10, 0.5, 0, 0);");
        });

        carta.setOnMouseExited(event -> {
            carta.setStyle("-fx-background-color: white;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: black;" +
                    "-fx-border-radius: 10;" +
                    "-fx-border-width: 1;" +
                    "-fx-effect: null;");
        });
    }

    //Elimina el hover
    private void quitarHover(StackPane carta) {
        carta.setOnMouseEntered(null);
        carta.setOnMouseExited(null);

        // También restaurar estilo normal
        carta.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;" +
                "-fx-effect: null;");
    }



    //Indica si una carta puede ser tomada, en base en su visibilidad
    public boolean draggableCard(StackPane card) {
        javafx.scene.Parent padre = card.getParent();
        StackPane visibleCard = (StackPane) card.lookup("#VisibleCard");

        // Si está en un tableau, verificar que esté visible
        if (padre instanceof VBox) {
            VBox columna = (VBox) padre;
            return visibleCard.isVisible() && tableaus.contains(columna);
        }

        // Si está en pozo o descarte, verificar que esté visible
        if (padre instanceof StackPane) {
            return visibleCard.isVisible();
        }

        return false;
    }


    //Permite arrastrar en su totalidad las cartas en base al punto donde se tome
    private ArrayList<StackPane> obtenerCartasEnSecuencia(StackPane cartaInicial) {
        ArrayList<StackPane> secuencia = new ArrayList<>();
        javafx.scene.Parent padre = cartaInicial.getParent();

        // Solo aplica para tableaus
        if (!(padre instanceof VBox) || !tableaus.contains((VBox) padre)) {
            secuencia.add(cartaInicial);
            return secuencia;
        }

        VBox tableau = (VBox) padre;
        int indiceCarta = tableau.getChildren().indexOf(cartaInicial);

        // Obtiene todas las cartas desde la seleccionada hasta la ultima visible
        for (int i = indiceCarta; i < tableau.getChildren().size(); i++) {
            StackPane carta = (StackPane) tableau.getChildren().get(i);
            StackPane visibleCard = (StackPane) carta.lookup("#VisibleCard");

            if (visibleCard.isVisible()) {
                secuencia.add(carta);
            } else {
                break;
            }
        }

        // Validar que la secuencia sea válida
        if (secuencia.size() > 1 && !esSecuenciaValida(secuencia)) {
            secuencia.clear();
            secuencia.add(cartaInicial); // Solo la carta inicial si la secuencia no es válida
        }

        return secuencia;
    }

    // Verifica si una secuencia de cartas es válida
    private boolean esSecuenciaValida(ArrayList<StackPane> secuencia) {
        for (int i = 0; i < secuencia.size() - 1; i++) {
            CartaInglesa cartaActual = obtenerCartaObjeto(secuencia.get(i));
            CartaInglesa cartaSiguiente = obtenerCartaObjeto(secuencia.get(i + 1));

            // Verificar colores alternados y valores descendentes
            if (cartaActual.getColor().equals(cartaSiguiente.getColor()) ||
                    cartaActual.getValor() != cartaSiguiente.getValor() + 1) {
                return false;
            }
        }
        return true;
    }

    // Configura eventos de mouse para una carta específica
    private void configurarEventosCarta(StackPane carta) {
        carta.setOnMousePressed(event -> {
            if (draggableCard(carta)) {
                cartaSeleccionada = carta;
                contenedorOrigen = carta.getParent();

                // Obtener todas las cartas que se pueden mover en secuencia
                cartasSeleccionadas = obtenerCartasEnSecuencia(carta);

                // Guardar offset para un arrastre suave
                offsetX = event.getSceneX() - carta.getLayoutX();
                offsetY = event.getSceneY() - carta.getLayoutY();

                // Efecto visual para todas las cartas seleccionadas
                for (StackPane cartaEnSecuencia : cartasSeleccionadas) {
                    cartaEnSecuencia.setStyle(cartaEnSecuencia.getStyle() +
                            "-fx-effect: dropshadow(gaussian, yellow, 15, 0.8, 0, 0);");
                }

                event.consume();
            }
        });

        carta.setOnMouseDragged(event -> {
            if (cartaSeleccionada == carta && !cartasSeleccionadas.isEmpty()) {
                // Calcular el desplazamiento
                double deltaX = event.getSceneX() - offsetX - carta.getLayoutX();
                double deltaY = event.getSceneY() - offsetY - carta.getLayoutY();

                // Mover todas las cartas en la secuencia
                for (StackPane cartaEnSecuencia : cartasSeleccionadas) {
                    cartaEnSecuencia.setLayoutX(cartaEnSecuencia.getLayoutX() + deltaX);
                    cartaEnSecuencia.setLayoutY(cartaEnSecuencia.getLayoutY() + deltaY);
                }

                // Actualizar offset
                offsetX = event.getSceneX() - carta.getLayoutX();
                offsetY = event.getSceneY() - carta.getLayoutY();

                event.consume();
            }
        });

        carta.setOnMouseReleased(event -> {
            if (cartaSeleccionada == carta) {
                manejarSoltarCarta(event);
                cartaSeleccionada = null;
                cartasSeleccionadas.clear();
                contenedorOrigen = null;
                event.consume();
            }
        });
    }

    //Maneja los eventos que ocurren al soltar una carta
    private void manejarSoltarCarta(javafx.scene.input.MouseEvent event) {
        VBox contenedorDestino = encontrarContenedorDestino(event.getSceneX(), event.getSceneY());

        if (contenedorDestino != null && puedeColocarSecuencia(cartasSeleccionadas, contenedorDestino)) {
            // Mover todas las cartas al nuevo contenedor
            moverSecuenciaDeCartas(cartasSeleccionadas, contenedorOrigen, contenedorDestino);

            // Verificar si se debe voltear una nueva carta
            verificarYVoltearCarta(contenedorOrigen);

            // Actualizar cartas arrastrables
            refreshDraggableCards();

            // Verificar si ganó el juego
            verificarYMostrarVictoria();
        } else {
            // Devolver todas las cartas a su posición original
            devolverCartasAPosicionOriginal();
        }
    }

    //Devuelve las cartas a su respectivo contenedor si no se completo la accion de forma valida
    private void devolverCartasAPosicionOriginal() {
        if (cartasSeleccionadas.isEmpty()) return;

        // Resetear posición visual de todas las cartas seleccionadas
        for (StackPane carta : cartasSeleccionadas) {
            carta.setLayoutX(0);
            carta.setLayoutY(0);
            restaurarEstiloNormal(carta);
        }

        //Si las cartas venían del descarte, devolverlas a la pila de descarte
        if (contenedorOrigen == zonaDescarte) {
            //Solo la primera carta debe volver al descarte (la que estaba visible)
            StackPane cartaDelDescarte = cartasSeleccionadas.get(0);

            //Verificar si la carta ya no está en la pila de descarte
            if (pilaDescarte.isEmpty() || pilaDescarte.peek() != cartaDelDescarte) {
                pilaDescarte.push(cartaDelDescarte);
                actualizarVisualizacionDescarte();
            }
        }

        // Si venían de un tableau, ya están en su contenedor original

    }

    // Valida si se puede colocar una secuencia de cartas
    private boolean puedeColocarSecuencia(ArrayList<StackPane> secuencia, VBox contenedorDestino) {
        if (secuencia.isEmpty()) return false;

        // Solo valida con la primera carta de la secuencia
        StackPane primeraCarta = secuencia.get(0);
        CartaInglesa cartaObjeto = obtenerCartaObjeto(primeraCarta);

        if (tableaus.contains(contenedorDestino)) {
            return puedeColocarEnTableau(cartaObjeto, contenedorDestino);
        } else if (foundations.contains(contenedorDestino)) {

            //Las foundations solo pueden aceptar una carta a la vez
            return secuencia.size() == 1 && puedeColocarEnFoundation(cartaObjeto, contenedorDestino);
        }

        return false;
    }

    // Mover una secuencia completa de cartas
    private void moverSecuenciaDeCartas(ArrayList<StackPane> secuencia, javafx.scene.Parent origen, VBox destino) {
        // Si viene del descarte, remover de la pila
        if (origen instanceof StackPane && origen == zonaDescarte) {
            for (StackPane carta : secuencia) {
                moverCartaDelDescarte(carta);
            }
        }

        // Remueve todas las cartas de la secuencia del contenedor visual
        for (StackPane carta : secuencia) {
            if (origen instanceof VBox) {
                ((VBox) origen).getChildren().remove(carta);
            } else if (origen instanceof StackPane) {
                ((StackPane) origen).getChildren().remove(carta);
            }

            // Resetear posición y transformaciones
            carta.setLayoutX(0);
            carta.setLayoutY(0);
        }

        // Agrega todas las cartas al contenedor destino
        for (int i = 0; i < secuencia.size(); i++) {
            StackPane carta = secuencia.get(i);
            destino.getChildren().add(carta);

            //Configurar márgenes de desplazamiento entre cartas
            if (tableaus.contains(destino)) {
                int posicion = destino.getChildren().size() - 1;
                VBox.setMargin(carta, new Insets(posicion * CARD_OFFSET, 0, 0, 0));
            } else {
                VBox.setMargin(carta, Insets.EMPTY);
            }

            // Restaurar estilo normal y reconfigurar eventos
            restaurarEstiloNormal(carta);
            configurarEventosCarta(carta);
        }
    }

    //Genera acciones de volteado y animaciones para los objetos del contenedor
    private void verificarYVoltearCarta(javafx.scene.Parent contenedor) {
        if (contenedor instanceof VBox && tableaus.contains((VBox) contenedor)) {
            VBox tableau = (VBox) contenedor;
            if (!tableau.getChildren().isEmpty()) {
                StackPane ultimaCarta = (StackPane) tableau.getChildren().get(tableau.getChildren().size() - 1);
                StackPane visibleCard = (StackPane) ultimaCarta.lookup("#VisibleCard");

                if (!visibleCard.isVisible()) {
                    try {
                        voltearCarta(ultimaCarta);
                        agregarHover(ultimaCarta);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    //Permite configurar las acciones del pozo
    @FXML
    private void manejarClicPozo() {
        if (!pilaPozo.isEmpty()) {
            // Determinar cuántas cartas sacar
            int cartasASacar = Math.min(3, pilaPozo.size());

            // Sacar cartas del pozo y pasarlas al descarte
            for (int i = 0; i < cartasASacar; i++) {
                if (!pilaPozo.isEmpty()) {
                    StackPane carta = pilaPozo.pop();

                    // Voltear la carta (hacerla visible)
                    try {
                        voltearCarta(carta);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Agregar a la pila de descarte
                    pilaDescarte.push(carta);
                }
            }

            // Actualizar visualizaciones
            actualizarVisualizacionPozo();
            actualizarVisualizacionDescarte();

        } else {
            // Si el pozo está vacío, reciclar desde descarte
            reciclarDescarte();
        }
    }

    // Reciclar cartas del descarte al pozo
    private void reciclarDescarte() {
        if (pilaDescarte.isEmpty()) {
            return;
        }

        //Mover todas las cartas del descarte al pozo en orden inverso
        while (!pilaDescarte.isEmpty()) {
            StackPane carta = pilaDescarte.pop();

            // Voltear la carta
            try {
                StackPane visibleCard = (StackPane) carta.lookup("#VisibleCard");
                if (visibleCard != null && visibleCard.isVisible()) {
                    voltearCarta(carta);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Limpiar eventos de drag and drop
            carta.setOnMousePressed(null);
            carta.setOnMouseDragged(null);
            carta.setOnMouseReleased(null);
            carta.setOnMouseEntered(null);
            carta.setOnMouseExited(null);

            // Resetear posición
            carta.setLayoutX(0);
            carta.setLayoutY(0);

            // Agregar al pozo
            pilaPozo.push(carta);
        }

        // Actualizar visualizaciones
        actualizarVisualizacionPozo();
        actualizarVisualizacionDescarte();
    }

    public void moverCartaDelDescarte(StackPane carta) {
        // Buscar y remover la carta de la pila de descarte
        if (!pilaDescarte.isEmpty() && pilaDescarte.peek() == carta) {
            pilaDescarte.pop();
            actualizarVisualizacionDescarte();
        }
    }


    // Hover específico para las cartas del pozo
    private void agregarHoverPozo(StackPane carta) {
        carta.setOnMouseEntered(event -> {
            // Solo aplicar hover si sigue siendo la carta superior del pozo
            if (!pilaPozo.isEmpty() && pilaPozo.peek() == carta) {
                carta.setStyle("-fx-background-color: #1a7cd4;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #0066cc;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, #0066cc, 10, 0.5, 0, 0);");
            }
        });

        carta.setOnMouseExited(event -> {
            if (!pilaPozo.isEmpty() && pilaPozo.peek() == carta) {
                carta.setStyle("-fx-background-color: linear-gradient(to bottom, #1a6fc4, #0d4d8c);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: black;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: null;");
            }
        });
    }


    public boolean verificarVictoria() {
        // El juego se gana cuando todas las foundations tienen 13 cartas
        for (VBox foundation : foundations) {
            if (foundation.getChildren().size() != 13) {
                return false;
            }
        }
        return true;
    }


    //Mensaje que te da la opcion de volver a jugar o no (se regresara al menu al hacer click en no)
    private void mostrarMensajeVictoria() {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION);
        alert.setTitle("Felicitaciones!");
        alert.setHeaderText("Has ganado!");
        alert.setContentText("Completaste Solitario!\n¿Quieres jugar otra vez?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                reiniciarJuego();
            }
            if (response == ButtonType.NO) {
                regrearAlMenu();
            }
        });
    }

    //Cambia la escena al menu principal
    public void regrearAlMenu(){
        manager = new SolitaireManager((Stage) t1.getScene().getWindow());
        try {
            manager.iniciarEscenaMenu();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    //Sirve para verificar victoria y colocar el mensaje de ganador
    private void verificarYMostrarVictoria() {
        if (verificarVictoria()) {
            mostrarMensajeVictoria();
        }
    }

    //Permite reiniciar el juego al terminar o al hacer click en el boton
    private void reiniciarJuego() {
        try {
            // Limpiar todos los contenedores
            for (VBox tableau : tableaus) {
                tableau.getChildren().clear();
            }
            for (VBox foundation : foundations) {
                foundation.getChildren().clear();
            }
            pozo.getChildren().clear();
            zonaDescarte.getChildren().clear();

            // Limpiar pilas
            while (!pilaPozo.isEmpty()) {
                pilaPozo.pop();
            }
            while (!pilaDescarte.isEmpty()) {
                pilaDescarte.pop();
            }

            // Reinicializar el juego
            mazo = new Mazo();
            cartasGraficas.clear();

            generarCartas();
            agregarCartasAlTableau();
            agregarCartasAlPozo();
            configurarCartasAlTomarYSoltar();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Da la configuracion de eventos disponibles para cada carta
    //que se toma y se coloca en otra parte
    public void configurarCartasAlTomarYSoltar() {
        for (VBox tableau : tableaus) {
            for (Node node : tableau.getChildren()) {
                if (node instanceof StackPane) {
                    StackPane carta = (StackPane) node;
                    configurarEventosCarta(carta);
                }
            }
        }

        if (!pozo.getChildren().isEmpty()) {
            StackPane ultimaCarta = (StackPane) pozo.getChildren().get(pozo.getChildren().size() - 1);
            configurarEventosCarta(ultimaCarta);
        }
    }

}