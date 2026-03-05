package proyecto_final;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {

    // ======== SISTEMA CENTRAL (CLASE INTERNA) ========
    private static class SistemaLogistica {
        private final InventarioBST inventario = new InventarioBST();
        private final ListaDoblementeEnlazada<Operacion> historial = new ListaDoblementeEnlazada<>();
        private final GrafoPesado grafo;

        public SistemaLogistica() {
            this.grafo = new GrafoPesado();
        }

        public InventarioBST getInventario() { return inventario; }
        public ListaDoblementeEnlazada<Operacion> getHistorial() { return historial; }
        public GrafoPesado getGrafo() { return grafo; }

        public boolean altaProducto(int id, String nombre, String categoria, int cantidad) {
            boolean ok = inventario.insertar(new InventarioBST.Producto(id, nombre, cantidad, categoria));
            if (ok) historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.INGRESO,
                    id, nombre, categoria, cantidad,
                    "Alta de producto"
            ));
            return ok;
        }

        public boolean bajaProducto(int id) {
            InventarioBST.Producto p = inventario.buscar(id);
            if (p == null) return false;
            boolean ok = inventario.eliminar(id);
            if (ok) historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.SALIDA,
                    id, p.getNombre(), p.getCategoria(), p.getCantidad(),
                    "Baja de producto"
            ));
            return ok;
        }

        public boolean actualizarProducto(int id, String nombre, String categoria, int cantidad) {
            boolean ok = inventario.actualizar(id, nombre, cantidad, categoria);
            if (ok) historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.INGRESO,
                    id, nombre, categoria, cantidad,
                    "Actualización"
            ));
            return ok;
        }

        public boolean salidaStock(int id, int cantidad, String obs) {
            InventarioBST.Producto p = inventario.buscar(id);
            if (p == null) return false;
            if (cantidad <= 0 || p.getCantidad() < cantidad) return false;

            p.setCantidad(p.getCantidad() - cantidad);
            historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.SALIDA,
                    id, p.getNombre(), p.getCategoria(), cantidad,
                    obs
            ));
            return true;
        }

        public boolean ingresoStock(int id, int cantidad, String obs) {
            InventarioBST.Producto p = inventario.buscar(id);
            if (p == null) return false;
            if (cantidad <= 0) return false;

            p.setCantidad(p.getCantidad() + cantidad);
            historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.INGRESO,
                    id, p.getNombre(), p.getCategoria(), cantidad,
                    obs
            ));
            return true;
        }
    }

    // ======== UI ========
    private final SistemaLogistica sistema;

    public MainFrame() {
        super("Proyecto Final - Logística (Compacto)");

        this.sistema = new SistemaLogistica();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Red (Grafo)", new PanelRed());          // <- cambiado
        tabs.addTab("Inventario (BST)", new PanelInventario());
        tabs.addTab("Historial (Lista Doble)", new PanelHistorial());

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // ==========================================================
    // =============== PANELES (CLASES INTERNAS) =================
    // ==========================================================

    // ==========================================================
    // PANEL RED (NUEVO ESTILO COMO PROYECTO 2)
    // ==========================================================
    private class PanelRed extends JPanel {

        private final JTextArea areaConexiones;
        private final JComboBox<String> comboOrigen;
        private final JComboBox<String> comboDestino;

        public PanelRed() {
            setLayout(new BorderLayout(10, 10));

            JPanel contenedor = new JPanel();
            contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

            // =============================
            // AREA CONEXIONES DIRECTAS
            // =============================
            areaConexiones = new JTextArea(8, 50);
            areaConexiones.setEditable(false);
            JScrollPane scrollConexiones = new JScrollPane(areaConexiones);
            scrollConexiones.setBorder(BorderFactory.createTitledBorder("Conexiones Directas"));

            // =============================
            // PANEL NODOS
            // =============================
            JPanel panelNodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelNodo.setBorder(BorderFactory.createTitledBorder("Gestión de Nodos"));

            JTextField txtNodo = new JTextField(10);
            JButton btnAgregarNodo = new JButton("Agregar Nodo");
            JButton btnEliminarNodo = new JButton("Eliminar Nodo");

            panelNodo.add(new JLabel("Nodo:"));
            panelNodo.add(txtNodo);
            panelNodo.add(btnAgregarNodo);
            panelNodo.add(btnEliminarNodo);

            // =============================
            // PANEL RUTAS (ARISTAS)
            // =============================
            JPanel panelRuta = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelRuta.setBorder(BorderFactory.createTitledBorder("Gestión de Rutas"));

            comboOrigen = new JComboBox<>();
            comboDestino = new JComboBox<>();
            JTextField txtDistancia = new JTextField(6);

            JButton btnAgregarRuta = new JButton("Agregar Ruta");
            JButton btnEliminarRuta = new JButton("Eliminar Ruta");

            panelRuta.add(new JLabel("Origen:"));
            panelRuta.add(comboOrigen);
            panelRuta.add(new JLabel("Destino:"));
            panelRuta.add(comboDestino);
            panelRuta.add(new JLabel("Distancia:"));
            panelRuta.add(txtDistancia);
            panelRuta.add(btnAgregarRuta);
            panelRuta.add(btnEliminarRuta);

            // =============================
            // PANEL CALCULAR RUTA MÁS CORTA
            // =============================
            JPanel panelCalculo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelCalculo.setBorder(BorderFactory.createTitledBorder("Ruta Más Corta (Dijkstra)"));

            JButton btnCalcular = new JButton("Calcular");
            JTextArea resultado = new JTextArea(4, 60);
            resultado.setEditable(false);

            panelCalculo.add(btnCalcular);
            panelCalculo.add(new JScrollPane(resultado));

            // ==================================================
            // ================== EVENTOS =======================
            // ==================================================

            // AGREGAR NODO
            btnAgregarNodo.addActionListener(e -> {
                String nombre = txtNodo.getText().trim();
                if (nombre.isEmpty()) return;

                boolean ok = sistema.getGrafo().agregarNodo(nombre);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "El nodo ya existe o es inválido.");
                } else {
                    // (opcional) registrar en historial de operaciones si quieres
                    // sistema.getHistorial().agregarFinal(new Operacion(...));
                }

                txtNodo.setText("");
                refrescarTodo();
            });

            // ELIMINAR NODO
            btnEliminarNodo.addActionListener(e -> {
                String nombre = txtNodo.getText().trim();
                if (nombre.isEmpty()) return;

                boolean ok = sistema.getGrafo().eliminarNodo(nombre);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "El nodo no existe.");
                }

                txtNodo.setText("");
                refrescarTodo();
            });

            // AGREGAR RUTA
            btnAgregarRuta.addActionListener(e -> {
                try {
                    if (comboOrigen.getSelectedItem() == null || comboDestino.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(this, "No hay nodos suficientes.");
                        return;
                    }

                    String o = comboOrigen.getSelectedItem().toString();
                    String d = comboDestino.getSelectedItem().toString();
                    int dist = Integer.parseInt(txtDistancia.getText().trim());

                    if (o.equals(d)) {
                        JOptionPane.showMessageDialog(this, "No puede conectar un nodo consigo mismo.");
                        return;
                    }
                    if (dist <= 0) {
                        JOptionPane.showMessageDialog(this, "La distancia debe ser > 0.");
                        return;
                    }

                    boolean ok = sistema.getGrafo().agregarArista(o, d, dist);
                    if (!ok) JOptionPane.showMessageDialog(this, "Error al agregar ruta (verifique nodos).");

                    txtDistancia.setText("");
                    refrescarTodo();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Distancia inválida.");
                }
            });

            // ELIMINAR RUTA
            btnEliminarRuta.addActionListener(e -> {
                if (comboOrigen.getSelectedItem() == null || comboDestino.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "No hay nodos suficientes.");
                    return;
                }

                String o = comboOrigen.getSelectedItem().toString();
                String d = comboDestino.getSelectedItem().toString();

                boolean ok = sistema.getGrafo().eliminarArista(o, d);
                if (!ok) JOptionPane.showMessageDialog(this, "La ruta no existe.");

                refrescarTodo();
            });

            // CALCULAR RUTA MÁS CORTA
            btnCalcular.addActionListener(e -> {
                if (comboOrigen.getSelectedItem() == null || comboDestino.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "No hay nodos suficientes.");
                    return;
                }

                String o = comboOrigen.getSelectedItem().toString();
                String d = comboDestino.getSelectedItem().toString();

                GrafoPesado.ResultadoRuta r = sistema.getGrafo().dijkstra(o, d);

                if (r.camino.isEmpty()) {
                    resultado.setText("No existe ruta.");
                } else {
                    resultado.setText(
                            "Ruta: " + String.join(" -> ", r.camino) +
                            "\nDistancia total: " + r.costo
                    );
                }
            });

            // =============================
            // ARMAR CONTENEDOR
            // =============================
            contenedor.add(scrollConexiones);
            contenedor.add(panelNodo);
            contenedor.add(panelRuta);
            contenedor.add(panelCalculo);

            add(contenedor, BorderLayout.CENTER);

            refrescarTodo();
        }

        private void refrescarTodo() {
            areaConexiones.setText(sistema.getGrafo().mostrarConexiones());
            actualizarCombos();
        }

        private void actualizarCombos() {
            String selO = (comboOrigen.getSelectedItem() != null) ? comboOrigen.getSelectedItem().toString() : null;
            String selD = (comboDestino.getSelectedItem() != null) ? comboDestino.getSelectedItem().toString() : null;

            comboOrigen.removeAllItems();
            comboDestino.removeAllItems();

            for (String nodo : sistema.getGrafo().getNombresNodos()) {
                comboOrigen.addItem(nodo);
                comboDestino.addItem(nodo);
            }

            // intentar restaurar selección
            if (selO != null) comboOrigen.setSelectedItem(selO);
            if (selD != null) comboDestino.setSelectedItem(selD);
        }
    }

    // ==========================
    // PANEL INVENTARIO (IGUAL)
    // ==========================
    private class PanelInventario extends JPanel {
        private final DefaultTableModel model;

        public PanelInventario() {
            setLayout(new BorderLayout(10, 10));

            JPanel form = new JPanel(new GridLayout(2, 5, 10, 10));

            JTextField txtId = new JTextField();
            JTextField txtNombre = new JTextField();
            JTextField txtCategoria = new JTextField();
            JTextField txtCantidad = new JTextField();

            JButton btnAlta = new JButton("Alta");
            JButton btnActualizar = new JButton("Actualizar");
            JButton btnBaja = new JButton("Baja");

            form.add(new JLabel("ID (único):"));
            form.add(txtId);
            form.add(new JLabel("Nombre:"));
            form.add(txtNombre);
            form.add(new JLabel("Categoría:"));
            form.add(txtCategoria);

            form.add(new JLabel("Cantidad:"));
            form.add(txtCantidad);
            form.add(btnAlta);
            form.add(btnActualizar);
            form.add(new JLabel(""));

            model = new DefaultTableModel(new Object[]{"ID", "Nombre", "Categoría", "Cantidad"}, 0);
            JTable table = new JTable(model);
            JScrollPane sp = new JScrollPane(table);

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            south.add(btnBaja);

            btnAlta.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtId.getText().trim());
                    String nombre = txtNombre.getText().trim();
                    String categoria = txtCategoria.getText().trim();
                    int cant = Integer.parseInt(txtCantidad.getText().trim());

                    if (nombre.isEmpty() || categoria.isEmpty() || cant < 0) {
                        JOptionPane.showMessageDialog(this, "Nombre y categoría no vacíos; cantidad >= 0");
                        return;
                    }

                    boolean ok = sistema.altaProducto(id, nombre, categoria, cant);
                    if (!ok) JOptionPane.showMessageDialog(this, "ID ya existe.");
                    refrescar();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID y Cantidad deben ser números.");
                }
            });

            btnActualizar.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtId.getText().trim());
                    String nombre = txtNombre.getText().trim();
                    String categoria = txtCategoria.getText().trim();
                    int cant = Integer.parseInt(txtCantidad.getText().trim());

                    if (nombre.isEmpty() || categoria.isEmpty() || cant < 0) {
                        JOptionPane.showMessageDialog(this, "Nombre y categoría no vacíos; cantidad >= 0");
                        return;
                    }

                    boolean ok = sistema.actualizarProducto(id, nombre, categoria, cant);
                    if (!ok) JOptionPane.showMessageDialog(this, "Producto no existe.");
                    refrescar();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID y Cantidad deben ser números.");
                }
            });

            btnBaja.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtId.getText().trim());
                    boolean ok = sistema.bajaProducto(id);
                    if (!ok) JOptionPane.showMessageDialog(this, "Producto no existe.");
                    refrescar();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID debe ser número.");
                }
            });

            add(form, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);

            refrescar();
        }

        private void refrescar() {
            model.setRowCount(0);
            for (InventarioBST.Producto p : sistema.getInventario().enOrden()) {
                model.addRow(new Object[]{p.getId(), p.getNombre(), p.getCategoria(), p.getCantidad()});
            }
        }
    }

    // ==========================
    // PANEL HISTORIAL (IGUAL)
    // ==========================
    private class PanelHistorial extends JPanel {
        private ListaDoblementeEnlazada.Nodo<Operacion> cursor;
        private final DefaultTableModel model;

        public PanelHistorial() {
            setLayout(new BorderLayout(10, 10));

            model = new DefaultTableModel(new Object[]{"Fecha","Tipo","ID","Producto","Categoría","Cantidad","Obs"}, 0);
            JTable table = new JTable(model);
            JScrollPane sp = new JScrollPane(table);

            JButton btnPrimero = new JButton("<< Primero");
            JButton btnAnterior = new JButton("< Anterior");
            JButton btnSiguiente = new JButton("Siguiente >");
            JButton btnUltimo = new JButton("Último >>");
            JButton btnRefrescar = new JButton("Refrescar");

            JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER));
            nav.add(btnPrimero);
            nav.add(btnAnterior);
            nav.add(btnSiguiente);
            nav.add(btnUltimo);
            nav.add(btnRefrescar);

            btnRefrescar.addActionListener(e -> {
                cargarTablaCompleta();
                cursor = sistema.getHistorial().getInicio();
            });

            btnPrimero.addActionListener(e -> { cursor = sistema.getHistorial().getInicio(); mostrarCursor(); });
            btnUltimo.addActionListener(e -> { cursor = sistema.getHistorial().getFin(); mostrarCursor(); });

            btnAnterior.addActionListener(e -> {
                if (cursor != null) cursor = cursor.getAnt();
                mostrarCursor();
            });

            btnSiguiente.addActionListener(e -> {
                if (cursor != null) cursor = cursor.getSgt();
                mostrarCursor();
            });

            add(sp, BorderLayout.CENTER);
            add(nav, BorderLayout.SOUTH);

            cargarTablaCompleta();
            cursor = sistema.getHistorial().getInicio();
        }

        private void cargarTablaCompleta() {
            model.setRowCount(0);
            ListaDoblementeEnlazada.Nodo<Operacion> it = sistema.getHistorial().getInicio();
            while (it != null) {
                Operacion op = it.getDato();
                model.addRow(new Object[]{
                        op.getFecha(), op.getTipo(), op.getProductoId(),
                        op.getProductoNombre(), op.getProductoCategoria(),
                        op.getCantidad(), op.getObservacion()
                });
                it = it.getSgt();
            }
        }

        private void mostrarCursor() {
            if (cursor == null) return;
            Operacion op = cursor.getDato();
            JOptionPane.showMessageDialog(this,
                    "Registro:\n" +
                            "Fecha: " + op.getFecha() + "\n" +
                            "Tipo: " + op.getTipo() + "\n" +
                            "Producto: " + op.getProductoId() + " - " + op.getProductoNombre() + "\n" +
                            "Categoría: " + op.getProductoCategoria() + "\n" +
                            "Cantidad: " + op.getCantidad() + "\n" +
                            "Obs: " + op.getObservacion()
            );
        }
    }
}