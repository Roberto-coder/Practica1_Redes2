import java.util.ArrayList;

public class Carrito {
    ArrayList<DetalleCarrito> detalleCarritos = new ArrayList<>();
    int total;
    public void agregarProducto(Producto producto, int cantidad) {
        detalleCarritos.add(new DetalleCarrito(producto, cantidad));
    }
    public void mostrarCarrito(){
        if (detalleCarritos.size() == 0){
            System.out.println("No hay productos aun en el carrito");
        }else{
            total = 0;
            for (DetalleCarrito detalleCarrito : detalleCarritos) {
                System.out.println("-------------------------------------------------------------------");
                System.out.println("Nombre: " + detalleCarrito.producto.getNombre());
                System.out.println("Cantidad: " + detalleCarrito.cantidad);
                System.out.println("Precio: $" + detalleCarrito.producto.getPrecio());
                System.out.println("Subtotal: $" + detalleCarrito.subtotal);
                System.out.println("-------------------------------------------------------------------");
                total += detalleCarrito.subtotal;
            }
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Total: $" + total);
        }
        
    }
}
