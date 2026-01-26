/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba;
import cadpi.CADPI;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojospi.ExcepcionPI;
import pojospi.Libro;
import pojospi.TipoLibro;
import pojospi.Usuario;

/**
 *
 * @author DosherGG
 */
public class  Prueba {

    
    public static void main(String[] args) throws ParseException {
        // TODO code application logic here
        int registrosAfectados = 0;
        CADPI cad;
        String fechaTexto = "17/08/2007";
        String fechaNacimiento = "17/09/2006";
        java.text.SimpleDateFormat formato = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.util.Date fechaConvertida = formato.parse(fechaTexto); // Requiere manejar excepciones
        java.util.Date fechaConvertida2 = formato.parse(fechaNacimiento); // Requiere manejar excepciones


        
// -----------------------------------------------------------------------------------------------------------------
// Para el modificarLibros (Oscar)
//Libro lMod = new Libro();
//lMod.setTitulo("Título Actualizado");
//lMod.setDescripcion("Nueva descripción del libro");
//lMod.setFechaPublicacion(fechaConvertida2); 
//lMod.setUrlArchivo("http://biblioteca.com/archivo_modificado.pdf");
//lMod.setCostoDinero(new BigDecimal(19.99)); 
//lMod.setPortada("portada_nueva.jpg");
//
//TipoLibro tipo = new TipoLibro();
//tipo.setIdTipo(3);
//
//lMod.setTipoLibro(tipo);



// -----------------------------------------------------------------------------------------------------------------
// Para el insertarLibro (Oscar)
//        Libro l = new Libro();
//        l.setTitulo("Holaghthtg");
//        l.setDescripcion("Detgbrscripción anónima");
//        l.setFechaPublicacion(fechaConvertida);
//        l.setUrlArchivo("nosetbghxd.pdf");
//        l.setCostoDinero(new BigDecimal(500));
//        l.setPortada("nosentbgose.jpg");
//       
//       
//        
//        Usuario u = new Usuario();
//        u.setId_usuario(1);
//      
//        
//        TipoLibro t = new TipoLibro();
//        t.setIdTipo(10);
//        l.setUsuario(u);
//        l.setTipoLibro(t);




// -----------------------------------------------------------------------------------------------------------------
// Para el insertUsuarios (Jon Ander)
// ===== INSERTAR =====
//            Usuario u = new Usuario();
//            u.setNombre_usuario("usuari3reoo_test");
//            u.setCorreo("usuarioero_test@mail.com");
//            u.setContrasena("4");
//            u.setTipoUsuario("A");
//            u.setPuntos(11);
//            u.setFechaRegistro(fechaConvertida);
//            u.setFechaNacimiento(fechaConvertida2);
        



// -----------------------------------------------------------------------------------------------------------------
// Para el modificarUsuarios (Jon Ander)
//Usuario uMod = new Usuario();
//uMod.setNombre_usuario("usuario_modificado");
//uMod.setContrasena("1");
//uMod.setTipoUsuario("A");
//uMod.setPuntos(50);
//uMod.setFechaNacimiento(fechaConvertida2);




// -----------------------------------------------------------------------------------------------------------------
// Para el insertarTipoLibro (Santiago)
//TipoLibro tl = new TipoLibro();
//tl.setNombreTipo("Psicológicondo");




        try {
            
        cad = new CADPI();
//      System.out.println(cad.leerLibro(1));    //El de LeerLibro(Oscar)
//      System.out.println(cad.leerLibros());    //El de LeerLibros (Oscar)
//      registrosAfectados = cad.eliminarLibro("15");   //El de EliminarLibros (Oscar)
//      registrosAfectados= cad.modificarLibro(16, lMod);    //El de ModificarLibro(Oscar)
//      registrosAfectados = cad.insertarLibro(l);    //El de insertarLibro(Oscar)   
//      registrosAfectados = cad.insertarUsuario(u);    //El de insertarUsuario(Jon Ander)
//      registrosAfectados = cad.modificarUsuario(3, uMod);   //El de modificarUsuario(Jon Ander)
//      System.out.println(cad.leerItemsMarketplace());   //El de LeerItemsMarketplace (Victor)
//      System.out.println(cad.leerItemMarketplace(1));   //El de LeerItemMarketplace (Victor)
//      System.out.println(cad.leerTipoLibro(2));     //El de leerTipoLibro(Santiago)
//      System.out.println(cad.leerTiposLibro());     //El de leerTiposLibro (Santiago)
//      System.out.println(cad.insertarTipoLibro(tl));     //El de InsertarTipoLibro (Santiago)
//      System.out.println(cad.eliminarTipoLibro(8));     //El de eliminarTipoLibro (Santiago)
       
            
            
        System.out.println("Registros eliminados/creados: " + registrosAfectados);
        } catch (ExcepcionPI ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
