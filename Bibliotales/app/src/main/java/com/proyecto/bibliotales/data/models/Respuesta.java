package com.proyecto.bibliotales.data.models;

import java.io.Serializable;
import java.util.List;
import pojospi.Libro;
import pojospi.Usuario;

public class Respuesta implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean exito;
    private String mensaje;

    private Libro libro;
    private List<Libro> libros;

    // Nuevo
    private Usuario usuario;

    public Respuesta() {}

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }

    public List<Libro> getLibros() { return libros; }
    public void setLibros(List<Libro> libros) { this.libros = libros; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}