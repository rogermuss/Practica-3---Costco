package costco_simulaciones;

import cola_logica.ColaCircular;

public class Caja {
    private boolean estadoUso;
    private boolean estadoDeApertura;
    private double tiempoDeApertura;
    private double tiempoFinal;
    private double tiempoEnUso = 0;
    private Cliente cliente;
    private ColaCircular<Cliente> filaCaja;
    private int atendidos;

    public static final boolean OPEN = true;
    public static final boolean CLOSE = false;
    public static final boolean OCCUPIED = true;
    public static final boolean EMPTY = false;

    public Caja() {
        this.estadoDeApertura = CLOSE;
        this.estadoUso = EMPTY;
        atendidos = 0;
        tiempoDeApertura = -1; // Inicializar en -1 para detectar si nunca se abrió
    }

    public void setEstadoDeApertura(boolean estado) {
        this.estadoDeApertura = estado;
    }

    public boolean getEstadoDeApertura() {
        return this.estadoDeApertura;
    }

    public void setEstadoUso(boolean estadoUso) {
        this.estadoUso = estadoUso;
    }

    public boolean getEstadoUso() {
        return this.estadoUso;
    }

    public boolean isOccupied(){
        return estadoUso;
    }

    public boolean isEmpty(){
        return !estadoUso;
    }

    public void setTiempoEnUso(double tiempoEnUso) {
        this.tiempoEnUso = tiempoEnUso;
    }

    public double getTiempoEnUso() {
        return this.tiempoEnUso;
    }

    public void setTiempoDeApertura(double tiempoDeApertura) {
        this.tiempoDeApertura = tiempoDeApertura;
    }

    public double getTiempoDeApertura() {
        return this.tiempoDeApertura;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Cliente getCliente() {
        return this.cliente;
    }

    // Calcula y acumula el tiempo de uso cuando se cierra
    public void cerrarCaja(double tiempoActual) {
        if (this.estadoDeApertura == OPEN && this.tiempoDeApertura >= 0) {
            // Acumular el tiempo que estuvo abierta en este período
            this.tiempoEnUso += (tiempoActual - this.tiempoDeApertura);
        }
        this.tiempoFinal = tiempoActual;
        this.estadoDeApertura = CLOSE;
    }

    public void setAtendidos(int atendidos) {
        this.atendidos = atendidos;
    }

    public int getAtendidos() {
        return atendidos;
    }

    public void aumentarAtendidos(){
        this.atendidos++;
    }

    public void setFilaCaja(ColaCircular<Cliente> filaCaja) {
        this.filaCaja = filaCaja;
    }

    public ColaCircular<Cliente> getFilaCaja() {
        return filaCaja;
    }

    public void setTiempoFinal(double tiempoFinal) {
        if (this.estadoDeApertura == OPEN && this.tiempoDeApertura >= 0) {
            // Solo sumar si la caja está abierta (para el final de simulación)
            this.tiempoEnUso += (tiempoFinal - this.tiempoDeApertura);
        }
        this.tiempoFinal = tiempoFinal;
    }

    public double getTiempoFinal() {
        return this.tiempoFinal;
    }

    public double getTiempoEnUsoActual(double tiempoActual) {
        double tiempoTotal = this.tiempoEnUso;

        // Si está actualmente abierta, sumar el tiempo desde la última apertura
        if (this.estadoDeApertura == OPEN && this.tiempoDeApertura >= 0) {
            tiempoTotal += (tiempoActual - this.tiempoDeApertura);
        }

        return tiempoTotal;
    }
}