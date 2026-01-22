/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pojospi;

/**
 *
 * @author DosherGG
 */
public class Marketplace {
    private Integer id_item;       
    private String nombre_item;    
    private String descripcion;  
    private Integer costo_puntos;  
    private String tipo_item;      
    private Usuario usuario;   

    public Marketplace() {
    }

    public Marketplace(Integer id_item, String nombre_item, String descripcion, Integer costo_puntos, String tipo_item, Usuario usuario) {
        this.id_item = id_item;
        this.nombre_item = nombre_item;
        this.descripcion = descripcion;
        this.costo_puntos = costo_puntos;
        this.tipo_item = tipo_item;
        this.usuario = usuario;
    }

    public Integer getId_item() {
        return id_item;
    }

    public void setId_item(Integer id_item) {
        this.id_item = id_item;
    }

    public String getNombre_item() {
        return nombre_item;
    }

    public void setNombre_item(String nombre_item) {
        this.nombre_item = nombre_item;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCosto_puntos() {
        return costo_puntos;
    }

    public void setCosto_puntos(Integer costo_puntos) {
        this.costo_puntos = costo_puntos;
    }

    public String getTipo_item() {
        return tipo_item;
    }

    public void setTipo_item(String tipo_item) {
        this.tipo_item = tipo_item;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Marketplace{" + "id_item=" + id_item + ", nombre_item=" + nombre_item + ", descripcion=" + descripcion + ", costo_puntos=" + costo_puntos + ", tipo_item=" + tipo_item + ", usuario=" + usuario + '}';
    }
    
    
}
