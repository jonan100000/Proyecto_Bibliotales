/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyecto.bibliotales.data.models;

import com.proyecto.bibliotales.data.models.ServerConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import pojospi.ExcepcionPI;

/**
 * CLASE PRINCIPAL DEL SERVIDOR
 * ----------------------------
 * Punto de entrada (main) del servidor 
 * Su funcion es:
 * - Abrir un puerto para escuchar peticiones
 * - Verificar que la base de datos es accesibles
 * - Quedarse en un bucle infinito esperando a que los clientes de Android se 
 * conecten
 * - Cuando un cliente se conecta, le asigna un "HiloCliente" para atenderle 
 * de forma exclusiva
 * 
 */
public class Servidor {
    
    public static void main(String[] args) throws ExcepcionPI {
        // 1. DEFINIMOS EL PUERTO
        // El puerto 5000 es arbitrario pero tiene que ser el mismo que pongamos 
        // en el cliente
        int port = ServerConfig.SERVER_PORT;
        
        /*
           2. INICIALIZAMOS EL SERVIDOR (ServerSocket)
        */
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("=======================================");
            System.out.println("==SERVIDOR INICIALIZADO CORRECTAMENTE==");
            System.out.println("======Escuchando en puerto: " + port + "=======");
            System.out.println("=======================================");
            
            /*
                3. VERIFICAMOS LA CONEXION CON ORACLE
            */
            String ipDb = ServerConfig.getDbIp();
            System.out.println("Verificamos conexion en: " + ipDb);
            
            CADPI cadTest = new CADPI();
            
            if (cadTest.testConnection()) {
                System.out.println("OK. Conexión Exitosa con la base de datos");
            } else {
                System.out.println("ERROR. No se pudo conectar a la base de datos");
                System.out.println("[AYUDA] Revisa el ServerConfig y asegurar que: \n1. La máquina virtual este encendida \n2. La IP es correcta en la VM \n3. Hay ping entre la PC y la VM");
            }
            
            /*
                4. BUCLE INFINITO DE ESCUCHA
                El servidor nunca termina por él mismo, siempre queda a la espera de clientes
            */
            System.out.println("Esperando clientes...");
            while (true) {
                // 4.1 El Bloqueo: accept()
                // Esta línea congela el programa hasta que un cliente logre conectarse
                Socket socket = serverSocket.accept();
                
                System.out.println("Nuevo cliente conectado! " + socket.getInetAddress().getHostAddress());
                
                // 4.2 DELEGAMOS EN UN HILO
                // Si atendieramos el cliente aqui, nadie mas podria conectarse a el mientras tanto
                // Por eso, crear un trabajador (HiloCliente) dedicado solo a este usuario
                HiloCliente hilo = new HiloCliente(socket);
                
                // 4.3 ARRANCAR EL HILO
                hilo.start();
                
            }
            
        } catch (IOException e) {
            System.out.println("ERROR FATAL. Servidor: " + e.getMessage());
            e.printStackTrace();
        }
        
    }
    
}
