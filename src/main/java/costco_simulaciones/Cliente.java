package costco_simulaciones;

public class Cliente {
    private int numeroCliente;
    private double tiempoLlegada;
    private double tiempoInicioPago;
    private double tiempoFinalPago;
    private double tiempoDeEspera;
    private double tiempoPagando;
    public enum Estado {
        FILA_UNICA,
        FILA_CAJA,
        PAGANDO,
        OUT
    }

    Estado estado;

    public Cliente(int numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setNumeroCliente(int numeroCliente) {
        this.numeroCliente = numeroCliente;
    }

    public int getNumeroCliente() {
        return numeroCliente;
    }

    public void setTiempoDeEspera(double tiempoDeEspera) {
        this.tiempoDeEspera = tiempoDeEspera;
    }
    public double getTiempoDeEspera() {
        return tiempoDeEspera;
    }
    public void setTiempoPagando(double tiempoPagando) {
        this.tiempoPagando = tiempoPagando;
    }
    public double getTiempoPagando() {
        return tiempoPagando;
    }

    public void setTiempoLlegada(double tiempoLlegada) {
        this.tiempoLlegada = tiempoLlegada;
    }

    public double getTiempoLlegada() {
        return tiempoLlegada;
    }

    //Metodo de Seteo y calculo del tiempo de pago
    public void setTiempoFinalPago(double tiempoFinalPago) {
        this.tiempoFinalPago = tiempoFinalPago;
        tiempoPagando = tiempoFinalPago - tiempoInicioPago;
    }
    public double getTiempoFinalPago() {
        return tiempoFinalPago;
    }

    //Metodo de Seteo y calculo del tiempo de espera
    public void setTiempoInicioPago(double tiempoInicioPago) {
        this.tiempoInicioPago = tiempoInicioPago;
        tiempoDeEspera = tiempoInicioPago - tiempoLlegada;
    }

    public double getTiempoInicioPago() {
        return tiempoInicioPago;
    }

    @Override
    public String toString() {
        return "Cliente " + numeroCliente +
                " | Tiempo de Espera =" + tiempoDeEspera +
                " | Tiempo Pagando = " + tiempoPagando;
    }
}
