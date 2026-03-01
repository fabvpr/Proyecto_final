package proyecto_final;

public class Producto {
    private int sku;
    private String nombre;
    private int cantidad;
    private String categoria;

    public Producto(int sku, String nombre, int cantidad, String categoria) {
        this.sku = sku;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.categoria = categoria;
    }

    public int getSku() { return sku; }
    public String getNombre() { return nombre; }
    public int getCantidad() { return cantidad; }
    public String getCategoria() { return categoria; }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}