import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


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

    public void editarProducto(String nombre, int nCantidad){
        boolean verificador=false;
        for (int i = 0; i < detalleCarritos.size(); i++) {
            if (nombre.equals(detalleCarritos.get(i).getProducto().getNombre())){
                detalleCarritos.get(i).setCantidad(nCantidad);
                detalleCarritos.get(i).actualizarSubtotal();
                verificador=true;
            }
        }

        if (verificador){
            System.out.println("Producto:"+nombre+" editado correctamente");
        }else {
            System.out.println("Producto no encontrado");
        }
    }

    public boolean buscarProducto(String nombre){
        boolean verificador=false;
        for (int i = 0; i < detalleCarritos.size(); i++) {
            if (nombre.equals(detalleCarritos.get(i).getProducto().getNombre())){
                verificador=true;
            }
        }
        return verificador;
    }

    public void generarTicket() {
        Random random = new Random();
        String path;
        File file;

        do {
            // Generar un número aleatorio entre 1 y 100
            int randomNumber = random.nextInt(100) + 1;

            // Construir la ruta del archivo PDF
            path = "src/Cliente/ticket" + randomNumber + ".pdf";
            file = new File(path);

            // Si el archivo ya existe, el bucle se repite; si no, se sale del bucle
        } while (file.exists());

        try {
            // Crear un objeto PdfWriter
            PdfWriter writer = new PdfWriter(path);

            // Crear un objeto PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Crear un objeto Document
            Document document = new Document(pdf);

            // Añadir un párrafo
            document.add(new Paragraph("¡Hola, Gracias por tu compra! Este es tu ticket correspondiente a los articulos:"));

            //Añadir articulos
            String articulo="";
            for (DetalleCarrito detalleCarrito : detalleCarritos) {
                articulo= "\n-------------------------------------------------------------------"
                        +"\nNombre: " + detalleCarrito.producto.getNombre()
                        +"\nCantidad: " + detalleCarrito.cantidad
                        +"\nPrecio: $" + detalleCarrito.producto.getPrecio()
                        +"\nSubtotal: $" + detalleCarrito.subtotal
                        +"\n-------------------------------------------------------------------";
                total += detalleCarrito.subtotal;
                document.add(new Paragraph(articulo));
            }
            document.add(new Paragraph("\n-------------------------------------------------------------------"+
                    "\nTotal: $" + total));


            // Cerrar el documento
            document.close();

            System.out.println("PDF creado exitosamente: " + path);

        }catch (Exception e){
            System.out.println(e);
        }
    }
}
