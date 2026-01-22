/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cadpi;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.animation.KeyValue;
import javafx.animation.KeyValue.Type;
import pojospi.ExcepcionPI;
import pojospi.Compra_Item;
import pojospi.Compra_Libro;
import pojospi.Genero;
import pojospi.Libro;
import pojospi.LibroGenero;
import pojospi.Marketplace;
import pojospi.MensajeForo;
import pojospi.Resena;
import pojospi.TipoLibro;
import pojospi.Usuario;

/**
 *
 * @author DosherGG
 */
public class CADPI {

    private Connection conexion;
    
    public CADPI() throws ExcepcionPI {
        try {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException ex) {          
            ExcepcionPI e = new ExcepcionPI();
            e.setMensajeErrorBD(ex.getMessage());  
            System.out.println("Error general del sistema, consulte con el administrador");  
            throw e;
        }
    }
    
    private void conectarBD() throws ExcepcionPI {
        try {
        conexion = DriverManager.getConnection("jdbc:oracle:thin:@172.16.202.1:1521:test", "bibliotales", "kk");
        }   catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
             e.setCodigoErrorBD(ex.getErrorCode());
             e.setMensajeErrorBD(ex.getMessage());
             e.setMensajeErrorUsuario("Error general del sistema, consulte con el administrador");
           throw e; 
        }
    }
    
     
     /**
     * Lee todas los registros de la tabla Libros
     * @return Cantidad de registros leídos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Óscar Eduardo Arango Torres
     * @version 1.0
     * @since AaD 1.0
    */
    public ArrayList<Libro> leerLibros() throws ExcepcionPI {
        conectarBD();
        ArrayList listaLibros = new ArrayList();
        Libro l;
        Usuario u;
        TipoLibro t;
             System.out.println("Ejecutando");
        String dql = "select * from tipo_libro T, libro L, usuario U"
                + " where T.id_tipo=L.id_tipo and L.id_usuario=U.id_usuario";
        try {
        Statement sentencia = conexion.createStatement();
        ResultSet resultado = sentencia.executeQuery(dql);
        while (resultado.next()) {
            l = new Libro();
            l.setId_libro(resultado.getInt("ID_LIBRO"));
            l.setTitulo(resultado.getString("TITULO"));
            l.setDescripcion(resultado.getString("DESCRIPCION"));
            l.setFechaPublicacion(resultado.getDate("FECHA_PUBLICACION"));
            l.setUrlArchivo(resultado.getString("URL_ARCHIVO"));
            l.setCostoDinero(resultado.getBigDecimal("COSTO_DINERO"));
            l.setPortada(resultado.getString("PORTADA"));
            
            
            u = new Usuario();
            u.setId_usuario(resultado.getInt("ID_USUARIO"));
            u.setNombre_usuario(resultado.getString("NOMBRE_USUARIO"));
            u.setCorreo(resultado.getString("CORREO"));
            u.setContrasena(resultado.getString("CONTRASENA"));
            u.setTipoUsuario(resultado.getString("TIPO_USUARIO"));
            u.setPuntos(resultado.getInt("PUNTOS"));
            u.setFechaRegistro(resultado.getDate("FECHA_REGISTRO"));
            u.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));
         
            t = new TipoLibro();
            t.setIdTipo(resultado.getInt("ID_TIPO"));
            t.setNombreTipo(resultado.getString("NOMBRE_TIPO"));
            
            l.setUsuario(u);
            l.setTipoLibro(t);
            listaLibros.add(l);  
            }
        resultado.close();
        sentencia.close();
        conexion.close();
       
        
         
        } catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
             e.setCodigoErrorBD(ex.getErrorCode());
             e.setMensajeErrorBD(ex.getMessage());
             e.setSentenciaSQL(dql);
             e.setMensajeErrorUsuario("Error general del sistema, consulte con el administrador");
           throw e; 
        }
        return listaLibros;
        }
    
    
    /**
     * Elimina un único registro de la tabla Libros
     * @param jobId Identificador de libro del registro que se desea eliminar
     * @return Cantidad de registros eliminados
     * @throws pojoshpi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Óscar Eduardo Arango Torres
     * @version 1.0
     * @since AaD 1.0
    */
    public int eliminarLibro(String id_libro) throws ExcepcionPI {
        conectarBD();
        int registrosAfectados = 0;
        String dml = "DELETE libro WHERE id_libro = "+ id_libro;
        try {
        Statement sentencia = conexion.createStatement();
        registrosAfectados = sentencia.executeUpdate(dml);
        sentencia.close();
        conexion.close();
          
        } catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
             e.setCodigoErrorBD(ex.getErrorCode());
             e.setMensajeErrorBD(ex.getMessage());
             e.setSentenciaSQL(dml);
            switch (ex.getErrorCode()) {    
                case 2292:
                    e.setMensajeErrorUsuario("No se puede eliminar el libro porque tiene un género, reseña, mensaje de foro o compra relacionados.");   
                    break;
                    
                default:
                    e.setMensajeErrorUsuario("Error general del sistema. Consulta con el administrador"); 
           }
           throw e; 
        }
        return registrosAfectados;
        }
    
    
    
    /**
     * Añade un registro a la tabla Libros
     * @param libro 
     * @return Cantidad de registros añadidos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Óscar Eduardo Arango Torres
     * @version 1.0
     * @since AaD 1.0
    */
    public Integer insertarLibro(Libro libro) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;
    String dml = "INSERT INTO libro (id_libro, titulo, descripcion, fecha_publicacion, url_archivo, id_usuario, id_tipo, costo_dinero, portada) "
               + "VALUES (LIBRO_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?, ?)";
    try {
        PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);
        sentenciaPreparada.setString(1, libro.getTitulo());
        sentenciaPreparada.setString(2, libro.getDescripcion());
        sentenciaPreparada.setDate(3, new java.sql.Date(libro.getFechaPublicacion().getTime())); 
        sentenciaPreparada.setString(4, libro.getUrlArchivo()); 
        sentenciaPreparada.setInt(5, libro.getUsuario().getId_usuario()); 
        sentenciaPreparada.setInt(6, libro.getTipoLibro().getIdTipo()); 
        sentenciaPreparada.setBigDecimal(7, libro.getCostoDinero()); 
        sentenciaPreparada.setString(8, libro.getPortada()); 

        registrosAfectados = sentenciaPreparada.executeUpdate();

        sentenciaPreparada.close();
        conexion.close();
        return registrosAfectados;
    }
    catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);

        switch (ex.getErrorCode()) {
            case 1: 
                e.setMensajeErrorUsuario("El título o la URL del archivo ya existen."); 
                break;
            case 1400: 
                e.setMensajeErrorUsuario("El título, la URL, el tipo de libro, y el costo del libro son obligatorios");
                break;
            case 2290: 
                e.setMensajeErrorUsuario("El costo del libro no puede ser menor a 0 o el titulo del libro no puede tener menos de 2 letras."); 
            case 2291: 
                e.setMensajeErrorUsuario("El usuario o el tipo de libro indicado no existen."); 
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
        }

        throw e;
    }
}
    
    
    
   
    
}
