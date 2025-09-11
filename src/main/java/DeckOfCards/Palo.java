package DeckOfCards;
/**
 * Palos de cartas de una baraja inglesa.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-1)
 */
public enum Palo {
    TREBOL(1,"♣","black"),
    DIAMANTE(2,"♦","red"),
    CORAZON(3,"❤","red"),
    PICA(4,"♠","black");

    private final int peso;
    private final String figura;
    private final String color;

    Palo(int peso, String figura, String color) {
        this.peso = peso;
        this.figura = figura;
        this.color = color;
    }
    public int getPeso() {
        return peso;
    }
    public String getFigura() {
        return figura;
    }
    public String getColor() {
        return color;
    }
}
