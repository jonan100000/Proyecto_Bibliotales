package com.proyecto.bibliotales.data.models;

import java.io.Serializable;
import java.util.List;
import pojospi.Libro;

/**
 * RESPUESTA
 * =========
 * Es el paquete que nos devuelve el servior. Contiene el resultado de la operacion (exito/fracaso) y los datos solicitados.
 *
 */
public class Respuesta implements Serializable{
    /*
        1. IDENTIFICACION: creamos un identificador unico de version de serializable
    */
    private static final long SerialVersionUID = 1L;

    /*
        2. Salio bien la operacion?
    */
    private boolean exito;

    /*
        3. Hay informacion para el cliente? (Usuario no encontrado, Guardado, Actualizado, etc.)
    */
    private String mensaje;

    /*
        4. Que informacion de empleado tienes? (CREATE, READ, UPDATE)
    */
    private Libro libro;

    /*
        5. Y si hemos pedido muchos empleados (READALL)?
    */
    private List<Libro> libros;

    /*
        6. CONSTRUCTORES
    */
    // Vacio
    public Respuesta() {
        // ---
    }

    // Completo
    public Respuesta(boolean exito, String mensaje, Libro libro, List<Libro> libros) {
        super();
        this.exito = exito;
        this.mensaje = mensaje;
        this.libro = libro;
        this.libros = libros;
    }

    /*
        7. GETTERS Y SETTERS
    */
    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }


}
