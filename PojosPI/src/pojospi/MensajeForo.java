/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

import java.time.LocalDateTime;

/**
 *
 * @author DosherGG
 */
public class MensajeForo {
    private Integer id_mensaje;        
    private String contenido;          
    private LocalDateTime fecha_mensaje_foro; 
    private Usuario usuario;         
    private MensajeForo id_mensajePadre;    
    private String titulo;             
    private Libro libro;   

    public MensajeForo() {
    }

    public MensajeForo(Integer id_mensaje, String contenido, LocalDateTime fecha_mensaje_foro, Usuario usuario, MensajeForo id_mensajePadre, String titulo, Libro libro) {
        this.id_mensaje = id_mensaje;
        this.contenido = contenido;
        this.fecha_mensaje_foro = fecha_mensaje_foro;
        this.usuario = usuario;
        this.id_mensajePadre = id_mensajePadre;
        this.titulo = titulo;
        this.libro = libro;
    }

    public Integer getId_mensaje() {
        return id_mensaje;
    }

    public void setId_mensaje(Integer id_mensaje) {
        this.id_mensaje = id_mensaje;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha_mensaje_foro() {
        return fecha_mensaje_foro;
    }

    public void setFecha_mensaje_foro(LocalDateTime fecha_mensaje_foro) {
        this.fecha_mensaje_foro = fecha_mensaje_foro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public MensajeForo getId_mensajePadre() {
        return id_mensajePadre;
    }

    public void setId_mensajePadre(MensajeForo id_mensajePadre) {
        this.id_mensajePadre = id_mensajePadre;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    @Override
    public String toString() {
        return "MensajeForo{" + "id_mensaje=" + id_mensaje + ", contenido=" + contenido + ", fecha_mensaje_foro=" + fecha_mensaje_foro + ", usuario=" + usuario + ", id_mensajePadre=" + id_mensajePadre + ", titulo=" + titulo + ", libro=" + libro + '}';
    }

    
    
    
}
