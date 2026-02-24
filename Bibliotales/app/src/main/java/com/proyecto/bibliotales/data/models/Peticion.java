package com.proyecto.bibliotales.data.models;

import java.io.Serializable;
import pojospi.Libro;
import pojospi.Usuario;

public class Peticion implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoOperacion {
        // Libros
        CREATE_LIBRO, READ_LIBRO, READALL_LIBRO, UPDATE_LIBRO, DELETE_LIBRO,

        // Usuarios
        CREATE_USUARIO,  // registro
        LOGIN_USUARIO, // iniciar sesión
        // (más adelante: LOGIN, READ_USUARIO, etc.)

        // Infra
        PING
    }

    private TipoOperacion tipoOperacion;

    // Libro (ya lo tenías)
    private Libro libro;
    private int id_libro;

    // Usuario (nuevo)
    private Usuario usuario;
    private int id_usuario;

    public Peticion() {}

    public Peticion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    // Registro
    public Peticion(TipoOperacion tipoOperacion, Usuario usuario) {
        this.tipoOperacion = tipoOperacion;
        this.usuario = usuario;
    }

    // getters/setters
    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(TipoOperacion tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public int getIdLibro() { return id_libro; }
    public void setIdLibro(int id_libro) { this.id_libro = id_libro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public int getIdUsuario() { return id_usuario; }
    public void setIdUsuario(int id_usuario) { this.id_usuario = id_usuario; }
}