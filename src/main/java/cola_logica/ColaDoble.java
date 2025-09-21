package cola_logica;

public class ColaDoble<T> {
    private T[] arreglo;
    private int left, right;

    public ColaDoble(int size) {
        arreglo = (T[]) new Object[size];
        left = -1;
        right = -1;
    }

    public boolean addFirst(T dato) {
        if (isFull()) return false;

        if (isEmpty()) {
            left = right = 0;
        } else {
            left = (left - 1 + arreglo.length) % arreglo.length;
        }

        arreglo[left] = dato;
        return true;
    }

    public boolean addLast(T dato) {
        if (isFull()) return false;

        if (isEmpty()) {
            left = right = 0;
        } else {
            right = (right + 1) % arreglo.length;
        }

        arreglo[right] = dato;
        return true;
    }

    public T removeFirst() {
        if (isEmpty()) {
            System.out.println("Subdesbordamiento");
            return null;
        }

        T dato = arreglo[left];
        if (left == right) { // un solo elemento
            left = right = -1;
        } else {
            left = (left + 1) % arreglo.length;
        }
        return dato;
    }

    public T removeLast() {
        if (isEmpty()) {
            System.out.println("Subdesbordamiento");
            return null;
        }

        T dato = arreglo[right];
        if (left == right) { // un solo elemento
            left = right = -1;
        } else {
            right = (right - 1 + arreglo.length) % arreglo.length;
        }
        return dato;
    }


    public boolean isFull() {
        return (left == (right + 1) % arreglo.length);
    }

    public boolean isEmpty() {
        return left == -1;
    }
}
