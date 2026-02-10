package com.proyecto.bibliotales.data.models;

/*
 * CONFIGURACION DEL SERVIDOR
 * ==========================
 * Esta clase se encarga de llamar al servidor
 *
 * Fluido de trabajo:
 * 1. Abre una conexion (socket) con la IP y el puerto del servidor
 * 2. Envia un objeto "Peticion"
 * 3. Espera a recibir un objeto "Respuesta"
 * 4. Cierra todas las conexiones y devuelve la respuesta
 * */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSocket {
    // 1. Conexion
    private String host;
    private int puerto;

    public ClienteSocket(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    /*
     * 2. Metodo principal
     * ================
     * Retorna un NULL si hubo un error de conexion o un objeto respuesta si no
     * */
    public Respuesta enviarPeticion(Peticion peticion) {
        // 3. Declarar variables de conexion
        Socket socket = null;
        ObjectInputStream ois = null; // Enviamos
        ObjectOutputStream oos = null; // Recibimos
        Respuesta respuesta = null;

        // Conectarnos
        try {
            // Conectamos con el servidor, lo que puede datnos un error de conexion si la IP esta mal o el server apagado
            socket = new Socket(host, puerto);

            // Creamos los canales
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());

            // Enviamos la peticion (Request). Para ello, la escribimos en el canal
            oos.writeObject(peticion);
            oos.flush();

            // Esperamos a recibir el objeto Respuesta
            respuesta = (Respuesta) ois.readObject();

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            // Cerramos
            try {
                oos.close();
                ois.close();
                socket.close();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        // Devolvemos al MainActivity la respuesta
        return respuesta;
    }

}
