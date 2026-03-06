package proyecto_final;

import javax.swing.*;//libreria para la interfaz gráfica
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MainFrame extends JFrame {//jFRame crea ventanas principales con interfaz gráfica

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
            //indica que se a insertado correctamente en el invetario
            if (ok) historial.agregarFinal(new Operacion(Operacion.TipoOperacion.INGRESO,
            		id, nombre, categoria, cantidad,"Alta de producto"));
            return ok;
        }

        public boolean bajaProducto(int id) {
            InventarioBST.Producto p = inventario.buscar(id);
            if (p == null) return false;
            boolean ok = inventario.eliminar(id);
            if (ok) historial.agregarFinal(new Operacion(
                    Operacion.TipoOperacion.SALIDA,
                    id, p.getNombre(), p.getCategoria(), p.getCantidad(),
                    "Baja de producto"));
            return ok;
        }

        public boolean actualizarProducto(int id, String nombre, String categoria, int cantidad) {
            boolean ok = inventario.actualizar(id, nombre, cantidad, categoria);
            if (ok) historial.agregarFinal(new Operacion(Operacion.TipoOperacion.INGRESO,
                    id, nombre, categoria, cantidad,"Actualización"));
            return ok;
        }
    }

    private final SistemaLogistica sistema;

    public MainFrame() {//diseño de la ventana general
        super("Proyecto Final - Logística (Compacto)");

        this.sistema = new SistemaLogistica();

        JTabbedPane tabs = new JTabbedPane(); // es un conjunto de paneles
        //cada uno de estos es un panle o pestaña
        tabs.addTab("Red (Grafo)", new PanelRed());//añade el nombre del panel y el contenido
        tabs.addTab("Inventario (BST)", new PanelInventario());
        tabs.addTab("Historial (Lista Doble)", new PanelHistorial());

        setLayout(new BorderLayout());// forma de organizarce los elementos dentro de la ventana
        add(tabs, BorderLayout.CENTER);//que se ubique en l centro y escala equivalentemente

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//finaliza la aplicación, cierra la ventana
        setSize(950, 650); //ancho y largo de la ventana en pixeles
        setLocationRelativeTo(null);//para que emerga en el centro la ventana
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        //Ejecutar en el hilo gráfico:crear la ventana principal y mostrarla
    }

    private class PanelRed extends JPanel {

        private final JTextArea areaConexiones; //área de textos
        private final JComboBox<String> comboOrigen;
        private final JComboBox<String> comboDestino;

        public PanelRed() {
            setLayout(new BorderLayout(10, 10));//espacio entre los componenetes(horizontal,verti)

            JPanel contenedor = new JPanel();
            contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));//organiza elementos verticales

            areaConexiones = new JTextArea(8, 50);//(alto por ancho) 8 fila 50 colmunas o caracteres
            areaConexiones.setEditable(false);// el ususuario no lo modifica el área
            JScrollPane scrollConexiones = new JScrollPane(areaConexiones);//barra de desplazamiento
            scrollConexiones.setBorder(BorderFactory.createTitledBorder("Conexiones Directas"));
            //limita el contenido a un borde que lo rodea

            JPanel panelNodo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            //organiza los componentes como palabras en una linea y los aline a la izq.
            panelNodo.setBorder(BorderFactory.createTitledBorder("Gestión de Nodos"));
            //crea una borde con el nombre de ahi en el panel

            JTextField txtNodo = new JTextField(10);//crea un campo de texo para ingresar nodos
            //con 10 columnsa de ancho
            JButton btnAgregarNodo = new JButton("Agregar Nodo");//boton de agregar
            JButton btnEliminarNodo = new JButton("Eliminar Nodo");//botón de elimar

            panelNodo.add(new JLabel("Nodo:"));//crea un texto especificado que se alinea al borde inicial
            panelNodo.add(txtNodo);
            panelNodo.add(btnAgregarNodo);
            panelNodo.add(btnEliminarNodo);

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

            JPanel panelCalculo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            panelCalculo.setBorder(BorderFactory.createTitledBorder("Ruta Más Corta (Dijkstra)"));

            JButton btnCalcular = new JButton("Calcular");
            JTextArea resultado = new JTextArea(4, 60);
            resultado.setEditable(false);

            panelCalculo.add(btnCalcular);
            panelCalculo.add(new JScrollPane(resultado));

            btnAgregarNodo.addActionListener(e -> {//en agregar nodo se hace lo siguiente,e detonante
                String nombre = txtNodo.getText().trim();
                if (nombre.isEmpty()) return;

                boolean ok = sistema.getGrafo().agregarNodo(nombre);//true se agrego correcta
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "El nodo ya existe o es inválido.");
                }
                txtNodo.setText("");//limpia el cuadro de texto para ingresar otro nodo
                refrescarTodo();//metodo interno que actualiza el grafo
            });
            //getText sirve para mostar lo que el usuarui escribio
            btnEliminarNodo.addActionListener(e -> {String nombre = txtNodo.getText().trim();
                if (nombre.isEmpty()) return;
                boolean ok = sistema.getGrafo().eliminarNodo(nombre);//true se elimino correcto
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "El nodo no existe.");
                }
                txtNodo.setText(""); //limpia el campo
                refrescarTodo();
            });
            
            btnAgregarRuta.addActionListener(e -> {
                try {
                    if (comboOrigen.getSelectedItem() == null || comboDestino.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(this, "No hay nodos suficientes.");
                        return;
                    }

                    String o = comboOrigen.getSelectedItem().toString();
                    String d = comboDestino.getSelectedItem().toString();
                    int dist = Integer.parseInt(txtDistancia.getText().trim());
                    //ontienen el texto de la distancia en forma de número

                    if (o.equals(d)) {
                        JOptionPane.showMessageDialog(this, "No puede conectar un nodo consigo mismo.");
                        return;
                    }
                    if (dist <= 0) {
                        JOptionPane.showMessageDialog(this, "La distancia debe ser > 0.");
                        return;
                    }

                    boolean ok = sistema.getGrafo().agregarArista(o, d, dist);// true si se crea un arista correctamente
                    if (!ok) JOptionPane.showMessageDialog(this, "Error al agregar ruta (verifique nodos).");

                    txtDistancia.setText("");//limpia el campo de distancia
                    refrescarTodo();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Distancia inválida.");
                    //ocurre cuando la distanci no es número
                }
            });
            btnEliminarRuta.addActionListener(e -> {
                if (comboOrigen.getSelectedItem() == null || comboDestino.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "No hay nodos suficientes.");
                    return;
                }

                String o = comboOrigen.getSelectedItem().toString();
                String d = comboDestino.getSelectedItem().toString();

                boolean ok = sistema.getGrafo().eliminarArista(o, d);//true si se elimina correcto
                if (!ok) JOptionPane.showMessageDialog(this, "La ruta no existe.");
                refrescarTodo();
            });

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
            //los contenedores añaden los paneles que van aun panel grande(red de suministros)
            contenedor.add(scrollConexiones);
            contenedor.add(panelNodo);
            contenedor.add(panelRuta);
            contenedor.add(panelCalculo);

            add(contenedor, BorderLayout.CENTER);//y lo ubica en el centro
            refrescarTodo();
        }

        private void refrescarTodo() {
            areaConexiones.setText(sistema.getGrafo().mostrarConexiones());
            actualizarCombos();
        }

        private void actualizarCombos() {
            String selO = (comboOrigen.getSelectedItem() != null) ? comboOrigen.getSelectedItem().toString() : null;
            String selD = (comboDestino.getSelectedItem() != null) ? comboDestino.getSelectedItem().toString() : null;
            //limpia los elementos de la caja los deja vacío
            comboOrigen.removeAllItems();
            comboDestino.removeAllItems();

            for (String nodo : sistema.getGrafo().getNombresNodos()) {//recorre todos los nodos
                comboOrigen.addItem(nodo);//añade nodos en treeSet incluyendo a los nuevos nds
                comboDestino.addItem(nodo);
            }
            // intentar restaurar selección, verifica que exista el nodo ingresado
            if (selO != null) comboOrigen.setSelectedItem(selO);
            if (selD != null) comboDestino.setSelectedItem(selD);
        }
    }

    private class PanelInventario extends JPanel {
        private final DefaultTableModel model;//modelo de datos deuna tabla

        public PanelInventario() {
            setLayout(new BorderLayout(10, 10));

            JPanel form = new JPanel(new GridLayout(2, 5, 10, 10));
            //organiza datos en una tabla (fila, columna, espacio Hr, esapcio vrt)

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
          //organiza los componentes como palabras en una linea y los aline a la derecha
            south.add(btnBaja);

            btnAlta.addActionListener(e -> {
                try {
                    int id = Integer.parseInt(txtId.getText().trim());
                    //convierte anumero el contendio de txtId
                    String nombre = txtNombre.getText().trim();
                    String categoria = txtCategoria.getText().trim();
                    int cant = Integer.parseInt(txtCantidad.getText().trim());

                    if (nombre.isEmpty() || categoria.isEmpty() || cant < 0) {
                        JOptionPane.showMessageDialog(this, "Nombre y categoría no vacíos; cantidad >= 0");
                        return;
                    }

                    boolean ok = sistema.altaProducto(id, nombre, categoria, cant);//true si se agrega correctamente
                    if (!ok) JOptionPane.showMessageDialog(this, "ID ya existe.");
                    refrescar();

                } catch (NumberFormatException ex) {//si lo ingresado no es numero
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
            model.setRowCount(0);//borra todas las filas de la tabal
            ListaDoblementeEnlazada.Nodo<Operacion> it = sistema.getHistorial().getInicio();
            while (it != null) {
                Operacion op = it.getDato();
                model.addRow(new Object[]{
                        op.getFecha(), op.getTipo(), op.getProductoId(),
                        op.getProductoNombre(), op.getProductoCategoria(),
                        op.getCantidad(), op.getObservacion()
                });
                it = it.getSgt();
            }//al final añade otra vez todo considerando las nuevas acualizaciones
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
                            "Obs: " + op.getObservacion());
        }//para mostrar en especifico una ventana 
    }
}