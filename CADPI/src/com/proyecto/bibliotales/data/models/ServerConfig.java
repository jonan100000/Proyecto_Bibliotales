/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proyecto.bibliotales.data.models;

import org.omg.CORBA.Environment;

/**
 *
 * @author santi
 */
public class ServerConfig {
    public static final Environment ENTORNO_ACTUAL = Environment.CLASE;
    
    public enum Environment {
        CASA, CLASE
    }
    
    public static String getDbIp() {
        switch (ENTORNO_ACTUAL) {
            case CASA:
                return "192.168.1.141";
                
            case CLASE:
                return "172.16.214.1";
                
            default:
                return "172.16.214.1";
               
        }
    }
    
    // Configuracion fija de la DB
    public static final String DB_PORT = "1521";
    public static final String DB_STD = "test";
    public static final String DB_USER = "bibliotales";
    public static final String DB_PASS = "kk";
    
    // Puerto de escucha del servidor
    public static final int SERVER_PORT = 5000;
    
}
