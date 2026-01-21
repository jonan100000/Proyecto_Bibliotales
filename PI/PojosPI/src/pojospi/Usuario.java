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
public class Usuario {
    private Integer id_usuario;     
    private String nombre_usuario;  
    private String correo;          
    private String contrasena;      
    private String tipo_usuario;          
    private Integer puntos;         
    private Date fecha_registro;
    private Date fecha_nacimiento; 

    public Usuario() {
    }

    public Usuario(Integer id_usuario, String nombre_usuario, String correo, String contrasena, String tipoUsuario, Integer puntos, Date fechaRegistro, Date fechaNacimiento) {
        this.id_usuario = id_usuario;
        this.nombre_usuario = nombre_usuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.tipo_usuario = tipoUsuario;
        this.puntos = puntos;
        this.fecha_registro = fechaRegistro;
        this.fecha_nacimiento = fechaNacimiento;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoUsuario() {
        return tipo_usuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipo_usuario = tipoUsuario;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public Date getFechaRegistro() {
        return fecha_registro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fecha_registro = fechaRegistro;
    }

    public Date getFechaNacimiento() {
        return fecha_nacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fecha_nacimiento = fechaNacimiento;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id_usuario=" + id_usuario + ", nombre_usuario=" + nombre_usuario + ", correo=" + correo + ", contrasena=" + contrasena + ", tipoUsuario=" + tipo_usuario + ", puntos=" + puntos + ", fechaRegistro=" + fecha_registro + ", fechaNacimiento=" + fecha_nacimiento + '}';
    }
    
    
    
    
}
