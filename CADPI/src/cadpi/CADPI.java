/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cadpi;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.animation.KeyValue;
import javafx.animation.KeyValue.Type;
import static jdk.nashorn.internal.objects.NativeFunction.call;
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
        conexion = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.141:1521:test", "bibliotales", "kk");
        }   catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
             e.setCodigoErrorBD(ex.getErrorCode());
             e.setMensajeErrorBD(ex.getMessage());
             e.setMensajeErrorUsuario("Error general del sistema, consulte con el administrador");
           throw e; 
        }
    }
    
    
 // =========================================================SCRIPTS DE OSCAR============================================================================================
    
     /**
     * Lee un registro de la tabla Libro
     * @return Registro leído
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Óscar Eduardo Arango Torres
     * @version 1.0
     * @since AaD 1.0
    */
    public Libro leerLibro(int idLibro) throws ExcepcionPI {
    conectarBD();
    Libro l = null;

    String dql = "SELECT l.*, u.*, t.* " +
                 "FROM libro l, usuario u, tipo_libro t " +
                 "WHERE l.id_usuario = u.id_usuario " +
                 "AND l.id_tipo = t.id_tipo " +
                 "AND l.id_libro = " + idLibro;
    try {
        Statement sentencia = conexion.createStatement();
        ResultSet rs = sentencia.executeQuery(dql);
        if (rs.next()) {
            l = new Libro();
            l.setId_libro(rs.getInt("ID_LIBRO"));
            l.setTitulo(rs.getString("TITULO"));
            l.setDescripcion(rs.getString("DESCRIPCION"));
            l.setUrlArchivo(rs.getString("URL_ARCHIVO"));
            l.setCostoDinero(rs.getBigDecimal("COSTO_DINERO")); 
            l.setPortada(rs.getString("PORTADA"));

          
            Usuario u = new Usuario();
            u.setId_usuario(rs.getInt("ID_USUARIO"));
            u.setNombre_usuario(rs.getString("NOMBRE_USUARIO"));
            u.setCorreo(rs.getString("CORREO"));
            u.setContrasena(rs.getString("CONTRASENA"));
            u.setTipoUsuario(rs.getString("TIPO_USUARIO"));
            u.setPuntos(rs.getInt("PUNTOS"));
            u.setFechaRegistro(rs.getDate("FECHA_REGISTRO"));
            u.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));
            l.setUsuario(u);

            
            TipoLibro tl = new TipoLibro();
            tl.setIdTipo(rs.getInt("ID_TIPO"));
            tl.setNombreTipo(rs.getString("NOMBRE_TIPO"));
            l.setTipoLibro(tl);
        }
        rs.close();
        sentencia.close();
        conexion.close();

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dql);
        e.setMensajeErrorUsuario("Error al intentar leer el libro con ID: " + idLibro);
        throw e;
    }
    return l;
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
 * Modificar un registro en la tabla libro.
 * @param id_libro Identificador del libro a modificar
 * @param libro con los nuevos datos
 * @return Cantidad de registros modificados
 * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
 */
public Integer modificarLibro(Integer id_libro, Libro libro) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;

    String sql = "call actualizar_libro(?, ?, ?, ?, ?, ?, ?, ?)"; 

    try {
        CallableStatement sentencia = conexion.prepareCall(sql);

        
        sentencia.setInt(1, id_libro);
        sentencia.setString(2, libro.getTitulo());
        sentencia.setString(3, libro.getDescripcion());
        sentencia.setDate(4, new java.sql.Date(libro.getFechaPublicacion().getTime()));
        sentencia.setString(5, libro.getUrlArchivo());
        sentencia.setInt(6, libro.getTipoLibro().getIdTipo()); 
        sentencia.setBigDecimal(7, libro.getCostoDinero());
        sentencia.setString(8, libro.getPortada());

        registrosAfectados = sentencia.executeUpdate();
        sentencia.close();
        conexion.close();

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();

        switch (ex.getErrorCode()) {
            case 1: 
                e.setMensajeErrorUsuario("El título o la URL del archivo ya existen.");
                break;
            case 1407: 
                e.setMensajeErrorUsuario("El título, fecha_publicación, url, tipo de libro y costo del libro son obligatorios.");
                break;
            case 2290: 
                e.setMensajeErrorUsuario("El costo del libro debe ser mayor a 0 y el titulo del libro debe tener mínimo de 2 letras");
                break;
            case 2291: 
                e.setMensajeErrorUsuario("El tipo de libro especificado no existe.");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                break;
        }

        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(sql);

        throw e;
    }

    return registrosAfectados;
}   
    
 

