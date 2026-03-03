package proyecto_final1;

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

    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = fin = nuevo;
        } else {
            nuevo.sgt = inicio;
            inicio.ant = nuevo;
            inicio = nuevo;
        }
    }

    public void eliminarPrimerElemento() {
        if (estaVacia()) return;
        if (inicio.sgt == null) {
            inicio = fin = null;
        } else {
            inicio = inicio.sgt;
            inicio.ant = null;
        }
    }

    public void eliminarUltimoElemento() {
        if (estaVacia()) return;
        if (fin.ant == null) {
            inicio = fin = null;
        } else {
            fin = fin.ant;
            fin.sgt = null;
        }
    }

    public void eliminarTodosElementos() {
        inicio = fin = null;
    }

    public int contarElementos() {
        int c = 0;
        Nodo<T> it = inicio;
        while (it != null) {
            c++;
            it = it.sgt;
        }
        return c;
    }

    public Nodo<T> getInicio() { return inicio; }
    public Nodo<T> getFin() { return fin; }
}