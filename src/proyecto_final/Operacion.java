package proyecto_final;

import java.time.LocalDateTime;

public class Operacion {

    public enum TipoOperacion { INGRESO, SALIDA }

    private final LocalDateTime fecha = LocalDateTime.now();
    private final TipoOperacion tipo;
    private final int productoId;
    private final String productoNombre;
    private final String productoCategoria;
    private final int cantidad;
    private final String observacion;

    public Operacion(TipoOperacion tipo, int productoId, String productoNombre, String productoCategoria,
                     int cantidad, String observacion) {
        this.tipo = tipo;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoCategoria = productoCategoria;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public LocalDateTime getFecha() { return fecha; }
    public TipoOperacion getTipo() { return tipo; }
    public int getProductoId() { return productoId; }
    public String getProductoNombre() { return productoNombre; }
    public String getProductoCategoria() { return productoCategoria; }
    public int getCantidad() { return cantidad; }
    public String getObservacion() { return observacion; }
}