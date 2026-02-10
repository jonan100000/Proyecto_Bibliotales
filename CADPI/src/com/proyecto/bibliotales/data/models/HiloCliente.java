package com.proyecto.bibliotales.data.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojospi.Libro;

/**
 * HILO CLIENTE
 * ============
 * Esta clase representa al recepcionista que atiende a un cliente (Android)
 * Extiende "Thread" para poder ejecutarse en paralelo al Servidor
 * 
 */
public class HiloCliente extends Thread {
    /*
        1. DECLARAMOS EL SOCKET (el telefono por el que hablamos con este cliente en particular)
    */
    private Socket socket;
    
    /*
        2. CONSTRUCTOR
    */
    public HiloCliente(Socket socket) {
        super();
        this.socket = socket;
    }
    
    /*
        3. OVERRIDE (Run)
        Aqui describimos el script que se ejecuta en paralelo (o concurrentemente)
    */
    @Override
    public void run() {
        // 3.1 Declaramos los tuneles
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        
        // 3.2 Agragamos un canal a cada tunel
        try {
            
            // "InputStream" -> oido del servidor por el cual escucha al cliente
            // "OutputStream" -> boca del servidor por el cual responde al cliente
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            
            // 3.3 Recibimos una peticion (READ)
            // El cliente nos envia un objeto Peticion serializado
            // Nos quedamos bloqueados esperando a que llegue entera
            Peticion peticion = (Peticion) ois.readObject();
            
            // 3.4 Procesamos peticion
            // Instanciamos el CAD para hablar con la base de datos
            CADPI cad = new CADPI();
            
            // 3.5 Preparamos la respuesta
            Respuesta respuesta = new Respuesta();
            
            // 3.5.1 Inicializamos la variable de exito 
            boolean exito;
            
            // 3.6 Miramos qué quiere hacer el cliente (CREATE, READ, UPDATE, DELETE, REAL ALL)
            switch(peticion.getTipoOperacion()) {
                case READ:
                    // 3.6.1 leer -> busca por ID
                    Libro libro = cad.leerLibro(peticion.getIdLibro());
                    if(libro != null) {
                        // Construimos la respuesta
                        respuesta.setLibro(libro); // Metemos el empleado en el sobre
                        respuesta.setExito(true); // Exito!
                        respuesta.setMensaje("Empleado encontrado");
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("No existe un empleado con ID: " + peticion.getIdLibro());
                    }
                    
                    break;
                    
                case READALL:
                    // 3.6.2 buscar todos los empleados -> Maximo 50 (se puede modificar en el EmpleadosCAD
                    List<Libro> lista = cad.leerLibros();
                    
                    if(!lista.isEmpty()) {
                        respuesta.setLibros(lista); // Metemos la lista Empleados
                        respuesta.setExito(true);
                        respuesta.setMensaje("Listado recuperado con " + lista.size() + " empleados.");
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("La base de datos parece vacia.");
                    }
                    break;
                    
//                case UPDATE:
//                    // 3.6.3 
//                    exito = cad.modificarLibro(peticion.getLibro());
//                    
//                    if(exito == true) {
//                        respuesta.setExito(true);
//                        respuesta.setMensaje("Datos actualizados.");
//                    } else {
//                        respuesta.setExito(false);
//                        respuesta.setMensaje("Error al actualizar. Revisa ID.");
//                    }
//                    break;
//                    
//                case CREATE:
//                    // 3.6.4
//                    exito = cad.insertarLibro(peticion.getLibro());
//                    
//                    libro = peticion.getLibro();
//                    
//                    if(exito == true) {
//                        respuesta.setExito(true);
//                        respuesta.setMensaje("Nuevo empleado creado:\n" + libro.toString());
//                    } else {
//                        respuesta.setExito(false);
//                        respuesta.setMensaje("Error al añadir nuevo empleado.");
//                    }
//                    break;
                    
                case PING:
                    // 3.6.x ping: comprueba si hay conexion cliente-servidor
                    respuesta.setExito(true);
                    respuesta.setMensaje("Pong! Servidor activo y escuchando");
                    break;
                
                default:
                    // Aqui va todo lo demas
                    respuesta.setExito(false);
                    respuesta.setMensaje("Operacion desconocida");
                    break;
            }
            
            /*
                4. ENVIAR LA RESPUESTA
            */
            oos.writeObject(respuesta);
            oos.flush();
            
        } catch (Exception ex) {
            System.out.println("Error general: " + ex.getMessage());
        } finally {
            /*
                5. CERRAMOS SESION
            */
            try {
                ois.close();
                oos.close();
                socket.close();
                
            } catch(IOException ex) {
                System.out.println("Error al cerrar canales: " + ex.getMessage());
            }
        }
        
    }
    
    
    
}
