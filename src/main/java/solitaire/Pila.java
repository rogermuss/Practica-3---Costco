package solitaire;

public class Pila<T> {
    private T[] pila;
    private int tope = -1;
    private int maximoValor;
    private int minimoValor;


    public Pila(){
        pila = (T[])new Object[5];
    }



    public Pila(int size) {
        pila = (T[]) new Object[size];
    }

    public boolean isEmpty() {
        return tope==-1;
    }

    public boolean isFull() {
        return tope==pila.length-1;
    }

    public void push(T item) {
        if(isFull()){
            System.out.println("Pila Full");
        }else{
            tope += 1;
            pila[tope] = item;
        }
    }

    public T pop() {
        if (isEmpty()) {
            System.out.println("Pila Empty");
            return null;
        }
        T item = pila[tope];
        pila[tope] = null;
        tope--;
        return item;
    }

    public int size() {
        return tope + 1;
    }


    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return pila[tope];
    }

    public T get(int index) {
        if (index < 0 || index > tope) return null;
        return pila[index];
    }

    public void pushAll(T[] items) {
        for (T item : items) {
            push(item);
        }
    }


}
