package proyecto_final1;

import java.util.*;

public class GrafoPesado {

    public static class ResultadoRuta {
        public final int costo;
        public final List<String> camino;

        ResultadoRuta(int costo, List<String> camino) {
            this.costo = costo;
            this.camino = camino;
        }
    }

    private static class Arista {
        int destino, peso;
        Arista(int destino, int peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    private final List<List<Arista>> ady = new ArrayList<>();
    private final Map<String, Integer> indicePorNombre = new HashMap<>();
    private final List<String> nombrePorIndice = new ArrayList<>();

    private static final int INF = 1_000_000_000;

    public GrafoPesado() { }

    public int getNumVertices() { return ady.size(); }

    // =============================
    // AGREGAR NODO (POR LETRA/NOMBRE)
    // =============================
    public boolean agregarNodo(String nombre) {
        if (nombre == null) return false;
        nombre = nombre.trim().toUpperCase();
        if (nombre.isEmpty()) return false;

        if (indicePorNombre.containsKey(nombre)) return false;

        int nuevoIndice = ady.size();
        indicePorNombre.put(nombre, nuevoIndice);
        nombrePorIndice.add(nombre);
        ady.add(new ArrayList<>());
        return true;
    }

    public boolean existeNodo(String nombre) {
        if (nombre == null) return false;
        return indicePorNombre.containsKey(nombre.trim().toUpperCase());
    }

    public Set<String> getNombresNodos() {
        return new TreeSet<>(indicePorNombre.keySet());
    }

    // =============================
    // AGREGAR ARISTA POR LETRAS
    // (NO DIRIGIDO)
    // =============================
    public boolean agregarArista(String origen, String destino, int peso) {
        if (origen == null || destino == null) return false;
        origen = origen.trim().toUpperCase();
        destino = destino.trim().toUpperCase();

        if (!indicePorNombre.containsKey(origen) || !indicePorNombre.containsKey(destino)) return false;
        if (peso <= 0) return false;

        int o = indicePorNombre.get(origen);
        int d = indicePorNombre.get(destino);

        ady.get(o).add(new Arista(d, peso));
        ady.get(d).add(new Arista(o, peso));
        return true;
    }

    // =============================
    // CONEXION DIRECTA (¿HAY ARISTA?)
    // =============================
    public boolean existeConexionDirecta(String origen, String destino) {
        if (origen == null || destino == null) return false;
        origen = origen.trim().toUpperCase();
        destino = destino.trim().toUpperCase();

        if (!indicePorNombre.containsKey(origen) || !indicePorNombre.containsKey(destino)) return false;

        int o = indicePorNombre.get(origen);
        int d = indicePorNombre.get(destino);

        for (Arista a : ady.get(o)) {
            if (a.destino == d) return true;
        }
        return false;
    }

    // =============================
    // MOSTRAR
    // =============================
    public String mostrarComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ady.size(); i++) {
            sb.append(nombrePorIndice.get(i)).append(": ");
            for (Arista a : ady.get(i)) {
                sb.append("-> (")
                  .append(nombrePorIndice.get(a.destino))
                  .append(", w=")
                  .append(a.peso)
                  .append(") ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // =============================
    // DIJKSTRA (POR LETRAS)
    // =============================
    public ResultadoRuta dijkstra(String inicio, String fin) {
        if (inicio == null || fin == null) return new ResultadoRuta(INF, Collections.emptyList());
        inicio = inicio.trim().toUpperCase();
        fin = fin.trim().toUpperCase();

        if (!indicePorNombre.containsKey(inicio) || !indicePorNombre.containsKey(fin)) {
            return new ResultadoRuta(INF, Collections.emptyList());
        }

        int n = ady.size();
        int[] dist = new int[n];
        int[] padre = new int[n];
        boolean[] vis = new boolean[n];

        Arrays.fill(dist, INF);
        Arrays.fill(padre, -1);

        int s = indicePorNombre.get(inicio);
        int t = indicePorNombre.get(fin);

        dist[s] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(x -> x[1]));
        pq.add(new int[]{s, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0];
            if (vis[u]) continue;
            vis[u] = true;

            for (Arista a : ady.get(u)) {
                int v = a.destino;
                int nd = dist[u] + a.peso;
                if (!vis[v] && nd < dist[v]) {
                    dist[v] = nd;
                    padre[v] = u;
                    pq.add(new int[]{v, dist[v]});
                }
            }
        }

        if (dist[t] == INF) return new ResultadoRuta(INF, Collections.emptyList());

        return new ResultadoRuta(dist[t], reconstruir(padre, s, t));
    }

    private List<String> reconstruir(int[] padre, int inicio, int fin) {
        LinkedList<String> path = new LinkedList<>();
        int cur = fin;
        while (cur != -1) {
            path.addFirst(nombrePorIndice.get(cur));
            if (cur == inicio) break;
            cur = padre[cur];
        }
        return path;
    }
}