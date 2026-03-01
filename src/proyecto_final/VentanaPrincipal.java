package proyecto_final;

import proyecto_final.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private Grafo grafo = new Grafo();
    private ArbolBST arbol = new ArbolBST();
    private ListaDoble historial = new ListaDoble();

    private JTextArea areaHistorial = new JTextArea();

    public VentanaPrincipal() {

        setTitle("Sistema de Suministros");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarDatos();

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Rutas", panelRutas());
        tabs.add("Inventario", panelInventario());
        tabs.add("Historial", panelHistorial());

        add(tabs);
    }

    private void inicializarDatos() {
        grafo.agregarNodo("A");
        grafo.agregarNodo("B");
        grafo.agregarNodo("C");
        /*grafo.agregarArista("A","B",5);
        grafo.agregarArista("B","C",3);
        grafo.agregarArista("A","C",10);*/
    }

    // ==========================
    // PANEL RUTAS
    // ==========================
    private JPanel panelRutas() {

        JPanel p = new JPanel(new BorderLayout());

        JPanel contenedor = new JPanel();
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        // =============================
        // AREA PARA MOSTRAR CONEXIONES
        // =============================
        JTextArea areaConexiones = new JTextArea(8, 50);
        areaConexiones.setEditable(false);
        areaConexiones.setText(grafo.mostrarConexiones());

        JScrollPane scrollConexiones = new JScrollPane(areaConexiones);
        scrollConexiones.setBorder(BorderFactory.createTitledBorder("Conexiones Directas"));

        // =============================
        // PANEL NODOS
        // =============================
        JPanel panelNodo = new JPanel();
        JTextField txtNodo = new JTextField(10);
        JButton btnAgregarNodo = new JButton("Agregar Nodo");
        JButton btnEliminarNodo = new JButton("Eliminar Nodo");

        panelNodo.setBorder(BorderFactory.createTitledBorder("Gestión de Nodos"));
        panelNodo.add(new JLabel("Nodo:"));
        panelNodo.add(txtNodo);
        panelNodo.add(btnAgregarNodo);
        panelNodo.add(btnEliminarNodo);

        // =============================
        // PANEL RUTAS
        // =============================
        JPanel panelRuta = new JPanel();
        panelRuta.setBorder(BorderFactory.createTitledBorder("Gestión de Rutas"));

        JComboBox<String> origen = new JComboBox<>();
        JComboBox<String> destino = new JComboBox<>();
        JTextField txtDistancia = new JTextField(5);

        actualizarCombos(origen, destino);

        JButton btnAgregarRuta = new JButton("Agregar Ruta");
        JButton btnEliminarRuta = new JButton("Eliminar Ruta");

        panelRuta.add(new JLabel("Origen"));
        panelRuta.add(origen);
        panelRuta.add(new JLabel("Destino"));
        panelRuta.add(destino);
        panelRuta.add(new JLabel("Distancia"));
        panelRuta.add(txtDistancia);
        panelRuta.add(btnAgregarRuta);
        panelRuta.add(btnEliminarRuta);

        // =============================
        // PANEL CALCULAR RUTA
        // =============================
        JPanel panelCalculo = new JPanel();
        panelCalculo.setBorder(BorderFactory.createTitledBorder("Ruta Más Corta"));

        JButton btnCalcular = new JButton("Calcular");
        JTextArea resultado = new JTextArea(4, 50);
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

            if (!grafo.agregarNodo(nombre)) {
                JOptionPane.showMessageDialog(this, "El nodo ya existe.");
            } else {
                historial.agregar("Nodo agregado: " + nombre);
                actualizarCombos(origen, destino);
                areaConexiones.setText(grafo.mostrarConexiones());
            }
        });

        // ELIMINAR NODO
        btnEliminarNodo.addActionListener(e -> {
            String nombre = txtNodo.getText().trim();

            if (!grafo.eliminarNodo(nombre)) {
                JOptionPane.showMessageDialog(this, "El nodo no existe.");
            } else {
                historial.agregar("Nodo eliminado: " + nombre);
                actualizarCombos(origen, destino);
                areaConexiones.setText(grafo.mostrarConexiones());
            }
        });

        // AGREGAR RUTA
        btnAgregarRuta.addActionListener(e -> {
            try {
                String o = origen.getSelectedItem().toString();
                String d = destino.getSelectedItem().toString();
                int dist = Integer.parseInt(txtDistancia.getText());

                if (o.equals(d)) {
                    JOptionPane.showMessageDialog(this, "No puede conectar un nodo consigo mismo.");
                    return;
                }

                if (!grafo.agregarArista(o, d, dist)) {
                    JOptionPane.showMessageDialog(this, "Error al agregar ruta.");
                } else {
                    historial.agregar("Ruta agregada: " + o + " - " + d + " (" + dist + ")");
                    areaConexiones.setText(grafo.mostrarConexiones());
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Distancia inválida.");
            }
        });

        // ELIMINAR RUTA
        btnEliminarRuta.addActionListener(e -> {
            String o = origen.getSelectedItem().toString();
            String d = destino.getSelectedItem().toString();

            if (!grafo.eliminarArista(o, d)) {
                JOptionPane.showMessageDialog(this, "La ruta no existe.");
            } else {
                historial.agregar("Ruta eliminada: " + o + " - " + d);
                areaConexiones.setText(grafo.mostrarConexiones());
            }
        });

        // CALCULAR MEJOR RUTA
        btnCalcular.addActionListener(e -> {
            String o = origen.getSelectedItem().toString();
            String d = destino.getSelectedItem().toString();

            String resultadoRuta = grafo.rutaMasCorta(o, d);
            resultado.setText(resultadoRuta);

            historial.agregar("Ruta calculada entre: " + o + " y " + d);
            areaHistorial.setText(historial.mostrar());
        });

        // =============================
        // AGREGAR TODO AL CONTENEDOR
        // =============================
        contenedor.add(scrollConexiones);
        contenedor.add(panelNodo);
        contenedor.add(panelRuta);
        contenedor.add(panelCalculo);

        p.add(contenedor, BorderLayout.CENTER);

        return p;
    }
    
    private void actualizarCombos(JComboBox<String> origen, JComboBox<String> destino) {
        origen.removeAllItems();
        destino.removeAllItems();

        for (String nodo : grafo.getNodos()) {
            origen.addItem(nodo);
            destino.addItem(nodo);
        }
    }

    // ==========================
    // PANEL INVENTARIO
    // ==========================
    private JPanel panelInventario() {

        JPanel p = new JPanel();

        JTextField sku = new JTextField(5);
        JTextField nombre = new JTextField(8);
        JTextField cantidad = new JTextField(5);

        JButton agregar = new JButton("Agregar");
        JButton eliminar = new JButton("Eliminar por SKU");

        JTextArea lista = new JTextArea(8,30);
        lista.setEditable(false);

        // AGREGAR PRODUCTO
        agregar.addActionListener(e -> {
            try {
                int skuVal = Integer.parseInt(sku.getText());
                int cantVal = Integer.parseInt(cantidad.getText());

                Producto prod = new Producto(
                        skuVal,
                        nombre.getText(),
                        cantVal,
                        "General"
                );

                boolean insertado = arbol.insertar(prod);

                if (!insertado) {
                    JOptionPane.showMessageDialog(this,
                            "El SKU ya existe.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                historial.agregar("Producto agregado: " + nombre.getText());
                actualizarLista(lista);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "SKU y Cantidad deben ser numéricos.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // ELIMINAR PRODUCTO
        eliminar.addActionListener(e -> {
            try {
                int skuVal = Integer.parseInt(sku.getText());

                boolean eliminado = arbol.eliminar(skuVal);

                if (!eliminado) {
                    JOptionPane.showMessageDialog(this,
                            "Producto no encontrado.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    historial.agregar("Producto eliminado (SKU): " + skuVal);
                    actualizarLista(lista);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ingrese un SKU válido.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        p.add(new JLabel("SKU"));
        p.add(sku);
        p.add(new JLabel("Nombre"));
        p.add(nombre);
        p.add(new JLabel("Cantidad"));
        p.add(cantidad);
        p.add(agregar);
        p.add(eliminar);
        p.add(new JScrollPane(lista));

        return p;
    }

    private void actualizarLista(JTextArea lista) {
        lista.setText("");
        for (Producto p1 : arbol.inOrden()) {
            lista.append(p1.getSku() + " - " +
                    p1.getNombre() + " - Cant: " +
                    p1.getCantidad() + "\n");
        }
        areaHistorial.setText(historial.mostrar());
    }

    // ==========================
    // PANEL HISTORIAL
    // ==========================
    private JPanel panelHistorial() {
        JPanel p = new JPanel(new BorderLayout());
        areaHistorial.setEditable(false);
        p.add(new JScrollPane(areaHistorial), BorderLayout.CENTER);
        return p;
    }
}