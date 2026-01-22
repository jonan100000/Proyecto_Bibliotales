/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author DosherGG
 */
public class Libro {
    private Integer id_libro;          
    private String titulo;            
    private String descripcion;      
    private Date fechaPublicacion; 
    private String urlArchivo;        
    private Usuario usuario;       
    private TipoLibro tipoLibro;          
    private BigDecimal costoDinero;  
    private String portada;

    public Libro() {
    }

    public Libro(Integer id_libro, String titulo, String descripcion, Date fechaPublicacion, String urlArchivo, Usuario usuario, TipoLibro tipoLibro, BigDecimal costoDinero, String portada) {
        this.id_libro = id_libro;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.urlArchivo = urlArchivo;
        this.usuario = usuario;
        this.tipoLibro = tipoLibro;
        this.costoDinero = costoDinero;
        this.portada = portada;
    }

    public Integer getId_libro() {
        return id_libro;
    }

    public void setId_libro(Integer id_libro) {
        this.id_libro = id_libro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoLibro getTipoLibro() {
        return tipoLibro;
    }

    public void setTipoLibro(TipoLibro tipoLibro) {
        this.tipoLibro = tipoLibro;
    }

    public BigDecimal getCostoDinero() {
        return costoDinero;
    }

    public void setCostoDinero(BigDecimal costoDinero) {
        this.costoDinero = costoDinero;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    @Override
    public String toString() {
        return "Libro{" + "id_libro=" + id_libro + ", titulo=" + titulo + ", descripcion=" + descripcion + ", fechaPublicacion=" + fechaPublicacion + ", urlArchivo=" + urlArchivo + ", usuario=" + usuario + ", tipoLibro=" + tipoLibro + ", costoDinero=" + costoDinero + ", portada=" + portada + '}';
    }

   
    
    
    
}
