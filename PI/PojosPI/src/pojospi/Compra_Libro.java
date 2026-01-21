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
public class Compra_Libro {
    private Integer id_compra;          
    private Usuario usuario;          
    private Libro libro;            
    private Date fecha_libro_compra; 

    public Compra_Libro() {
    }

    public Compra_Libro(Integer id_compra, Usuario usuario, Libro libro, Date fecha_libro_compra) {
        this.id_compra = id_compra;
        this.usuario = usuario;
        this.libro = libro;
        this.fecha_libro_compra = fecha_libro_compra;
    }

    public Integer getId_compra() {
        return id_compra;
    }

    public void setId_compra(Integer id_compra) {
        this.id_compra = id_compra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Date getFecha_libro_compra() {
        return fecha_libro_compra;
    }

    public void setFecha_libro_compra(Date fecha_libro_compra) {
        this.fecha_libro_compra = fecha_libro_compra;
    }

    @Override
    public String toString() {
        return "Compra_Libro{" + "id_compra=" + id_compra + ", usuario=" + usuario + ", libro=" + libro + ", fecha_libro_compra=" + fecha_libro_compra + '}';
    }
    
    
}
