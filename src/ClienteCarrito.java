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
            String folder = "src/Cliente/imagenes/";
            recibirArchivo(cl);
            //recibirImagenes(cl,folder);
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
                    //Cierra el socket
                    //cl.close();
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
        System.out.println("Ingrese la cantidad de unidades del producto a agregar");
        int cProducto = s.nextInt();
        Producto producto = buscarProducto(nProducto);
        if (producto == null) {
            System.out.println("Producto no encontrado");
        }else{
            carrito.agregarProducto(producto, cProducto);
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
        Scanner s = new Scanner(System.in);
        System.out.println("Ingrese el nombre del producto a eliminar");
        String nProducto = s.nextLine();

        carrito.borrarProducto(nProducto);


    }

    public static void editarProducto() {
        System.out.println("Editando producto...");
        // Código para editar un producto
    }

    public static void comprarProducto() {
        /*for (DetalleCarrito objetos:carrito.detalleCarritos) {
            if(catalogo.contains(objetos.getProducto())){
                System.out.println(objetos.getProducto().nombre);
                catalogo
            }
        }*/
        for (int i = 0; i < catalogo.size(); i++) {
            for (int j = 0; j < carrito.detalleCarritos.size(); j++) {
                if(catalogo.get(i).equals(carrito.detalleCarritos.get(j).getProducto())){
                    System.out.println(catalogo.get(i).getNombre());
                    catalogo.get(i).actualizarStock(carrito.detalleCarritos.get(j).getCantidad());
                }
            }
        }
        actualizarCatalogo();
        carrito.limpiarCarrito();

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

    private static void recibirImagenes(Socket socket, String folder) {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

            // Recibimos la cantidad total de imágenes
            int totalImages = dataInputStream.readInt();
            System.out.println("Número total de imágenes a recibir: " + totalImages);

            // Creamos la carpeta de guardado si no existe
            File saveFolder = new File(folder);
            if (!saveFolder.exists()) {
                saveFolder.mkdirs();
                System.out.println("Carpeta de guardado creada en: " + folder);
            }

            for (int i = 0; i < totalImages; i++) {
                String fileName = dataInputStream.readUTF();
                long fileSize = dataInputStream.readLong();
                System.out.println("Recibiendo archivo: " + fileName + ", Tamaño: " + fileSize + " bytes");

                // Creamos un flujo para escribir el archivo recibido en la carpeta de guardado
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(saveFolder, fileName)));

                // Definimos un buffer para leer los datos del archivo en bloques de 1024 bytes
                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;

                // Leemos los datos del archivo y los escribimos en el archivo local
                while (totalBytesRead < fileSize && (bytesRead = dataInputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalBytesRead))) != -1) {
                    bufferedOutputStream.write(buffer, 0, bytesRead);
                    bufferedOutputStream.flush();
                    totalBytesRead += bytesRead;
                }

                // Cerramos el flujo para escribir el archivo
                bufferedOutputStream.close();

                System.out.println("Archivo recibido y guardado en: " + saveFolder.getAbsolutePath());
            }

            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void enviarArchivo(Socket cl, String filename) {
        File file = new File(filename);
        long tam=file.length();

        try {//Establecemos conexion con el servidor
            //Flujos de entrada y salida
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            //Enviamos el nombre y tamaño del archivo
            dos.writeUTF(filename);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            //Enviamos los datos obtenidos del archivo el bloques de 1024 bits
            byte[] b = new byte[1024];
            long enviados = 0;
            int porcentaje, n;
            while(enviados<tam){
                n = dis.read(b);
                dos.write(b,0,n);
                dos.flush();
                enviados=enviados+n;
                porcentaje=(int)(enviados*100/tam);
                System.out.print("Enviado:"+porcentaje+"%\r");
            }//While
            System.out.print("\n\nArchivo enviado al servidor");
            dos.close();//Cerramos flujo de salida
            dis.close();//Cerramos flujo de entrada

        } catch (Exception e) {
            System.out.println("Error al conectar con el servidor o al enviar el archivo: " + e.getMessage());
        }
    }


}


