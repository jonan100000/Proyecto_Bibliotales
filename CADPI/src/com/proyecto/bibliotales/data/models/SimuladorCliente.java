package com.proyecto.bibliotales.data.models;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import pojospi.Libro;

/**
 * SIMULADOR DE CLIENTE (INTERACTIVO)
 * ==================================
 * Esta clase permite probar el servidor mediante un menú de consola.
 * Actúa como si fuera el móvil: Conecta, envía petición y espera respuesta.
 */
public class SimuladorCliente {

    public static void main(String[] args) {
        // CONFIGURACIÓN
        // Si usas el Simulador desde el MISMO PC que el servidor: localhost
        // Si lo usas desde otro PC: Pon la IP del servidor.
        String SERVER_IP = "localhost";
        int SERVER_PORT = 5000;

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        System.out.println("========================================");
        System.out.println("   SIMULADOR DE CLIENTE ANDROID");
        System.out.println("   Destino: " + SERVER_IP + ":" + SERVER_PORT);
        System.out.println("========================================");

        do {
            System.out.println("\nSELECCIONA UNA OPERACIÓN:");
            System.out.println("1. PING (Comprobar conexión)");
            System.out.println("2. LEER LIBRO (Read by ID)");
            System.out.println("0. SALIR");
            System.out.print("> ");

            try {
                String input = scanner.nextLine();
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            if (opcion == 0)
                break;

            // IMPORTANTE:
            // Cada vez que queremos hablar con el servidor, debemos abrir un NUEVO socket.
            // El servidor cierra la conexión después de responder, así que "colgamos" y
            // "volvemos a llamar".
            try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                Peticion peticion = null;

                switch (opcion) {
                    case 1: // PING
                        System.out.println("--> [PING] Comprobando servidor...");
                        peticion = new Peticion(Peticion.TipoOperacion.PING);
                        break;

                    case 2: // LEER
                        System.out.print("   Introduce el ID del libro (ej: 1): ");
                        try {
                            String idStr = scanner.nextLine();
                            if (idStr.isEmpty())
                                continue;
                            int id = Integer.parseInt(idStr);

                            System.out.println("--> [READ] Solicitando datos del libro ID " + id + "...");

                            // Construimos la petición de lectura
                            peticion = new Peticion();
                            peticion.setTipoOperacion(Peticion.TipoOperacion.READ);
                            peticion.setIdLibro(id);

                        } catch (Exception e) {
                            System.out.println("   [!] ID inválido. Debe ser un número.");
                            continue;
                        }
                        break;

                    default:
                        System.out.println("   [!] Opción no válida.");
                        continue;
                }

                // SI TENEMOS UNA PETICIÓN VÁLIDA, LA ENVIAMOS
                if (peticion != null) {
                    oos.writeObject(peticion);
                    oos.flush();

                    // Y ESPERAMOS LA RESPUESTA
                    Respuesta respuesta = (Respuesta) ois.readObject();

                    // MOSTRAMOS EL RESULTADO
                    System.out.println("\n<-- RESPUESTA DEL SERVIDOR:");

                    if (respuesta.isExito()) {
                        System.out.println("    [OK] " + respuesta.getMensaje());

                        // Si nos han devuelto un objeto Empleado, lo mostramos
                        if (respuesta.getLibro() != null) {
                            Libro l = respuesta.getLibro();
                            System.out.println("         -----------------------");
                            System.out.println("         Titulo: " + l.getTitulo());
                            System.out.println("         Descripción:  " + l.getDescripcion());
                            System.out.println("         URL:    " + l.getUrlArchivo());
                            System.out.println("         -----------------------");
                        }
                    } else {
                        System.out.println("    [ERROR] " + respuesta.getMensaje());
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("\n[ERROR FATAL]");
                System.out.println("No se pudo conectar con el servidor.");
                System.out.println("Detalles: " + e.getMessage());
            }

        } while (opcion != 0);

        System.out.println("Simulador cerrado.");
        scanner.close();
    }
}

