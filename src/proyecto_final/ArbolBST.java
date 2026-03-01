package proyecto_final;

import proyecto_final.Producto;

import java.util.*;

public class ArbolBST {

    private class Nodo {
        Producto p;
        Nodo izq, der;
        Nodo(Producto p) { this.p = p; }
    }

    private Nodo raiz;

    public boolean insertar(Producto p) {
        if (buscar(p.getSku()) != null)
            return false; // SKU duplicado

        raiz = insertarRec(raiz, p);
        return true;
    }

    private Nodo insertarRec(Nodo n, Producto p) {
        if (n == null) return new Nodo(p);

        if (p.getSku() < n.p.getSku())
            n.izq = insertarRec(n.izq, p);
        else
            n.der = insertarRec(n.der, p);

        return n;
    }

    public Producto buscar(int sku) {
        return buscarRec(raiz, sku);
    }

    private Producto buscarRec(Nodo n, int sku) {
        if (n == null) return null;
        if (sku == n.p.getSku()) return n.p;
        return sku < n.p.getSku()
                ? buscarRec(n.izq, sku)
                : buscarRec(n.der, sku);
    }

    public boolean eliminar(int sku) {
        if (buscar(sku) == null) return false;
        raiz = eliminarRec(raiz, sku);
        return true;
    }

    private Nodo eliminarRec(Nodo n, int sku) {
        if (n == null) return null;

        if (sku < n.p.getSku())
            n.izq = eliminarRec(n.izq, sku);
        else if (sku > n.p.getSku())
            n.der = eliminarRec(n.der, sku);
        else {
            // Caso 1 y 2
            if (n.izq == null) return n.der;
            if (n.der == null) return n.izq;

            // Caso 3 (dos hijos)
            Nodo sucesor = minimo(n.der);
            n.p = sucesor.p;
            n.der = eliminarRec(n.der, sucesor.p.getSku());
        }
        return n;
    }

    private Nodo minimo(Nodo n) {
        while (n.izq != null)
            n = n.izq;
        return n;
    }

    public List<Producto> inOrden() {
        List<Producto> lista = new ArrayList<>();
        inOrdenRec(raiz, lista);
        return lista;
    }

    private void inOrdenRec(Nodo n, List<Producto> lista) {
        if (n != null) {
            inOrdenRec(n.izq, lista);
            lista.add(n.p);
            inOrdenRec(n.der, lista);
        }
    }
}