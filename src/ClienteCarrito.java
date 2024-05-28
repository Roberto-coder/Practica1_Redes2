import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ClienteCarrito {
    static ArrayList<Producto> catalogo;
    static Carrito carrito = new Carrito();
    static String folder = "Cliente/";
    public static void main(String[] args) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriba la direccion del servidor: ");
            String host = br.readLine();
            System.out.print("\n\nEscriba el puerto: ");
            int pto=Integer.parseInt(br.readLine());
            Socket cl = new Socket(host,pto);
            DataInputStream dis = new DataInputStream(cl.getInputStream());
            recibirDatos(cl, folder, dis);
            
            System.out.println("El socket esta cerrado? "+cl.isClosed());
            menu(cl);

            //catalogo.listaProductos.add();

        }catch (Exception e){
            System.out.println(e);
        }

    }

    public static void menu(Socket cl) {
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
                    try {
                        System.out.println("Saliendo del programa...");
                        //Regresa archivo al servidor
                        enviarArchivo(cl,folder+"catalogo.txt");
                        //Cierra el socket
                        cl.close();
                    }catch (Exception e){
                        System.out.println("Error:" + e);
                    }
                    break;
                default:
                    System.out.println("Opción no válida. Por favor ingresa una opción entre 1 y 6.");
            }
            
        } while (opcion != 10);
    }


    private static void agregarProductoCarrito() {
        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Ingrese el nombre del producto a agregar");
            String nProducto = s.nextLine();
            System.out.println("Ingrese la cantidad de unidades del producto a agregar");
            int cProducto = s.nextInt();
            Producto producto = buscarProducto(nProducto);
            if (producto == null) {
                System.out.println("Producto no encontrado");
            }else if(producto.stock<cProducto){
                System.out.println("Cantidad de productos seleccionados mayor a la existente");
            } else if (carrito.buscarProducto(producto.getNombre())) {
                System.out.println("El producto ya estaba en el carrito, porfavor editalo");
            } else{
                carrito.agregarProducto(producto, cProducto);
                System.out.println(producto);
                System.out.println("Producto agregado correctamente");
            }
        }catch (Exception e){
            System.out.println("Ingrese una cantidad valida");
            agregarProductoCarrito();
        }
    }

    public static void mostrarProductos() {
        System.out.println("Mostrando productos...");
        try{
            //Deserializar
            FileInputStream fileIn = new FileInputStream(folder +"catalogo.txt");
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
        try {
            Scanner s = new Scanner(System.in);
            System.out.println("Ingrese el nombre del producto a editar");
            String nProducto = s.nextLine();
            System.out.println("Ingrese la nueva cantidad del producto:");
            int cProducto = s.nextInt();
            Producto producto = buscarProducto(nProducto);//buscar en el catalogo
            if (producto.stock < cProducto) {
                System.out.println("Cantidad de productos seleccionados mayor a la existente");
            } else {
                carrito.editarProducto(nProducto, cProducto);
            }
        }catch (Exception e){
            System.out.println("Ingrese una cantidad valida");
            editarProducto();
        }
    }

    public static void comprarProducto() {

        if (!carrito.detalleCarritos.isEmpty()){
            for (Producto producto : catalogo) {
                for (int j = 0; j < carrito.detalleCarritos.size(); j++) {
                    if (producto.equals(carrito.detalleCarritos.get(j).getProducto())) {
                        System.out.println(producto.getNombre());
                        producto.actualizarStock(carrito.detalleCarritos.get(j).getCantidad());
                    }
                }
            }
            actualizarCatalogo();
            //carrito.generarTicket();
            carrito.limpiarCarrito();
        }else {
            System.out.println("Primero agrega articulos al carrito");
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
            //Enviamos un mensaje de aceptacion al servidor
            dos.writeUTF("SEND_FILE"); // Enviar comando al servidor
            dos.flush();
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

    private static void recibirDatos(Socket cl, String folder, DataInputStream dis) {
        try {
            // Recibe el archivo
            String fileName = dis.readUTF();
            long fileSize = dis.readLong();

            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(folder, fileName)))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                for (long bytesToRead = fileSize; bytesToRead > 0; ) {
                    int bytesToReadThisIteration = (int) Math.min(buffer.length, bytesToRead);
                    bytesRead = dis.read(buffer, 0, bytesToReadThisIteration);
                    if (bytesRead == -1) {
                        throw new EOFException("Fin de archivo inesperado al recibir el archivo: " + fileName);
                    }
                    bos.write(buffer, 0, bytesRead);
                    bytesToRead -= bytesRead;
                }
            }

            System.out.println("Archivo recibido y guardado en: " + folder);

            // Recibe las imágenes
            int numImages = dis.readInt();
            System.out.println("Número total de imágenes a recibir: " + numImages);

            for (int i = 0; i < numImages; i++) {
                String imageFileName = dis.readUTF();
                long imageFileSize = dis.readLong();
                System.out.println("Recibiendo imagen: " + imageFileName + ", Tamaño: " + imageFileSize + " bytes");

                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(folder, imageFileName)))) {
                    byte[] imageBuffer = new byte[1024];
                    int totalBytesRead = 0;
                    for (long bytesToRead = imageFileSize; bytesToRead > 0; ) {
                        int bytesToReadThisIteration = (int) Math.min(imageBuffer.length, bytesToRead);
                        int bytesRead = dis.read(imageBuffer, 0, bytesToReadThisIteration);
                        if (bytesRead == -1) {
                            throw new EOFException("Fin de archivo inesperado al recibir la imagen: " + imageFileName);
                        }
                        bos.write(imageBuffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        bytesToRead -= bytesRead;
                    }
                }

                System.out.println("Imagen recibida y guardada en: " + folder);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


