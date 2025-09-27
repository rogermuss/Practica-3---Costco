package costco_simulaciones;


import cola_logica.ColaCircular;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.JFXPanel;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class SimulacionActualidad {

    //Hilos de la simulacion
    private Thread hiloEntrada;
    private Thread[] hiloCaja = new Thread[12];
    private Thread hiloCambioDeEstado;
    private Thread hiloAperturaCajas;
    private Timeline timeline = new Timeline();

    public final double DURACION_SIMULACION = 600.0; //Son 600 segundos
    private boolean terminado = false;
    private double tiempoSimulacion = 0.0;
    private Cliente clienteTemporal;

    private int contCliente = 1;
    private int contadorCajasAbiertas = 4;

    private ArrayList<Caja> cajas = new ArrayList<>(12);
    private ArrayList<Cliente> clientes = new ArrayList<>();
    private ArrayList<ColaCircular<Cliente>> filasCajeros = new ArrayList<>(12);

    public SimulacionActualidad() {
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
            filasCajeros.add(new ColaCircular<Cliente>(1000));
        }

        for(int i = 0; i < 12; i++){
            cajas.add(new Caja());
            cajas.get(i).setFilaCaja(filasCajeros.get(i));
            if(i<4){
                cajas.get(i).setEstadoDeApertura(Caja.OPEN);
                cajas.get(i).setTiempoDeApertura(tiempoSimulacion);
            }
        }


    }







    private void iniciarReloj() {

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {
                    tiempoSimulacion += 0.1;
                    //Termina la simulacion
                    terminado = tiempoSimulacion >= DURACION_SIMULACION;
                    //Tiempo Transcurrido

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
                                Thread.sleep(1);
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
                            Thread.sleep(1); // espera si no hay clientes
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
                        Thread.sleep(1);
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
                    clienteTemporal = cliente;
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
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (clienteTemporal != null) {
                    int indexFilaMenor = cajaAbiertaMenor();
                    filasCajeros.get(indexFilaMenor).push(clienteTemporal);
                    clienteTemporal = null;
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


    //Abre y cierra las cajas segun las filas
    public void hiloEstadoCajas() {
        // Array para llevar control del tiempo que cada caja ha estado vacía
        double[] tiempoVaciaDesde = new double[12];
        for(int i = 0; i < 12; i++) {
            tiempoVaciaDesde[i] = -1; // -1 significa que no está vacía o no se ha registrado
        }

        hiloAperturaCajas = new Thread(() -> {
            while (!terminado) {
                try {
                    Thread.sleep(50); // revisar cada 50ms
                } catch (InterruptedException e) {
                    return;
                }

                int abiertas = 0;
                int llenas = 0;

                // contar cajas abiertas y cuantas estan llenas
                for (int i = 0; i < cajas.size(); i++) {
                    if (cajas.get(i).getEstadoDeApertura() == Caja.OPEN) {
                        abiertas++;
                        if (filasCajeros.get(i).size() > 4) {
                            llenas++;
                        }
                    }
                }

                // abrir nueva caja si todas las abiertas estan llenas
                if (llenas == abiertas && abiertas < 12) {
                    cajas.get(abiertas).setEstadoDeApertura(Caja.OPEN);
                    cajas.get(abiertas).setTiempoDeApertura(tiempoSimulacion);
                    System.out.println(">>> Se abrio la caja " + abiertas + " en t=" + tiempoSimulacion);
                }

                for (int i = 4; i < cajas.size(); i++) {
                    if (cajas.get(i).getEstadoDeApertura() == Caja.OPEN) {
                        boolean cajaVacia = filasCajeros.get(i).isEmpty() &&
                                cajas.get(i).getEstadoUso() == Caja.EMPTY;

                        if (cajaVacia) {
                            // Si es la primera vez que detectamos que está vacía, marcar el tiempo
                            if (tiempoVaciaDesde[i] == -1) {
                                tiempoVaciaDesde[i] = tiempoSimulacion;
                            }
                            // Si ha estado vacía por al menos 2 segundos, cerrarla
                            else if (tiempoSimulacion - tiempoVaciaDesde[i] >= 2.0) {
                                cajas.get(i).cerrarCaja(tiempoSimulacion);
                                tiempoVaciaDesde[i] = -1; // resetear el contador
                                System.out.println("<<< Se cerro la caja " + i + " en t=" + tiempoSimulacion);
                            }
                        } else {
                            // Si la caja ya no está vacía, resetear el contador
                            tiempoVaciaDesde[i] = -1;
                        }
                    }
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

    public ArrayList<ColaCircular<Cliente>> getFilasCajeros() {
        return filasCajeros;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public static  void main(String[] args) {
        new JFXPanel();
        new SimulacionActualidad();
    }
}
