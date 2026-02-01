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
     * Elimina un único registro de la tabla Libro
     * @param id_libro Identificador de libro del registro que se desea eliminar
     * @return Cantidad de registros eliminados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
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
    * @param id_libro Identificador de Libro del registro que se desea modificar
    * @param libro Objeto que condensa toda la información de un libro
    * @return Cantidad de registros modificados
    * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
    * @author Óscar Eduardo Arango Torres
    * @version 1.0
    * @since AaD 1.0
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
        sentenciaPreparada.setObject(5, usuario.getPuntos(), java.sql.Types.NUMERIC);
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
            case 1: 
                e.setMensajeErrorUsuario("El nombre de usuario o el correo ya existen.");
                break;
            case 1400:
                e.setMensajeErrorUsuario("Todo deben estar completos.");
                break;
            case 2290: 
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
     * @param id_usuario Identificador de Usuario del registro que se desea modificar
     * @param usuario Objeto que condensa toda la información de un departamento
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
        sentenciaPreparada.setObject(5, usuario.getPuntos(), java.sql.Types.NUMERIC);
        sentenciaPreparada.setDate(6, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
      

        registrosAfectados = sentenciaPreparada.executeUpdate();

        sentenciaPreparada.close();
        conexion.close();

    } catch (SQLException ex) {

        ExcepcionPI e = new ExcepcionPI();

        switch (ex.getErrorCode()) {
            case 1:
                e.setMensajeErrorUsuario("El nombre de usuario o el correo ya existen.");
                break;
            case 1407:
                e.setMensajeErrorUsuario("Ningún campo puede estar vacíos.");
                break;
            case 2290: 
                e.setMensajeErrorUsuario("El costo de puntos debe ser mayor a 0 o el tipo de usuario debe ser: R (registrado) o A (administrador)");
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
    * Lee un registro de la tabla mensaje_foro
    * @return Registro leído
    * @param idMensaje identificador de mensaje_foro del registro que se desea leer.
    * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
    * @author Jon Ander Elvira
     * @version 1.0
    * @since AaD 1.0
    */
    public MensajeForo leerMensajeForo(int idMensaje) throws ExcepcionPI {
    conectarBD();
    MensajeForo m = null;

    // Consulta con múltiples alias para evitar colisiones de nombres:
    // u  -> Autor del mensaje actual
    // ul -> Autor del libro (quien lo subió)
    // up -> Autor del mensaje padre (al que se está respondiendo)
    // mp -> El mensaje padre en sí
    String dql = "SELECT mf.*, u.*, l.*, tl.nombre_tipo, " +     
                 "ul.id_usuario AS id_usuario_creador , ul.nombre_usuario AS nombre_creador_libro, ul.correo AS correo_creador," + 
                 "ul.contrasena AS contrasena_creador, ul.tipo_usuario AS tipo_usuario_creador, ul.puntos AS puntos_creador," + 
                 "ul.fecha_registro AS fecha_registro_creador, ul.fecha_nacimiento AS fecha_nacimiento_creador," +
                 "mp.contenido AS cont_padre, mp.titulo AS tit_padre, mp.fecha_mensaje_foro AS fecha_padre, " +
                 "up.id_usuario AS id_usuario_padre, up.nombre_usuario AS nombre_usuario_padre, up.correo AS correo_padre," + 
                 "up.contrasena AS contrasena_padre, up.tipo_usuario AS tipo_usuario_padre, up.puntos AS puntos_padre, " + 
                 "up.fecha_registro AS fecha_registro_padre, up.fecha_nacimiento AS fecha_nacimiento_padre " + 
                 "FROM mensaje_foro mf, usuario u, libro l, tipo_libro tl, usuario ul, mensaje_foro mp, usuario up " +
                 "WHERE mf.id_usuario = u.id_usuario " +
                 "AND mf.id_libro = l.id_libro " +
                 "AND l.id_tipo = tl.id_tipo " +
                 "AND l.id_usuario = ul.id_usuario " +
                 "AND mf.id_mensaje_padre = mp.id_mensaje(+) " + 
                 "AND mp.id_usuario = up.id_usuario(+) " + 
                 "AND mf.id_mensaje = " + idMensaje;

    try {
        Statement sentencia = conexion.createStatement();
        ResultSet rs = sentencia.executeQuery(dql);

        if (rs.next()) {
            m = new MensajeForo();
           
            m.setId_mensaje(rs.getInt("id_mensaje")); 
            m.setContenido(rs.getString("contenido")); 
            m.setFecha_mensaje_foro(rs.getTimestamp("fecha_mensaje_foro").toLocalDateTime()); 
            m.setTitulo(rs.getString("titulo")); 

           
            Usuario u = new Usuario();
            u.setId_usuario(rs.getInt("id_usuario")); 
            u.setNombre_usuario(rs.getString("nombre_usuario")); 
            u.setCorreo(rs.getString("correo")); 
            u.setContrasena(rs.getString("contrasena")); 
            u.setTipoUsuario(rs.getString("tipo_usuario")); 
            u.setPuntos(rs.getInt("puntos")); 
            u.setFechaRegistro(rs.getDate("fecha_registro"));
            u.setFechaNacimiento(rs.getDate("fecha_nacimiento")); 
            m.setUsuario(u);

           
            Libro l = new Libro();
            l.setId_libro(rs.getInt("id_libro")); 
            l.setTitulo(rs.getString("titulo")); 
            l.setDescripcion(rs.getString("descripcion")); 
            l.setFechaPublicacion(rs.getDate("fecha_publicacion")); 
            l.setUrlArchivo(rs.getString("url_archivo")); 
            l.setCostoDinero(rs.getBigDecimal("costo_dinero")); 
            l.setPortada(rs.getString("portada")); 
            
           
            TipoLibro tl = new TipoLibro();
            tl.setIdTipo(rs.getInt("id_tipo")); 
            tl.setNombreTipo(rs.getString("nombre_tipo")); 
            l.setTipoLibro(tl);
            
          
            Usuario autorLibro = new Usuario();
            autorLibro.setId_usuario(rs.getInt("id_usuario_creador")); 
            autorLibro.setNombre_usuario(rs.getString("nombre_creador_libro")); 
            autorLibro.setCorreo(rs.getString("correo_creador")); 
            autorLibro.setContrasena(rs.getString("contrasena_creador")); 
            autorLibro.setTipoUsuario(rs.getString("tipo_usuario_creador")); 
            autorLibro.setPuntos(rs.getInt("puntos_creador")); 
            autorLibro.setFechaRegistro(rs.getDate("fecha_registro_creador"));
            autorLibro.setFechaNacimiento(rs.getDate("fecha_nacimiento_creador")); 
            l.setUsuario(autorLibro);
            
            m.setLibro(l);

          
            int idPadre = rs.getInt("id_mensaje_padre"); 
            if (!rs.wasNull()) {
                MensajeForo padre = new MensajeForo();
                padre.setId_mensaje(idPadre);
                padre.setContenido(rs.getString("cont_padre")); 
                padre.setTitulo(rs.getString("tit_padre"));
                if (rs.getTimestamp("fecha_padre") != null) {
                    padre.setFecha_mensaje_foro(rs.getTimestamp("fecha_padre").toLocalDateTime());
                }
                
               
                Usuario uP = new Usuario();
                uP.setId_usuario(rs.getInt("id_usuario_padre")); 
                uP.setNombre_usuario(rs.getString("nombre_usuario_padre"));
                uP.setCorreo(rs.getString("correo_padre")); 
                uP.setContrasena(rs.getString("contrasena_padre")); 
                uP.setTipoUsuario(rs.getString("tipo_usuario_padre")); 
                uP.setPuntos(rs.getInt("puntos_padre")); 
                uP.setFechaRegistro(rs.getDate("fecha_registro_padre"));
                uP.setFechaNacimiento(rs.getDate("fecha_nacimiento_padre")); 
                padre.setUsuario(uP);
                
                m.setId_mensajePadre(padre);
            } else {
                m.setId_mensajePadre(null); 
            }
        }

        rs.close();
        sentencia.close();
        conexion.close();

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dql);
        e.setMensajeErrorUsuario("Error al leer el mensaje del foro con jerarquía de usuarios.");
        throw e;
    }
    return m;
}



    /** 
    * Elimina un único registro de la table MensajeForo
     * @param idMensaje Identificador de mensaje_foro del registro que se desea eliminar
    * @return Cantidad de registros eliminados
    * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
    * @author Jon Ander Elvira
    * @version 1.0
    * @since AaD 1.0
    */
    public Integer eliminarMensajeForo(Integer idMensaje) throws ExcepcionPI {
    conectarBD();
    int registrosAfectados = 0;

    String dml = "DELETE FROM mensaje_foro WHERE id_mensaje = " + idMensaje;

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
                e.setMensajeErrorUsuario("No se puede eliminar el mensaje porque tiene respuestas asociadas.");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema.");
        }

        throw e;
    }

    return registrosAfectados;
}
    
    

















    
    
    
    
    
    
 // =========================================================SCRIPTS DE VICTOR============================================================================================    
    
      
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
    
    
    
    
   
      
    /**
     * Añade un registro a la tabla Marketplace
     * @param item 
     * @return Cantidad de registros añadidos
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Victor Torrens
     * @version 1.0
     * @since AaD 1.0
     */
    public int insertarItemMarketplace(Marketplace item) throws ExcepcionPI {
    conectarBD();
    int registros = 0;

    String dml = "INSERT INTO marketplace "
               + "(id_item, nombre_item, descripcion, costo_puntos, tipo_item, id_usuario) "
               + "VALUES (MARKETPLACE_SEQ.nextval, ?, ?, ?, ?, ?)";

    try {
        PreparedStatement ps = conexion.prepareStatement(dml);

        ps.setString(1, item.getNombre_item());
        ps.setString(2, item.getDescripcion());
        ps.setObject(3, item.getCosto_puntos(), java.sql.Types.NUMERIC);
        ps.setString(4, item.getTipo_item());
        ps.setInt(5, item.getUsuario().getId_usuario());

        registros = ps.executeUpdate();
        ps.close();
        conexion.close();
        return registros;

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);

        switch (ex.getErrorCode()) {
            case 1:
                e.setMensajeErrorUsuario("Este item ya existe");
                break;
            case 1400:
                e.setMensajeErrorUsuario("El nombre del item, el coste de puntos o que tipo es el item son obligatorios.");
                break;
            case 2290:
                 e.setMensajeErrorUsuario("El costo de puntos del item no puede ser menor a 0, el nombre del item debe tener más de 2 caracteres o"
                        +  "el tipo de item debe ser : E (emoji), M (marco) o F (fondo)");
                break;
            case 2291:
                e.setMensajeErrorUsuario("El usuario indicado no existe");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema");
        }
        throw e;
    }
    
}
    

    
       
    
    /**
     * Modificar un registro en la tabla marketplace 
     * @param id_item Identificador del item del Marketplace del registro que se desea modificar
     * @param item Objeto que condensa toda la información de un item del Marketplace
     * @return Cantidad de registros modificados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Victor Torrens
     * @version 1.0
     * @since AaD 1.0
     */
    public Integer modificarItemMarketplace(Integer id_item, Marketplace item) throws ExcepcionPI {
        conectarBD();
        int registrosAfectados = 0;

        String sql = "call actualizar_marketplace(?, ?, ?, ?, ?)";

        try {
            CallableStatement sentenciaPreparada = conexion.prepareCall(sql);

            sentenciaPreparada.setInt(1, id_item);
            sentenciaPreparada.setString(2, item.getNombre_item());
            sentenciaPreparada.setString(3, item.getDescripcion());
            sentenciaPreparada.setObject(4, item.getCosto_puntos(), java.sql.Types.NUMERIC);
            sentenciaPreparada.setString(5, item.getTipo_item());

            registrosAfectados = sentenciaPreparada.executeUpdate();

            sentenciaPreparada.close();
            conexion.close();

        } catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();

            switch (ex.getErrorCode()) {
                case 1: 
                    e.setMensajeErrorUsuario("El nombre del item ya existe en el marketplace.");
                    break;
                case 1407:
                    e.setMensajeErrorUsuario("El nombre del item, el coste de puntos y que tipo es el item son obligatorios.");
                    break;
                case 2290: 
                    e.setMensajeErrorUsuario("El costo de puntos del item no puede ser menor a 0, el nombre del item debe tener más de 2 caracteres o"
                        +  "el tipo de item debe ser : E (emoji), M (marco) o F (fondo)");
                    break;
                case 2291: 
                    e.setMensajeErrorUsuario("El usuario asociado al item no existe.");
                    break;  
                default:
                    e.setMensajeErrorUsuario("Error general del sistema en Marketplace. Consulte con el administrador.");
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
     * Elimina un único registro de la tabla Marketplace
     * @param idItem Identificador de Marketplace del registro que se desea eliminar
     * @return Cantidad de registros eliminados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Victor Torrens
     * @version 1.0
     * @since AaD 1.0
    */
    public int eliminarItemMarketplace(int idItem) throws ExcepcionPI {
    conectarBD();
    int registros = 0;

    String dml = "DELETE FROM marketplace WHERE id_item = " + idItem;

    try {
        Statement st = conexion.createStatement();
        registros = st.executeUpdate(dml);

        st.close();
        conexion.close();

    } catch (SQLException ex) {
        ExcepcionPI e = new ExcepcionPI();
        e.setCodigoErrorBD(ex.getErrorCode());
        e.setMensajeErrorBD(ex.getMessage());
        e.setSentenciaSQL(dml);

        switch (ex.getErrorCode()) {
            case 2292:
                e.setMensajeErrorUsuario("No se puede eliminar el item porque tiene compras asociadas");
                break;
            default:
                e.setMensajeErrorUsuario("Error general del sistema");
        }
        throw e;
    }
    return registros;
}



























 // =========================================================SCRIPTS DE SANTIAGO============================================================================================
    
    /**
     * Lee un registro de la tabla tipoLibro
     * @return Registro leído
     * @param id_tipo Identificador de TipoLibro del registro que se desea leer
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
     * Añade un registro a la tabla TipoLibro
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
     * @param idTipo Identificador de TipoLibro del registro que se desea eliminar
     * @return Cantidad de registros eliminados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
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
  
    
    
  /**
     * Modifica un registro en la tabla TipoLibro 
     * @param id_tipo Identificador de TipoLibro del registro que se desea modificar
     * @param tipoLibro Objeto que condensa toda la información de un tipolibro.
     * @return Cantidad de registros modificados
     * @throws pojospi.ExcepcionPI Se lanzará cuando se produzca un error de base de datos
     * @author Santiago Quiceno
     * @version 1.0
     * @since AaD 1.0
     */
    public Integer modificarTipoLibro(Integer id_tipo, TipoLibro tipoLibro) throws ExcepcionPI {
        conectarBD();
        int registrosAfectados = 0;
        
        String sql = "call actualizar_tipo_libro(?, ?)";
        
        try {
            CallableStatement sentencia = conexion.prepareCall(sql);
            
            sentencia.setInt(1, id_tipo);
            sentencia.setString(2, tipoLibro.getNombreTipo());
            
            registrosAfectados = sentencia.executeUpdate();
            sentencia.close();
            conexion.close();
            
        } catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
            
            switch (ex.getErrorCode()) {
                case 1:
                    e.setMensajeErrorUsuario("Ya existe otro tipo de libro con ese nombre.");
                    break;
                case 1400: 
                    e.setMensajeErrorUsuario("El nombre del tipo de libro es obligatorio.");
                    break;
                case 2290: 
                    e.setMensajeErrorUsuario("El nombre debe tener al menos 3 caracteres (sin espacios).");
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
  
  
  

}    
    


