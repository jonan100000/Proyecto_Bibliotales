/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

import java.util.Date;

/**
 *
 * @author DosherGG
 */
public class Resena {
    private Integer id_resena;        
    private Integer puntuacion_resena;      
    private String comentario_resena;       
    private Date fechaResena;   
    private Libro libro;         
    private Usuario usuario;      

    public Resena() {
    }

    public Resena(Integer id_resena, Integer puntuacion_resena, String comentario_resena, Date fechaResena, Libro libro, Usuario usuario) {
        this.id_resena = id_resena;
        this.puntuacion_resena = puntuacion_resena;
        this.comentario_resena = comentario_resena;
        this.fechaResena = fechaResena;
        this.libro = libro;
        this.usuario = usuario;
    }

    public Integer getId_resena() {
        return id_resena;
    }

    public void setId_resena(Integer id_resena) {
        this.id_resena = id_resena;
    }

    public Integer getPuntuacion_resena() {
        return puntuacion_resena;
    }

    public void setPuntuacion_resena(Integer puntuacion_resena) {
        this.puntuacion_resena = puntuacion_resena;
    }

    public String getComentario_resena() {
        return comentario_resena;
    }

    public void setComentario_resena(String comentario_resena) {
        this.comentario_resena = comentario_resena;
    }

    public Date getFechaResena() {
        return fechaResena;
    }

    public void setFechaResena(Date fechaResena) {
        this.fechaResena = fechaResena;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Resena{" + "id_resena=" + id_resena + ", puntuacion_resena=" + puntuacion_resena + ", comentario_resena=" + comentario_resena + ", fechaResena=" + fechaResena + ", libro=" + libro + ", usuario=" + usuario + '}';
    }
    
    
}