/**
     * Añade un registro a la tabla Libro
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
    
    
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   
    
    
    
    
    
    
    
    
  // =========================================================SCRIPTS DE Jon Ander======================================================================================= 
    
  /**
     * Añade un registro a la tabla Usuario
     * @param usuario 
     * @return Cantidad de registros añadidos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Jon Ander Elvira
     * @version 1.0
     * @since AaD 1.0
    */
   public Integer insertarUsuario(Usuario usuario) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;

    String dml = "INSERT INTO usuario " +
                 "(id_usuario, nombre_usuario, correo, contrasena, tipo_usuario, puntos, fecha_registro, fecha_nacimiento) " +
                 "VALUES (USUARIO_SEQ.nextval, ?, ?, ?, ?, ?, ?, ?)";

    try {
        PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);

        sentenciaPreparada.setString(1, usuario.getNombre_usuario());
        sentenciaPreparada.setString(2, usuario.getCorreo());
        sentenciaPreparada.setString(3, usuario.getContrasena());
        sentenciaPreparada.setString(4, usuario.getTipoUsuario());
        sentenciaPreparada.setInt(5, usuario.getPuntos());
        sentenciaPreparada.setDate(6, new java.sql.Date(usuario.getFechaRegistro().getTime()));
        sentenciaPreparada.setDate(7, new java.sql.Date(usuario.getFechaNacimiento().getTime()));

        registrosAfectados = sentenciaPreparada.executeUpdate();

        sentenciaPreparada.close();
        conexion.close();

        return registrosAfectados;

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);

        switch (ex.getErrorCode()) {
            case 1: // ORA-00001 unique constraint
                e.setMensajeErrorUsuario("El nombre de usuario o el correo ya existen.");
                break;
            case 1400: // NOT NULL
                e.setMensajeErrorUsuario("Todo deben estar completos.");
                break;
            case 2290: // CHECK constraint
                e.setMensajeErrorUsuario("Los puntos deben ser mayores a 0 y el tipo de usuario debe ser 0 o 1.");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
        }

        throw e;
    }
   }
    
 
   
   
  /**
     * Modificar un registro en la tabla usuario
     * @param id_usuario
     * @param usuario
     * @return Cantidad de registros modificados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Jon Ander Elvira
     * @version 1.0
     * @since AaD 1.0
    */
    public Integer modificarUsuario(Integer id_usuario, Usuario usuario) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;

    String sql = "call actualizar_usuario(?, ?, ?, ?, ?, ?) ";

    try {

        CallableStatement sentenciaPreparada = conexion.prepareCall(sql);

        sentenciaPreparada.setInt(1, id_usuario);
        sentenciaPreparada.setString(2, usuario.getNombre_usuario());
        sentenciaPreparada.setString(3, usuario.getContrasena());
        sentenciaPreparada.setString(4, usuario.getTipoUsuario());
        sentenciaPreparada.setInt(5, usuario.getPuntos());
        sentenciaPreparada.setDate(6, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
      

        registrosAfectados = sentenciaPreparada.executeUpdate();

        sentenciaPreparada.close();
        conexion.close();

    } catch (SQLException ex) {

        ExcepcionPI e = new ExcepcionPI();

        switch (ex.getErrorCode()) {
            case 1: // ORA-00001
                e.setMensajeErrorUsuario("El nombre de usuario o el correo ya existen.");
                break;
            case 1407: // NOT NULL
                e.setMensajeErrorUsuario("Ningún campo puede estar vacíos.");
                break;
            case 2290: // CHECK constraint
                e.setMensajeErrorUsuario("Algún valor no cumple las restricciones establecidas.");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
                break;
        }

        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(sql);

        throw e;
    }

    return registrosAfectados;
    }
    
    
    
    
    
    

















    
    
    
    
    
    
 // =========================================================SCRIPTS DE VICTOR============================================================================================    
    
      /**
     * Lee un registro de la tabla marketplace
     * @return Registro leído
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Victor Torrens
     * @version 1.0
     * @since AaD 1.0
    */
    public Marketplace leerItemMarketplace(int idItem) throws ExcepcionPI {
    conectarBD();
    Marketplace m = null;
    Usuario u = new Usuario();

    String dql = "SELECT m.*, u.* "
               + "FROM marketplace m, usuario u "
               + "WHERE m.id_usuario = u.id_usuario AND m.id_item = " + idItem;

    try {
        Statement sentencia = conexion.createStatement();
        ResultSet rs = sentencia.executeQuery(dql);

        if (rs.next()) {
            m = new Marketplace();
            m.setId_item(rs.getInt("ID_ITEM"));
            m.setNombre_item(rs.getString("NOMBRE_ITEM"));
            m.setDescripcion(rs.getString("DESCRIPCION"));
            m.setCosto_puntos(rs.getInt("COSTO_PUNTOS"));
            m.setTipo_item(rs.getString("TIPO_ITEM"));

            u.setId_usuario(rs.getInt("ID_USUARIO"));
            u.setNombre_usuario(rs.getString("NOMBRE_USUARIO"));
            u.setCorreo(rs.getString("CORREO"));
            u.setCorreo(rs.getString("CORREO"));
            u.setContrasena(rs.getString("CONTRASENA"));
            u.setTipoUsuario(rs.getString("TIPO_USUARIO"));
            u.setPuntos(rs.getInt("PUNTOS"));
            u.setFechaRegistro(rs.getDate("FECHA_REGISTRO"));
            u.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO"));

            m.setUsuario(u);
        }

        rs.close();
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
    return m;
}
    
    
    
    
    
    
    
    
       /**
     * Lee todas los registros de la tabla marketplace
     * @return Cantidad de registros leídos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Victor Torrens
     * @version 1.0
     * @since AaD 1.0
    */
    public ArrayList<Marketplace> leerItemsMarketplace() throws ExcepcionPI {
    conectarBD();
    ArrayList<Marketplace> lista = new ArrayList<>();
    String dql = "SELECT m.*, u.* FROM marketplace m, usuario u "
               + "WHERE m.id_usuario = u.id_usuario";

    try {
        Statement sentencia = conexion.createStatement();
        ResultSet resultado = sentencia.executeQuery(dql);

        while (resultado.next()) {
            Marketplace m = new Marketplace();
            Usuario u = new Usuario();

            m.setId_item(resultado.getInt("ID_ITEM"));
            m.setNombre_item(resultado.getString("NOMBRE_ITEM"));
            m.setDescripcion(resultado.getString("DESCRIPCION"));
            m.setCosto_puntos(resultado.getInt("COSTO_PUNTOS"));
            m.setTipo_item(resultado.getString("TIPO_ITEM"));

            u = new Usuario();
            u.setId_usuario(resultado.getInt("ID_USUARIO"));
            u.setNombre_usuario(resultado.getString("NOMBRE_USUARIO"));
            u.setCorreo(resultado.getString("CORREO"));
            u.setContrasena(resultado.getString("CONTRASENA"));
            u.setTipoUsuario(resultado.getString("TIPO_USUARIO"));
            u.setPuntos(resultado.getInt("PUNTOS"));
            u.setFechaRegistro(resultado.getDate("FECHA_REGISTRO"));
            u.setFechaNacimiento(resultado.getDate("FECHA_NACIMIENTO"));

            m.setUsuario(u);
            lista.add(m);
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
    return lista;
    }
    

 




























 // =========================================================SCRIPTS DE SANTIAGO============================================================================================
    
       /**
     * Lee un registro de la tabla tipoLibro
     * @return Registro leído
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Santiago Quiceno
     * @version 1.0
     * @since AaD 1.0
    */ 
  public TipoLibro leerTipoLibro(int id_tipo) throws ExcepcionPI {
    conectarBD();
    TipoLibro tipo = null;
    
    String dql = "SELECT tl.* FROM tipo_libro tl WHERE id_tipo = " + id_tipo;
    
    try {
        Statement sentencia = conexion.createStatement();

        
        ResultSet resultado = sentencia.executeQuery(dql);
        
        if (resultado.next()) {
            tipo = new TipoLibro();
            tipo.setIdTipo(resultado.getInt("id_tipo"));
            tipo.setNombreTipo(resultado.getString("nombre_tipo"));
        }
        
        resultado.close();
        sentencia.close();
        conexion.close();
        
    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dql);
        e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador");
        throw e;
    }
    
    return tipo;
} 
  
  
  
   
     /**
     * Lee todas los registros de la tabla tipoLibro
     * @return Cantidad de registros leídos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Santiago Quiceno
     * @version 1.0
     * @since AaD 1.0
    */
  public ArrayList<TipoLibro> leerTiposLibro() throws ExcepcionPI {
    conectarBD();
    ArrayList<TipoLibro> listaTipos = new ArrayList<>();
    TipoLibro t;
    
    String dql = "SELECT tl.* FROM tipo_libro ORDER BY id_tipo";
    
    try {
        Statement sentencia = conexion.createStatement();
        ResultSet resultado = sentencia.executeQuery(dql);
        
        while (resultado.next()) {
            t = new TipoLibro();
            t.setIdTipo(resultado.getInt("id_tipo"));
            t.setNombreTipo(resultado.getString("nombre_tipo"));
            listaTipos.add(t);
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
    
    return listaTipos;
}
  
  
  
  
  /**
     * Añade un registro a la tabla Libro
     * @param tipoLibro 
     * @return Cantidad de registros añadidos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Santiago Quiceno
     * @version 1.0
     * @since AaD 1.0
    */
  public int insertarTipoLibro(TipoLibro tipoLibro) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;
    
    String dml = "INSERT INTO tipo_libro (id_tipo, nombre_tipo) VALUES (TIPO_LIBRO_SEQ.nextval, ?)";
    
    try {
        PreparedStatement sentenciaPreparada = conexion.prepareStatement(dml);
        sentenciaPreparada.setString(1, tipoLibro.getNombreTipo());
        
        registrosAfectados = sentenciaPreparada.executeUpdate();
        
        sentenciaPreparada.close();
        conexion.close();
        
        return registrosAfectados;
        
    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);
        
        switch (ex.getErrorCode()) {
            case 1: 
                e.setMensajeErrorUsuario("Ya existe un tipo de libro con ese nombre.");
                break;
            case 1400:
                e.setMensajeErrorUsuario("El nombre del tipo de libro es obligatorio");
                break;
            case 2290: 
                e.setMensajeErrorUsuario("El nombre debe tener al menos 3 caracteres (sin espacios).");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
        }
        
        throw e;
    }
}
  
  
  
  
    /**
     * Elimina un único registro de la tabla tipoLibro
     * @param jobId Identificador de libro del registro que se desea eliminar
     * @return Cantidad de registros eliminados
     * @throws pojoshpi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Santiago Quiceno
     * @version 1.0
     * @since AaD 1.0
    */
    public int eliminarTipoLibro(int idTipo) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;
    
    String dml = "DELETE FROM tipo_libro WHERE id_tipo = " + idTipo;
    
    try {
        Statement sentencia = conexion.createStatement();
        registrosAfectados = sentencia.executeUpdate(dml);
        
        sentencia.close();
        conexion.close();
        return registrosAfectados;
        
    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);
        
        switch (ex.getErrorCode()) {
            case 2292: 
                e.setMensajeErrorUsuario("El tipo de libro no se puede eliminar. Existen libros asociados a este tipo.");
                break;
            case 1403: 
                    e.setMensajeErrorUsuario("No se encontró el tipo de libro con ID: " + idTipo);
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema. Consulte con el administrador.");
        }
        
        throw e;
    }
}
  
  
  

}    
    


