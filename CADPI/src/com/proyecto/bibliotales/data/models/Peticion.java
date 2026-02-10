package com.proyecto.bibliotales.data.models;

import java.io.Serializable;
import pojospi.Libro;

/**
 * PETICION
 * ========
 * Es el sobre que envia el cliente. Representa la informacion que viaja desde la app de Android al servidor
 * IMPORTANTE: es serializable, es decir, se transforma en binario para viajar por la red.
 * 
 */
public class Peticion implements Serializable {
    
    /*
        1. IDENTIFICACION: creamos un identificador unico de version de serializable
    */
    private static final long SerialVersionUID = 1L;
    
    /*
        2. CREAMOS EL ENUM DE TIPOOPERACION
    */
    public enum TipoOperacion {
        CREATE,    // Crea
        READ,      // Lee
        READALL,   // Lee todos
        UPDATE,    // Actualiza
        DELETE,    // Borra
        PING       // Comprueba la conexion
    }
    
    /*
        3. Que quiere hacer el cliente?
    */
    private TipoOperacion tipoOperacion;
    
    /*
        4. Con que datos? (Create/Update)
    */
    private Libro libro;
    
    /*
        5. Con que ID (Read/Delete)
    */
    private int id_libro;
    
    /*
        6. CONSTRUCTORES
    */
    // 6.1 Vacio
    public Peticion() {
        // ---
    }
    
    // 6.2 Constructor para Read All
    public Peticion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
    
    // 6.3 Constructor para Read/Delete
    public Peticion(TipoOperacion tipoOperacion, int id_libro) {
        this.tipoOperacion = tipoOperacion;
        this.id_libro = id_libro;
    }
    
    // 6.4 Constructor para Create/Update
    public Peticion(TipoOperacion tipoOperacion, int id_libro, Libro libro) {
        this.tipoOperacion = tipoOperacion;
        this.id_libro = id_libro;
        this.libro = libro;
    }
    
    /*
        7. GETTERS Y SETTERS
    */ 
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setEmpleado(Libro libro) {
        this.libro = libro;
    }

    public int getIdLibro() {
        return id_libro;
    }

    public void setIdLibro(int id_libro) {
        this.id_libro = id_libro;
    }
    
    
}
