package solitaire;

import DeckOfCards.CartaInglesa;

import java.util.ArrayList;
/**
 * Modela el montículo donde se colocan las cartas
 * que se extraen de Draw pile.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-2)
 */
public class WastePile {
    private Pila<CartaInglesa> cartas;

    public WastePile() {
        cartas = new Pila<>(52); // tamaño maximo de un mazo
    }

    // Agregar una sola carta
    public void addCarta(CartaInglesa nueva) {
        cartas.push(nueva);
    }

    // Vaciar la pila y regresar todas las cartas
    public CartaInglesa[] emptyPile() {
        if (cartas.isEmpty()) {
            return new CartaInglesa[0];
        }
        CartaInglesa[] pile = new CartaInglesa[cartas.size()];
        for (int i = pile.length - 1; i >= 0; i--) {
            pile[i] = cartas.pop();
        }
        return pile;
    }

    // Obtener la carta de arriba sin removerla
    public CartaInglesa verCarta() {
        return cartas.peek();
    }

    // Obtener y remover la carta de arriba
    public CartaInglesa getCarta() {
        return cartas.pop();
    }

    @Override
    public String toString() {
        if (cartas.isEmpty()) {
            return "---";
        } else {
            CartaInglesa carta = cartas.peek();
            carta.makeFaceUp();
            return carta.toString();
        }
    }

    public boolean hayCartas() {
        return !cartas.isEmpty();
    }
}
