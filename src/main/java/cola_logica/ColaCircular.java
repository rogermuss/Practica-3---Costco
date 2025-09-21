package cola_logica;

public class ColaCircular<T> {
    private int inicio = -1;  // índice del primer elemento
    private int fin = -1;     // índice del último elemento
    private int max;          // tamaño máximo de la cola
    private T[] cola;

    public ColaCircular(int size) {
        cola = (T[]) new Object[size];
        this.max = size;
    }

    // Agrega un elemento al final
    public boolean push(T dato) {
        if (isFull()) return false;

        if (isEmpty()) {
            inicio = 0;
            fin = 0;
        } else {
            fin = (fin + 1) % max; // avanza circularmente
        }

        cola[fin] = dato;
        return true;
    }

    // Quita un elemento del inicio
    public T pop() {
        if (isEmpty()) return null;

        T dato = cola[inicio];

        if (inicio == fin) { // solo había un elemento
            inicio = -1;
            fin = -1;
        } else {
            inicio = (inicio + 1) % max; // avanza circularmente
        }

        return dato;
    }

    // Verifica si la cola está vacía
    public boolean isEmpty() {
        return inicio == -1;
    }

    // Verifica si la cola está llena
    public boolean isFull() {
        return ((fin + 1) % max) == inicio;
    }

    // Número de elementos actuales
    public int size() {
        if (isEmpty()) return 0;
        if (fin >= inicio) {
            return fin - inicio + 1;
        } else {
            return max - inicio + fin + 1;
        }
    }
}
