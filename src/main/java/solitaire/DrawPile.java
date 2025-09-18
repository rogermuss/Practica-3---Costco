package solitaire;

import DeckOfCards.CartaInglesa;

/**
 * Modela un mazo de cartas de solitario.
 * @author Cecilia Curlango
 * @version 2025
 */
public class DrawPile {
    private Pila<CartaInglesa> cartas;
    private int cuantasCartasSeEntregan = 3;

    public DrawPile() {
        DeckOfCards.Mazo mazo = new DeckOfCards.Mazo();
        java.util.ArrayList<CartaInglesa> lista = mazo.getCartas();
        cartas = new Pila<>(lista.size());
        for (CartaInglesa c : lista) {
            cartas.push(c);
        }
        setCuantasCartasSeEntregan(3);
    }

    public void setCuantasCartasSeEntregan(int cuantasCartasSeEntregan) {
        this.cuantasCartasSeEntregan = cuantasCartasSeEntregan;
    }

    public int getCuantasCartasSeEntregan() {
        return cuantasCartasSeEntregan;
    }

    /**
     * Retira N cartas (para repartir a los tableaus al inicio).
     */
    public java.util.ArrayList<CartaInglesa> getCartas(int cantidad) {
        java.util.ArrayList<CartaInglesa> retiradas = new java.util.ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            retiradas.add(cartas.pop());
        }
        return retiradas;
    }

    /**
     * Retira el bloque de cartas configurado (1 o 3 normalmente).
     */
    public java.util.ArrayList<CartaInglesa> retirarCartas() {
        java.util.ArrayList<CartaInglesa> retiradas = new java.util.ArrayList<>();
        int maximoARetirar = cartas.size() < cuantasCartasSeEntregan ? cartas.size() : cuantasCartasSeEntregan;

        for (int i = 0; i < maximoARetirar; i++) {
            CartaInglesa retirada = cartas.pop();
            retirada.makeFaceUp();
            retiradas.add(retirada);
        }
        return retiradas;
    }

    public boolean hayCartas() {
        return !cartas.isEmpty();
    }

    public CartaInglesa verCarta() {
        return cartas.peek();
    }

    /**
     * Recarga el monton (cuando se recicla el WastePile).
     */
    public void recargar(java.util.ArrayList<CartaInglesa> cartasAgregar) {
        cartas = new Pila<>(cartasAgregar.size());
        for (CartaInglesa c : cartasAgregar) {
            c.makeFaceDown();
            cartas.push(c);
        }
    }

    @Override
    public String toString() {
        if (cartas.isEmpty()) {
            return "-E-";
        }
        return "@";
    }
}
