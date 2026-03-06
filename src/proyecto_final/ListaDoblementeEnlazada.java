package proyecto_final;

public class ListaDoblementeEnlazada<T> {

    public static class Nodo<T> {
        private final T dato;
        private Nodo<T> sgt;
        private Nodo<T> ant;

        public Nodo(T dato) { this.dato = dato; }

        public T getDato() { return dato; }
        public Nodo<T> getSgt() { return sgt; }
        public Nodo<T> getAnt() { return ant; }
    }

    private Nodo<T> inicio;
    private Nodo<T> fin;
    
    public boolean estaVacia() {
        return inicio == null;
    }
    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = fin = nuevo;
        } else {
            fin.sgt = nuevo;
            nuevo.ant = fin;
            fin = nuevo;
        }
    }
    public Nodo<T> getInicio() { return inicio; }
    public Nodo<T> getFin() { return fin; }
}