package costco_simulaciones;


import cola_logica.ColaCircular;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.JFXPanel;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class SimulacionPandemia {

    //Hilos de la simulacion
    private Thread hiloEntrada;
    private Thread[] hiloCaja = new Thread[12];
    private Thread hiloCambioDeEstado;
    private Thread hiloAperturaCajas;
    private Timeline timeline = new Timeline();

    public final double DURACION_SIMULACION = 600.0; //Son 600 segundos
    private boolean terminado = false;
    private double tiempoSimulacion = 0.0;

    private int contCliente = 1;
    private int contadorCajasAbiertas = 4;

    private ArrayList<Caja> cajas = new ArrayList<>(12);
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<ColaCircular<Cliente>> filasCajeros = new ArrayList<>(12);
    private ColaCircular<Cliente> filaUnica;

    public SimulacionPandemia() {
        iniciarReloj();

        //Se hace setup a las condiciones iniciales del programa
        configuracionInicial();

        //Establezco hilo de entrada de clientes.
        hiloEntrada();

        //Cambia el estado entre fila unica a fila de cajeros y entradas al cajero
        hiloCambioDeEstado();

        //Verifica el estado de las filas para abrir las cajas
        hiloEstadoCajas();

        //Inicializa los hilos que verifican el estado de la caja para simular los pagos
        iniciarHilosCajas();
    }

    public void configuracionInicial(){
        for(int i = 0; i < 12; i++){
            filasCajeros.add(new ColaCircular<Cliente>(4));
        }

        for(int i = 0; i < 12; i++){
            cajas.add(new Caja());
            cajas.get(i).setFilaCaja(filasCajeros.get(i));
            if(i<4){
                cajas.get(i).setEstadoDeApertura(Caja.OPEN);
                cajas.get(i).setTiempoDeApertura(tiempoSimulacion);
            }
        }

        filaUnica = new ColaCircular<Cliente>(1200);

    }







    private void iniciarReloj() {

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    tiempoSimulacion += 0.1;
                    //Termina la simulacion
                    terminado = tiempoSimulacion >= DURACION_SIMULACION;
                    //Tiempo Transcurrido
                    System.out.println(tiempoSimulacion);

                    //Si se termina la simulacion, agregar tiempo a las cajas
                    if(terminado){
                        // Detener timeline
                        timeline.stop();

                        // Interrumpir todos los hilos para que terminen limpiamente
                        if(hiloEntrada != null) hiloEntrada.interrupt();
                        if(hiloCambioDeEstado != null) hiloCambioDeEstado.interrupt();
                        if(hiloAperturaCajas != null) hiloAperturaCajas.interrupt();

                        for(Thread hilo : hiloCaja){
                            if(hilo != null) hilo.interrupt();
                        }
                        for (int i = 0; i < cajas.size(); i++){
                            //Tambien calcula el tiempo de uso
                            cajas.get(i).setTiempoFinal(tiempoSimulacion);
                        }
                        System.out.println("Terminado\n\n");
                        System.out.println("Tiempo de Simulacion "+tiempoSimulacion+"\n\n");
                        for(int i = 0; i < cajas.size(); i++){
                            System.out.println("Caja "+i+
                                    ": Tiempo Abierta: " + cajas.get(i).getTiempoEnUso()+
                                    ": Clientes Atendidos: "+cajas.get(i).getAtendidos() + "\n");

                        }
                        for(int i = 0; i < clientes.size(); i++){
                            System.out.println("\n ID: "+clientes.get(i).getNumeroCliente()
                                    + "\nTiempo de Espera: "+clientes.get(i).getTiempoDeEspera()
                                    + "\nTiempo Pagando: "+clientes.get(i).getTiempoPagando()+"\n\n");
                        }

                    }
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    //HILO QUE SIMULA EL PROCESO DE PAGO DE CADA CAJA
    public void iniciarHilosCajas() {
        for (int i = 0; i < cajas.size(); i++) {
            int index = i; // lambda necesita variable final
            hiloCaja[index] = new Thread(() -> {
                Random rand = new Random();
                while (!terminado) {
                    // Revisar si hay clientes en la fila de esta caja
                    if (!filasCajeros.get(index).isEmpty() && cajas.get(index).getEstadoDeApertura() == Caja.OPEN) {
                        Cliente cliente = filasCajeros.get(index).pop();
                        cliente.setTiempoInicioPago(tiempoSimulacion);
                        cajas.get(index).setCliente(cliente);
                        cajas.get(index).setEstadoUso(Caja.OCCUPIED);

                        // CAMBIO: En lugar de Thread.sleep(), usar tiempo de simulación
                        double tiempoPago = rand.nextDouble() * 2 + 3; // 3-5 segundos simulados
                        double tiempoFinPago = tiempoSimulacion + tiempoPago;

                        // Esperar hasta que pase el tiempo de simulación
                        while (!terminado && tiempoSimulacion < tiempoFinPago) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                return;
                            }
                        }

                        // Solo procesar si no fue interrumpido
                        if (!terminado) {
                            cliente.setTiempoFinalPago(tiempoSimulacion);
                            cajas.get(index).aumentarAtendidos();
                            cajas.get(index).setEstadoUso(Caja.EMPTY);
                        }

                    } else {
                        try {
                            Thread.sleep(50); // espera si no hay clientes
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            });
            hiloCaja[index].start(); // arranca el hilo
        }
    }


    //HILO DE ENTRADA DE CLIENTE
    //HILO DE ENTRADA DE CLIENTE
    public void hiloEntrada() {
        Random rand = new Random();
        hiloEntrada = new Thread(() -> {
            while (!terminado) {
                // intervalo de llegada entre 0.5 y 1.0 segundos simulados
                double intervalo = rand.nextDouble() * 0.5 + 0.5;
                double proximaLlegada = tiempoSimulacion + intervalo;

                // esperar hasta que llegue el tiempo de simulacion
                while (!terminado && tiempoSimulacion < proximaLlegada) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        return;
                    }
                }

                if (!terminado) {
                    //Inicializo al Cliente y le doy la hora de llegada
                    Cliente cliente = new Cliente(contCliente);
                    cliente.setTiempoLlegada(tiempoSimulacion);

                    //Meto un cliente, aumento contador
                    clientes.add(cliente);
                    filaUnica.push(cliente);
                    contCliente++;
                }
            }
        });
        hiloEntrada.start();
    }



    //HILO DE CAMBIO ENTRE LA FILA UNICA Y LAS FILAS DE CADA CAJA
    public void hiloCambioDeEstado() {
        hiloCambioDeEstado = new Thread(() -> {

            while (!terminado) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (!filaUnica.isEmpty() && cajaAbiertaMenor() != -1) {
                    int indexFilaMenor = cajaAbiertaMenor();
                    Cliente cliente = filaUnica.pop();
                    cliente.setTiempoInicioPago(tiempoSimulacion); // marca inicio de pago
                    filasCajeros.get(indexFilaMenor).push(cliente);
                }

            }
        });
        hiloCambioDeEstado.start();

    }

    public int cajaAbiertaMenor() {
        int indexMenor = -1;
        int menor = Integer.MAX_VALUE;

        for (int i = 0; i < cajas.size(); i++) {
            // Solo considerar cajas abiertas y con espacio
            if (cajas.get(i).getEstadoDeApertura() == Caja.OPEN && !filasCajeros.get(i).isFull()) {
                int sizeFila = filasCajeros.get(i).size();
                if (sizeFila < menor) {
                    menor = sizeFila;
                    indexMenor = i;
                }
            }
        }

        return indexMenor;
    }


    //Abre la caja si estan llenas las filas
    public void hiloEstadoCajas() {
        hiloAperturaCajas = new Thread(() -> {
            while (!terminado) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int cont = 0;
                for(int i = 0; i < contadorCajasAbiertas; i++){
                    if(filasCajeros.get(i).isFull()){
                        cont++;
                    }
                }
                if(cont == contadorCajasAbiertas && contadorCajasAbiertas < 12){
                    cajas.get(contadorCajasAbiertas).setEstadoDeApertura(Caja.OPEN);
                    cajas.get(contadorCajasAbiertas).setTiempoDeApertura(tiempoSimulacion);
                    contadorCajasAbiertas++;
                }
            }
        });
        hiloAperturaCajas.start();

    }

    public ArrayList<Caja> getCajas() {
        return cajas;
    }

    public double getTiempoSimulacion() {
        return tiempoSimulacion;
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public ColaCircular<Cliente> getFilaUnica() {
        return filaUnica;
    }

    public ArrayList<ColaCircular<Cliente>> getFilasCajeros() {
        return filasCajeros;
    }

    public boolean isTerminado() {
        return terminado;
    }


    public static  void main(String[] args) {
        new JFXPanel();
        SimulacionPandemia s = new SimulacionPandemia();

    }
}
