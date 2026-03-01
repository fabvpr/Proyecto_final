package proyecto_final;

public class ListaDoble {

    private class Nodo {
        String dato;
        Nodo sig, ant;
        Nodo(String d) { dato = d; }
    }

    private Nodo cabeza, cola;

    public void agregar(String dato) {
        Nodo nuevo = new Nodo(dato);
        if (cabeza == null)
            cabeza = cola = nuevo;
        else {
            cola.sig = nuevo;
            nuevo.ant = cola;
            cola = nuevo;
        }
    }

    public String mostrar() {
        StringBuilder sb = new StringBuilder();
        Nodo actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato).append("\n");
            actual = actual.sig;
        }
        return sb.toString();
    }
}
