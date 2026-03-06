package proyecto_final;

import java.util.ArrayList;
import java.util.List;

public class InventarioBST {

    public static class Producto {
        private final int id; // ÚNICO
        private String nombre;
        private int cantidad;
        private String categoria; // NUEVO

        public Producto(int id, String nombre, int cantidad, String categoria) {
            this.id = id;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.categoria = categoria;
        }

        public int getId() { return id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        @Override
        public String toString() {
            return "Producto{id=" + id +", nombre='" + nombre + '\'' +", categoria='" + 
            		categoria + '\'' +", cantidad=" + cantidad +"}";
        }
    }
    private static class NodoP {
        Producto dato;
        NodoP izq, der;
        NodoP(Producto dato) { this.dato = dato; }
    }

    private NodoP raiz;

    public boolean insertar(Producto p) {
        if (buscar(p.getId()) != null) return false;
        raiz = insertarRec(raiz, p);
        return true;
    }

    private NodoP insertarRec(NodoP n, Producto p) {
        if (n == null) return new NodoP(p);
        if (p.getId() < n.dato.getId()) n.izq = insertarRec(n.izq, p);
        else n.der = insertarRec(n.der, p);
        return n;
    }

    public Producto buscar(int id) {
        NodoP n = raiz;
        while (n != null) {
            if (id == n.dato.getId()) return n.dato;
            n = (id < n.dato.getId()) ? n.izq : n.der;
        }
        return null;
    }

   public boolean actualizar(int id, String nombre, int cantidad, String categoria) {
        Producto p = buscar(id);
        if (p == null) return false;
        p.setNombre(nombre);
        p.setCantidad(cantidad);
        p.setCategoria(categoria);
        return true;
    }

    public boolean eliminar(int id) {
        if (buscar(id) == null) return false;
        raiz = eliminarRec(raiz, id);
        return true;
    }

    private NodoP eliminarRec(NodoP n, int id) {
        if (n == null) return null;

        if (id < n.dato.getId()) n.izq = eliminarRec(n.izq, id);
        else if (id > n.dato.getId()) n.der = eliminarRec(n.der, id);
        else {
            // 0 o 1 hijo
            if (n.izq == null) return n.der;
            if (n.der == null) return n.izq;
            // 2 hijos: reemplazar por sucesor inorder
            NodoP suc = minNodo(n.der);
            n.dato = suc.dato;
            n.der = eliminarRec(n.der, suc.dato.getId());
        }
        return n;
    }

    private NodoP minNodo(NodoP n) {
        while (n.izq != null) n = n.izq;
        return n;
    }

    public List<Producto> enOrden() {
        List<Producto> res = new ArrayList<>();
        enOrdenRec(raiz, res);
        return res;
    }

    private void enOrdenRec(NodoP n, List<Producto> res) {
        if (n == null) return;
        enOrdenRec(n.izq, res);
        res.add(n.dato);
        enOrdenRec(n.der, res);
    }
}