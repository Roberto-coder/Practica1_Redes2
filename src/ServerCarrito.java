//Bibliotecas necesarias para enviar, recibir y socket

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class ServerCarrito {
    static ArrayList<Producto> productos= new ArrayList<>();
    public static void main(String[] args) {//Metodo main

        crearCatalogo();

        try {//Iniciamos proceso de recepcion de archivo serializado
            int port=6030;
            ServerSocket s = new ServerSocket(port);//Socket en el puerto 7000
            System.out.println("Servidor iniciado en el puerto " + port);
            //Iniciamos un ciclo infinito que estara esperando una conexion
            for (; ; ) {
                //Acepta la conexion del cliente
                Socket cl = s.accept();
                System.out.print("Conexión establecida desde "+cl.getInetAddress()+":"+cl.getPort());
                String IMAGE_FOLDER_PATH = "src/Servidor/imagenes/";

                enviarArchivo(cl,"src/Servidor/catalogo.txt");
                //enviarImagenes(cl,IMAGE_FOLDER_PATH );
            }
        } catch (Exception e) {
            e.printStackTrace();//cachamos la posible excepcion
        }

    }//main

    private static void enviarImagenes(Socket cl, String folder) {
        try {
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            File imageFolder = new File(folder);
            if (!imageFolder.exists() || !imageFolder.isDirectory()) {
                System.out.println("La ruta especificada no es una carpeta válida.");
                return;
            }

            File[] imageFiles = imageFolder.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png");
                }
            });
            // Enviamos la cantidad total de imágenes
            int numImages=imageFiles.length;
            dos.writeInt(numImages);
            dos.flush();

            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    long fileSize = imageFile.length();
                    DataInputStream dis = new DataInputStream(new FileInputStream(imageFile));

                        // Enviamos el nombre del archivo y su tamaño
                        dos.writeUTF(imageFile.getName());
                        dos.flush();
                        dos.writeLong(fileSize);
                        dos.flush();

                        // Enviamos los datos del archivo en bloques de 1024 bytes
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while((bytesRead =dis.read(buffer))!=-1){
                            dos.write(buffer, 0, bytesRead);
                        }
                        dos.flush();
                        dis.close();
                    System.out.println("Imagen Enviada: "+imageFile.getName());
                    }
            }

            dos.close();
            //os.close();
        }catch (Exception e){
            System.out.println("Error al enviar imagen: "+ e);
        }
    }

    private static void crearCatalogo() {
        productos.add(new Producto("Lethal Company",3,5));
        productos.add(new Producto("Ark",4,7));
        productos.add(new Producto("Fallout",8,6));

        try {
            // Serializando el ArrayList
            FileOutputStream fileOut = new FileOutputStream("src/Servidor/catalogo.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(productos);
            out.close();
            fileOut.close();
            System.out.println("Datos serializados guardados en catalogo.txt");

            // Deserializando el ArrayList
            FileInputStream fileIn = new FileInputStream("src/Servidor/catalogo.txt");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ArrayList<String> listDeserialized = (ArrayList<String>) in.readObject();
            in.close();
            fileIn.close();

            System.out.println("ArrayList deserializado: " + listDeserialized);
        } catch (Exception i) {
            System.out.println(i);
        }


    }

    //Funcion para enviar el archivo
    public static void enviarArchivo(Socket cl, String filename) {
        File file = new File(filename);
        long tam=file.length();

        try {//Establecemos conexion con el servidor
            //Flujos de entrada y salida
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            DataInputStream dis = new DataInputStream(Files.newInputStream(file.toPath()));
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
