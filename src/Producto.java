import java.io.Serializable;

public class Producto implements Serializable {

    String nombre;
    int stock;
    double precio;

    public Producto(String nombre, int stock, double precio) {
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void imprimirDatos(){
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Nombre: " + getNombre());
        System.out.println("Stock: " + getStock());
        System.out.println("Precio: $" + getPrecio());
        System.out.println("-------------------------------------------------------------------");

    }

    public void actualizarStock(int cantidad){
        setStock(this.stock-cantidad);
    }

}
