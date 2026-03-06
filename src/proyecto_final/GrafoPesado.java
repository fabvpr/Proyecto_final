package proyecto_final;

import java.util.*;

public class GrafoPesado {

    public static class ResultadoRuta {
        //final: indica que el valor no puede modificarse una vez asigando
    	public final int costo;
        public final List<String> camino;
        //se usa para el método disjktra
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

    public boolean agregarNodo(String nombre) {
        nombre = nombre.trim().toUpperCase();

        if (indicePorNombre.containsKey(nombre)) return false;

        int nuevoIndice = ady.size();
        indicePorNombre.put(nombre, nuevoIndice);
        nombrePorIndice.add(nombre);
        ady.add(new ArrayList<>());
        return true;
    }

    public Set<String> getNombresNodos() {
        return new TreeSet<>(indicePorNombre.keySet());
    }

    public boolean eliminarNodo(String nombre) {
        if (nombre == null) return false;
        nombre = nombre.trim().toUpperCase();
        if (!indicePorNombre.containsKey(nombre)) return false;//NO EXISTE EL INDICE DE ESE NODO

        int idxEliminar = indicePorNombre.get(nombre);

        Map<Integer, Integer> oldToNew = new HashMap<>();
        List<String> nuevosNombres = new ArrayList<>();
        //elimina el nodo
        for (int old = 0, neu = 0; old < nombrePorIndice.size(); old++) {
            if (old == idxEliminar) continue;
            oldToNew.put(old, neu);
            nuevosNombres.add(nombrePorIndice.get(old));
            neu++;
        }
        //elimina la arista
        List<List<Arista>> nuevaAdy = new ArrayList<>();
        for (int i = 0; i < nuevosNombres.size(); i++) nuevaAdy.add(new ArrayList<>());
        //Copiar aristas ignorando las que toquen al eliminado
        for (int oldU = 0; oldU < ady.size(); oldU++) {
            if (oldU == idxEliminar) continue;
            int newU = oldToNew.get(oldU);

            for (Arista a : ady.get(oldU)) {
                int oldV = a.destino;
                if (oldV == idxEliminar) continue;

                int newV = oldToNew.get(oldV);
                nuevaAdy.get(newU).add(new Arista(newV, a.peso));
            }
        }

        ady.clear();
        ady.addAll(nuevaAdy);

        indicePorNombre.clear();
        nombrePorIndice.clear();
        nombrePorIndice.addAll(nuevosNombres);

        for (int i = 0; i < nombrePorIndice.size(); i++) {
            indicePorNombre.put(nombrePorIndice.get(i), i);
        }

        return true;
    }

    public boolean agregarArista(String origen, String destino, int peso) {
        if (origen == null || destino == null) return false;
        origen = origen.trim().toUpperCase();
        destino = destino.trim().toUpperCase();
        
        if (!indicePorNombre.containsKey(origen) 
        		|| !indicePorNombre.containsKey(destino)) return false;
        if (peso <= 0) return false;

        int o = indicePorNombre.get(origen);
        int d = indicePorNombre.get(destino);

        ady.get(o).add(new Arista(d, peso));
        ady.get(d).add(new Arista(o, peso));
        return true;
    }
    
    public boolean eliminarArista(String origen, String destino) {
        if (origen == null || destino == null) return false;
        origen = origen.trim().toUpperCase();
        destino = destino.trim().toUpperCase();

        if (!indicePorNombre.containsKey(origen) 
        		|| !indicePorNombre.containsKey(destino)) 
        	return false;

        int o = indicePorNombre.get(origen);
        int d = indicePorNombre.get(destino);

        boolean removed1 = ady.get(o).removeIf(a -> a.destino == d);
        boolean removed2 = ady.get(d).removeIf(a -> a.destino == o);

        return removed1 || removed2;
    }
    
    public String mostrarConexiones() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ady.size(); i++) {
            String nodo = nombrePorIndice.get(i);
            sb.append(nodo).append(" -> ");

            if (ady.get(i).isEmpty()) {
                sb.append("Sin conexiones");
            } else {
                for (Arista a : ady.get(i)) {
                    sb.append(nombrePorIndice.get(a.destino))
                      .append(" (")
                      .append(a.peso)
                      .append(")  ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public ResultadoRuta dijkstra(String inicio, String fin) {
        inicio = inicio.trim().toUpperCase();
        fin = fin.trim().toUpperCase();

        if (!indicePorNombre.containsKey(inicio) 
        		|| !indicePorNombre.containsKey(fin)) {
            return new ResultadoRuta(INF, Collections.emptyList());
        }

        int n = ady.size();
        int[] dist = new int[n];
        int[] padre = new int[n];
        boolean[] vis = new boolean[n];

        Arrays.fill(dist, INF);
        Arrays.fill(padre, -1);
        //dis[]=[1,3,6,4,3]
        //dist=[inf,inf,inf,inf,inf]
        //padre[]=[A,C,D,E]
        //padre=[-1,0,1,2,3]
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

            for (Arista a : ady.get(u)) {//guarda aristas de u
                int v = a.destino;//a donde llega un arista osea arista de u a v
                int nd = dist[u] + a.peso;//calcula la distancia acumula hasta u y el peso hasta v
                if (!vis[v] && nd < dist[v]) {//verifica si se escoge la distancia corta
                    dist[v] = nd;//actualiza la distancia
                    padre[v] = u;//actualiza el padre
                    pq.add(new int[]{v, dist[v]});//remplaza a u por el nodo con menor distancia
                }// si no se cumeple el i entonces vuelve a entrar al for para buscar a otra arista 
            }//la cola queda vacia cuando se recorrio todos los caminos posibles
        }
        if (dist[t] == INF) //verifica si no esxite la ruta
        	return new ResultadoRuta(INF, Collections.emptyList());//si no existe, su distancia es in
        return new ResultadoRuta(dist[t], reconstruir(padre, s, t));/*min dist, y reconstruir*/
    }

    private List<String> reconstruir(int[] padre, int inicio, int fin) {
        LinkedList<String> path = new LinkedList<>(); //permite gudars los padres desde el fin hasta el inico
        int cur = fin;//empieza en el nodo final
        while (cur != -1) {///empieza desde el fin hasta llegar al inicio
            path.addFirst(nombrePorIndice.get(cur));//permite añadir al inicio de la lista
            if (cur == inicio) break;// verifica si llegamos la inicio
            cur = padre[cur];//vamos al nodo anterior	
        }
        return path;
        //basicamente empieza al final pero va insertando al incio los anteriores para que recontrulla
        //el camino mas corto en orden [a,b,d,f] algo asi imrprimira empiezando desde f hasta a
    }
}