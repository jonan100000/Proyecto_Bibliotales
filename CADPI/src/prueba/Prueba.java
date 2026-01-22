/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prueba;
import cadpi.CADPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import pojospi.ExcepcionPI;

/**
 *
 * @author DosherGG
 */
public class  Prueba {

    
    public static void main(String[] args) {
        // TODO code application logic here
        int registrosAfectados = 0;
        CADPI cad;
        try {
            
        cad = new CADPI();
      System.out.println(cad.leerLibros());   // El de LeerLibros
//      registrosAfectados = cad.eliminarLibro("15");    // El de EliminarLibros
          
            
            
        System.out.println("Registros eliminados/creados: " + registrosAfectados);
        } catch (ExcepcionPI ex) {
            Logger.getLogger(Prueba.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
