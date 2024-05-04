public class DetalleCarrito {
    Producto producto;
    int cantidad;
    double subtotal;
    public DetalleCarrito(Producto producto, int cantidad){
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.precio * cantidad;
    }
}
