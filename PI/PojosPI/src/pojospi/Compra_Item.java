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
public class Compra_Item {
    private Integer id_compra;      
    private Usuario usuario;    
    private Marketplace marketplace;        
    private Date fecha_item_compra; 

    public Compra_Item() {
    }

    public Compra_Item(Integer id_compra, Usuario usuario, Marketplace marketplace, Date fecha_item_compra) {
        this.id_compra = id_compra;
        this.usuario = usuario;
        this.marketplace = marketplace;
        this.fecha_item_compra = fecha_item_compra;
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

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public Date getFecha_item_compra() {
        return fecha_item_compra;
    }

    public void setFecha_item_compra(Date fecha_item_compra) {
        this.fecha_item_compra = fecha_item_compra;
    }

    @Override
    public String toString() {
        return "Compra_Item{" + "id_compra=" + id_compra + ", usuario=" + usuario + ", marketplace=" + marketplace + ", fecha_item_compra=" + fecha_item_compra + '}';
    }
    
    
}
