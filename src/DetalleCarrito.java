public class DetalleCarrito {
    Producto producto;
    int cantidad;
    double subtotal;
    public DetalleCarrito(Producto producto, int cantidad){
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.precio * cantidad;
    }
    public void actualizarSubtotal(){
        this.subtotal=producto.precio * cantidad;
    }
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
