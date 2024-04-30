//Bibliotecas necesarias para enviar, recibir y socket

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerCarrito {
    ArrayList<Producto> productos= new ArrayList<>();
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





                //Cierra el socket
                //cl.close();

            }
        } catch (Exception e) {
            e.printStackTrace();//cachamos la posible excepcion
        }

    }//main

    private static void crearCatalogo() {
        productos.add(new Producto("Lethal Company",3,5));
        productos.add(new Producto("Ark",4,7));
        productos.add(new Producto("Fallout",8,6));


    }

    //Funcion para recibir el archivo
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
            BufferedOutputStream dos=new BufferedOutputStream(new FileOutputStream(nombre));
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
                System.out.println("\nProgreso del archivo: " + porcentaje + "%");
            }//While
            //Cerramos los flujos de entrada y salida, asi como el socket
            dos.close();
            dis.close();
        }catch (Exception e){
            System.out.println("Error: "+ e);
        }
        return nombre;
    }
}
