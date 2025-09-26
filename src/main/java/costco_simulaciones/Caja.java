package costco_simulaciones;

import cola_logica.ColaCircular;

public class Caja {
    private boolean estadoUso;
    private boolean estadoDeApertura;
    private double tiempoDeApertura;
    private double tiempoFinal;
    private double tiempoEnUso;
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
    }


    public void setEstadoDeApertura(boolean estado) {
        this.estadoDeApertura = estado;  // era this.estadoUso
    }

    public boolean getEstadoDeApertura() {
        return this.estadoDeApertura;    // era this.estadoUso
    }

    public void setEstadoUso(boolean estadoUso) {
        this.estadoUso = estadoUso;
    }
    public boolean getEstadoUso() {
        return this.estadoUso;
    }

    public boolean isOccupied(){ return estadoUso;}
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
        this.tiempoFinal = tiempoFinal;
        if (this.estadoDeApertura == OPEN) {
            this.tiempoEnUso = tiempoFinal - tiempoDeApertura;
        } else {
            this.tiempoEnUso = 0.0;
        }
    }
    public double getTiempoFinal() {
        return this.tiempoFinal;
    }

}
