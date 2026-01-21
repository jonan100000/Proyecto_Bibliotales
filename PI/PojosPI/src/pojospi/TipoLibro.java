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
public class TipoLibro {
    private Integer id_tipo;      
    private String nombre_tipo;  

    public TipoLibro() {
    }

    public TipoLibro(Integer idTipo, String nombreTipo) {
        this.id_tipo = idTipo;
        this.nombre_tipo = nombreTipo;
    }

    public Integer getIdTipo() {
        return id_tipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.id_tipo = idTipo;
    }

    public String getNombreTipo() {
        return nombre_tipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombre_tipo = nombreTipo;
    }

    @Override
    public String toString() {
        return "tipoLibro{" + "idTipo=" + id_tipo + ", nombreTipo=" + nombre_tipo + '}';
    }
    
    
}
