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
//            u.setNombre_usuario("usuarioo_test");
//            u.setCorreo("usuarioo_test@mail.com");
//            u.setContrasena("3");
//            u.setTipoUsuario("R");
//            u.setPuntos(10);
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




        try {
            
        cad = new CADPI();
//      System.out.println(cad.leerLibros());   // El de LeerLibros
//      registrosAfectados = cad.eliminarLibro("15");    // El de EliminarLibros
//      registrosAfectados = cad.insertarLibro(l);
//      registrosAfectados = cad.insertarUsuario(u);
//      registrosAfectados = cad.modificarUsuario(3, uMod);
//      System.out.println(cad.leerItemsMarketplace());
//      System.out.println(cad.leerItemMarketplace(1));
         
            
            
        System.out.println("Registros eliminados/creados: " + registrosAfectados);
        } catch (ExcepcionPI ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
