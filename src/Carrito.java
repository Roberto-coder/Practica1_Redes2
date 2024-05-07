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

    public void limpiarCarrito(){
        detalleCarritos.clear();
    }

    public void borrarProducto(String nombre){
        boolean verificador=false;
        for (int i = 0; i < detalleCarritos.size(); i++) {
            if (nombre.equals(detalleCarritos.get(i).getProducto().getNombre())){
                detalleCarritos.remove(i);
                verificador=true;
            }
        }
        if (verificador){
            System.out.println("Producto:"+nombre+" eliminado correctamente");
        }else {
            System.out.println("Producto no encontrado");
        }
    }
}
