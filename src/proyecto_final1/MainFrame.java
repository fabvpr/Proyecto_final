package proyecto_final1;

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
        tabs.addTab("Red (Grafo)", new PanelRed());
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

    private class PanelRed extends JPanel {

        private final JTextArea area;

        public PanelRed() {
            setLayout(new BorderLayout(10, 10));

            JPanel contenedor = new JPanel();
            contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

            // ========================
            // SECCION AGREGAR NODO
            // ========================
            JPanel panelNodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtNodo = new JTextField(8);
            JButton btnAgregarNodo = new JButton("Agregar Nodo");

            panelNodo.setBorder(BorderFactory.createTitledBorder("1) Agregar Nodo (letra/nombre)"));
            panelNodo.add(new JLabel("Nodo:"));
            panelNodo.add(txtNodo);
            panelNodo.add(btnAgregarNodo);

            // ========================
            // SECCION CONECTAR NODOS
            // ========================
            JPanel panelConexion = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtOrigen = new JTextField(8);
            JTextField txtDestino = new JTextField(8);
            JTextField txtPeso = new JTextField(6);
            JButton btnConectar = new JButton("Conectar");

            panelConexion.setBorder(BorderFactory.createTitledBorder("2) Crear Conexión (arista)"));
            panelConexion.add(new JLabel("Origen:"));
            panelConexion.add(txtOrigen);
            panelConexion.add(new JLabel("Destino:"));
            panelConexion.add(txtDestino);
            panelConexion.add(new JLabel("Peso:"));
            panelConexion.add(txtPeso);
            panelConexion.add(btnConectar);

            // ========================
            // SECCION VALIDAR CONEXION DIRECTA
            // ========================
            JPanel panelCheck = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtC1 = new JTextField(8);
            JTextField txtC2 = new JTextField(8);
            JButton btnCheck = new JButton("Verificar Conexión");

            panelCheck.setBorder(BorderFactory.createTitledBorder("3) Verificar si existe conexión directa"));
            panelCheck.add(new JLabel("Nodo 1:"));
            panelCheck.add(txtC1);
            panelCheck.add(new JLabel("Nodo 2:"));
            panelCheck.add(txtC2);
            panelCheck.add(btnCheck);

            // ========================
            // SECCION DIJKSTRA (DENTRO DEL GRAFO)
            // ========================
            JPanel panelRuta = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField txtInicio = new JTextField(8);
            JTextField txtFin = new JTextField(8);
            JButton btnBuscar = new JButton("Buscar Ruta Óptima");

            panelRuta.setBorder(BorderFactory.createTitledBorder("4) Ruta Óptima (Dijkstra)"));
            panelRuta.add(new JLabel("Inicio:"));
            panelRuta.add(txtInicio);
            panelRuta.add(new JLabel("Fin:"));
            panelRuta.add(txtFin);
            panelRuta.add(btnBuscar);

            // ========================
            // AREA DE SALIDA
            // ========================
            area = new JTextArea();
            area.setEditable(false);
            JScrollPane sp = new JScrollPane(area);

            // ========================
            // EVENTOS
            // ========================

            btnAgregarNodo.addActionListener(e -> {
                String nombre = txtNodo.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese un nombre de nodo (ej: A).");
                    return;
                }

                boolean ok = sistema.getGrafo().agregarNodo(nombre);
                if (!ok) JOptionPane.showMessageDialog(this, "El nodo ya existe o es inválido.");

                txtNodo.setText("");
                refrescar();
            });

            btnConectar.addActionListener(e -> {
                try {
                    String o = txtOrigen.getText().trim();
                    String d = txtDestino.getText().trim();
                    int w = Integer.parseInt(txtPeso.getText().trim());

                    if (o.isEmpty() || d.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Origen y Destino no pueden estar vacíos.");
                        return;
                    }
                    if (w <= 0) {
                        JOptionPane.showMessageDialog(this, "El peso debe ser > 0.");
                        return;
                    }

                    boolean ok = sistema.getGrafo().agregarArista(o, d, w);
                    if (!ok) JOptionPane.showMessageDialog(this, "No se pudo conectar: verifique nodos y peso.");

                    refrescar();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Peso inválido.");
                }
            });

            btnCheck.addActionListener(e -> {
                String a = txtC1.getText().trim();
                String b = txtC2.getText().trim();
                if (a.isEmpty() || b.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese ambos nodos.");
                    return;
                }
                if (!sistema.getGrafo().existeNodo(a) || !sistema.getGrafo().existeNodo(b)) {
                    JOptionPane.showMessageDialog(this, "Uno o ambos nodos no existen.");
                    return;
                }

                boolean existe = sistema.getGrafo().existeConexionDirecta(a, b);
                JOptionPane.showMessageDialog(this,
                        existe ? "Sí, existe conexión directa entre " + a.toUpperCase() + " y " + b.toUpperCase()
                               : "No existe conexión directa entre " + a.toUpperCase() + " y " + b.toUpperCase());
            });

            btnBuscar.addActionListener(e -> {
                String i = txtInicio.getText().trim();
                String f = txtFin.getText().trim();

                if (i.isEmpty() || f.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese inicio y fin.");
                    return;
                }

                GrafoPesado.ResultadoRuta r = sistema.getGrafo().dijkstra(i, f);
                if (r.camino.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No existe ruta entre " + i.toUpperCase() + " y " + f.toUpperCase());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Costo mínimo: " + r.costo + "\nCamino: " + String.join(" -> ", r.camino));
                }
            });

            contenedor.add(panelNodo);
            contenedor.add(panelConexion);
            contenedor.add(panelCheck);
            contenedor.add(panelRuta);

            add(contenedor, BorderLayout.NORTH);
            add(sp, BorderLayout.CENTER);

            refrescar();
        }

        private void refrescar() {
            area.setText(sistema.getGrafo().mostrarComoTexto());
        }
    }

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