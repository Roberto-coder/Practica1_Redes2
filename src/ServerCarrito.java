//Bibliotecas necesarias para enviar, recibir y socket

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerCarrito {
    static ArrayList<Producto> productos= new ArrayList<>();
    static String folder = "src/Servidor/imagenes/";
    static String archivo = "src/Servidor/catalogo.txt";
    public static void main(String[] args) {//Metodo main

        crearCatalogo();

        try {//Iniciamos proceso de recepcion de archivo serializado
            int port=6030;
            ServerSocket s = new ServerSocket(port);//Socket en el puerto 7000
            System.out.println("Servidor iniciado en el puerto " + port);
            //Iniciamos un ciclo infinito que estara esperando una conexion
            for ( ; ; ) {
                //Acepta la conexion del cliente
                Socket cl = s.accept();
                System.out.print("Conexión establecida desde "+cl.getInetAddress()+":"+cl.getPort());

                enviarDatos(cl,folder,archivo);

                //recibirArchivo(cl);
            }
        } catch (Exception e) {
            e.printStackTrace();//cachamos la posible excepcion
        }

    }//main



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

    private static void enviarDatos(Socket cl, String folder, String archivo) {
        DataOutputStream dos=null;
        try {
           dos = new DataOutputStream(cl.getOutputStream());

            // Envía el archivo
            File file = new File(archivo);
            long fileSize = file.length();

            // Enviamos el nombre del archivo y su tamaño
            dos.writeUTF(file.getName());
            dos.flush();
            dos.writeLong(fileSize);
            dos.flush();

            // Enviamos los datos del archivo en bloques de 1024 bytes
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                dos.flush();
            }

            // Envía las imágenes
            File imageFolder = new File(folder);
            File[] imageFiles = imageFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

            if (imageFiles != null) {
                dos.writeInt(imageFiles.length); // Envía la cantidad total de imágenes
                for (File imageFile : imageFiles) {
                    long imageFileSize = imageFile.length();
                    dos.writeUTF(imageFile.getName());
                    dos.flush();
                    dos.writeLong(imageFileSize);
                    dos.flush();

                    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(imageFile))) {
                        byte[] imageBuffer = new byte[1024];
                        int imageBytesRead;
                        while ((imageBytesRead = bis.read(imageBuffer)) != -1) {
                            dos.write(imageBuffer, 0, imageBytesRead);
                        }
                        dos.flush();
                    }
                }
            } else {
                dos.writeInt(0); // Envía 0 si no hay imágenes
            }

            System.out.println("Datos enviados al servidor");
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String recibirArchivo(Socket cl) {
        String nombre="";//Nombre del archivo
        BufferedOutputStream dos=null;
            try {
                //Definimos flujo de entrada orientado a bits ligado al socket
                DataInputStream dis=new DataInputStream(cl.getInputStream());
                String command = dis.readUTF();
                if ("SEND_FILE".equals(command)) {
                    //Leemos los datos del archivo recibido en bloques de 1024
                    byte[] b = new byte[1024];
                    nombre = dis.readUTF();
                    System.out.print("\nRecibimos el archivo: "+nombre);
                    long tam = dis.readLong();

                    //Creamos flujo para escribir el archivo de salida
                    dos=new BufferedOutputStream(new FileOutputStream("src/Cliente/catalogo.txt"));
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
            }else {
                System.out.println("El servidor no envio el catalogo");
            }
            }catch (Exception e){
                System.out.println("Error: "+ e);
            }


        return nombre;
    }

}
