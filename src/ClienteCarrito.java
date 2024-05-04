import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ClienteCarrito {
    static ArrayList<Producto> catalogo;
    static Carrito carrito = new Carrito();
    public static void main(String[] args) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriba la direccion del servidor: ");
            String host = br.readLine();
            System.out.print("\n\nEscriba el puerto: ");
            int pto=Integer.parseInt(br.readLine());
            Socket cl = new Socket(host,pto);

            recibirArchivo(cl);
            menu();

            //catalogo.listaProductos.add();

        }catch (Exception e){
            System.out.println(e);
        }

    }

    public static void menu() {
        Scanner scanner = new Scanner(System.in);
        int opcion;
        mostrarProductos();
        do {
            System.out.println("\nSelecciona la opcion que deseas realizar:");
            System.out.println("1.- Mostrar productos");
            System.out.println("2.- Agregar un producto al carrito");
            System.out.println("3.- Eliminar un producto");
            System.out.println("4.- Editar un producto");
            System.out.println("5.- Comprar producto");
            System.out.println("6.- Mostrar mi carrito");
            System.out.println("10.- Salir");

            System.out.print("Ingresa tu opción: ");
            opcion = scanner.nextInt();
            switch (opcion) {
                case 1:
                    mostrarProductos();
                    break;
                case 2:
                    agregarProductoCarrito();
                    break;
                case 3:
                    eliminarProducto();
                    break;
                case 4:
                    editarProducto();
                    break;
                case 5:
                    comprarProducto();
                    break;
                case 6:
                    carrito.mostrarCarrito();
                    break;    
                case 10:
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    break;
                default:
                    System.out.println("Opción no válida. Por favor ingresa una opción entre 1 y 6.");
            }
            
        } while (opcion != 10);
    }


    private static void agregarProductoCarrito() {
        Scanner s = new Scanner(System.in);
        System.out.println("Ingrese el nombre del producto a agregar");
        String nProducto = s.nextLine();
        Producto producto = buscarProducto(nProducto);
        if (producto == null) {
            System.out.println("Producto no encontrado");
        }else{
            carrito.agregarProducto(producto);
            System.out.println(producto);
            System.out.println("Producto agregado correctamente");
        }
    }

    public static void mostrarProductos() {
        System.out.println("Mostrando productos...");
        try{
            //Deserializar
            FileInputStream fileIn = new FileInputStream("src/Cliente/catalogo.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            catalogo = (ArrayList<Producto>) in.readObject();
            in.close();
            fileIn.close();

            System.out.println("El catalogo es el siguiente: ");
            for (Producto producto: catalogo) {
                producto.imprimirDatos();
            }
        }catch(Exception e){
            System.out.println("Error al mostrar productos: "+e);
        }
    }

    public static void agregarProducto() {
        Scanner ap = new Scanner(System.in);
        System.out.println("Agregando producto...");
        System.out.println("Ingrese el nombre del producto");
        String pNombre = ap.nextLine();
        System.out.println("Ingrese el stock del producto");
        int pStock = ap.nextInt();
        System.out.println("Ingrese el precio del producto");
        int pPrecio = ap.nextInt();
        Producto nProducto = new Producto(pNombre, pStock, pPrecio);
        catalogo.add(nProducto);
        actualizarCatalogo();
        System.out.println("Producto agregado correctamente");
        mostrarProductos();
    }

    private static void actualizarCatalogo() {
        try{
        // Serializando el ArrayList
        FileOutputStream fileOut = new FileOutputStream("src/Cliente/catalogo.txt");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(catalogo);
        out.close();
        fileOut.close();
        System.out.println("Catalogo actualizado catalogo.txt");
        } catch (Exception i) {
            System.out.println(i);
        }
    }

    public static void eliminarProducto() {
        System.out.println("Ingrese el nombre");
    }

    public static void editarProducto() {
        System.out.println("Editando producto...");
        // Código para editar un producto
    }

    public static void comprarProducto() {
        System.out.println("Comprando producto...");
        // Código para comprar un producto
    }

    public static String recibirArchivo(Socket cl) {
        String nombre="";//Nombre del archivo
        try {
            //Definimos flujo de entrada orientado a bits ligado al socket
            DataInputStream dis=new DataInputStream(cl.getInputStream());

            //Definimos flujo de entrada orientado a bits ligado al socket
            //Leemos los datos del archivo recibido en bloques de 1024
            byte[] b = new byte[1024];
            nombre = dis.readUTF();
            System.out.print("\nRecibimos el archivo: "+nombre);
            long tam = dis.readLong();

            //Creamos flujo para escribir el archivo de salida
            BufferedOutputStream dos=new BufferedOutputStream(new FileOutputStream("src/Cliente/catalogo.txt"));
            //Preparamos los datos para recibir los
            long recibidos=0;
            int n,porcentaje;
            //Definimos un ciclo donde estaremos recibiendo
            while(recibidos<tam){
                n = dis.read(b, 0, Math.min(b.length, (int)(tam - recibidos)));
                if (n == -1) break; // Si llegamos al final del archivo
                dos.write(b, 0, n);
                dos.flush();
                recibidos += n;
                porcentaje = (int) ((recibidos * 100) / tam);
                System.out.println("\nDescarga del catalogo: " + porcentaje + "%");
            }//While
            //Cerramos los flujos de entrada y salida, asi como el socket
            dos.close();
            dis.close();
        }catch (Exception e){
            System.out.println("Error: "+ e);
        }
        return nombre;
    }
    public static Producto buscarProducto(String nProducto){
        Producto pBuscado = null;
        for (Producto producto : catalogo) {
            if (producto.getNombre().equals(nProducto)) {
                pBuscado = producto;
                break;
            }
        }
        return pBuscado;
    }
}


