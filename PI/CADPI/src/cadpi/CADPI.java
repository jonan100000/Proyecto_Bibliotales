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
        conexion = DriverManager.getConnection("jdbc:oracle:thin:@172.16.202.1:1521:test", "PI", "kk");
        }   catch (SQLException ex) {
            ExcepcionPI e = new ExcepcionPI();
             e.setCodigoErrorBD(ex.getErrorCode());
             e.setMensajeErrorBD(ex.getMessage());
             e.setMensajeErrorUsuario("Error general del sistema, consulte con el administrador");
           throw e; 
        }
    }
    
   
    
}
