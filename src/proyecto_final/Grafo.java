package proyecto_final;

import java.util.*;

public class Grafo {

    private Map<String, List<Arista>> ady = new HashMap<>();

    private class Arista {
        String destino;
        int peso;

        Arista(String d, int p) {
            destino = d;
            peso = p;
        }
    }

    public boolean agregarNodo(String nombre) {
        if (ady.containsKey(nombre)) return false;
        ady.put(nombre, new ArrayList<>());
        return true;
    }

    public boolean eliminarNodo(String nombre) {
        if (!ady.containsKey(nombre)) return false;

        ady.remove(nombre);

        for (List<Arista> lista : ady.values()) {
            lista.removeIf(a -> a.destino.equals(nombre));
        }
        return true;
    }

    public boolean agregarArista(String o, String d, int peso) {
        if (!ady.containsKey(o) || !ady.containsKey(d)) return false;

        ady.get(o).add(new Arista(d, peso));
        ady.get(d).add(new Arista(o, peso));
        return true;
    }

    public boolean eliminarArista(String o, String d) {
        if (!ady.containsKey(o) || !ady.containsKey(d)) return false;

        ady.get(o).removeIf(a -> a.destino.equals(d));
        ady.get(d).removeIf(a -> a.destino.equals(o));
        return true;
    }

    public Set<String> getNodos() {
        return ady.keySet();
    }

    // Dijkstra con reconstrucción de ruta
    public String rutaMasCorta(String origen, String destino) {

        if (!ady.containsKey(origen) || !ady.containsKey(destino))
            return "Nodo inválido.";

        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> previo = new HashMap<>();

        for (String nodo : ady.keySet())
            dist.put(nodo, Integer.MAX_VALUE);

        PriorityQueue<String> cola =
                new PriorityQueue<>(Comparator.comparing(dist::get));

        dist.put(origen, 0);
        cola.add(origen);

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            for (Arista a : ady.get(actual)) {
                int nueva = dist.get(actual) + a.peso;
                if (nueva < dist.get(a.destino)) {
                    dist.put(a.destino, nueva);
                    previo.put(a.destino, actual);
                    cola.add(a.destino);
                }
            }
        }

        if (!previo.containsKey(destino) && !origen.equals(destino))
            return "No existe ruta.";

        List<String> ruta = new LinkedList<>();
        String paso = destino;

        while (paso != null) {
            ruta.add(0, paso);
            paso = previo.get(paso);
        }

        return "Ruta: " + String.join(" -> ", ruta) +
               "\nDistancia total: " + dist.get(destino);
    }
    
    public String mostrarConexiones() {
        StringBuilder sb = new StringBuilder();

        for (String nodo : ady.keySet()) {
            sb.append(nodo).append(" -> ");

            List<Arista> lista = ady.get(nodo);
            if (lista.isEmpty()) {
                sb.append("Sin conexiones");
            } else {
                for (Arista a : lista) {
                    sb.append(a.destino)
                      .append(" (")
                      .append(a.peso)
                      .append(")  ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}