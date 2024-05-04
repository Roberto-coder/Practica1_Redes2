import java.util.ArrayList;

public class Carrito {
    ArrayList<Producto> productos = new ArrayList<>();
    int subtotales;
    int total;
    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }
    public void mostrarCarrito(){
        if (productos.size() == 0){
            System.out.println("No hay productos aun en el carrito");
        }else{
            for (Producto producto : productos) {
                System.out.println("-------------------------------------------------------------------");
                System.out.println("Nombre: " + producto.getNombre());
                System.out.println("Stock: " + producto.getStock());
                System.out.println("Precio: $" + producto.getPrecio());
                System.out.println("-------------------------------------------------------------------");
            }
        }
        
    }
}
