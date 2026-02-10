package com.proyecto.bibliotales.data.models

// 1. Creamos un enum para establecer el entorno
enum class Entorno {
    NORMAL, REMOTO
}

/*
* CONFIGURACION DE CLIENTE
* ========================
* Esta clase realiza la configuracion de red de la App
* */

val ENTORNO_ACTUAL = Entorno.NORMAL

object ClienteConfig {
    // Establecemos la IP del servidor en funcion del entorno
    fun getServerIP(): String {
        return when(ENTORNO_ACTUAL) {
            // 10.0.2.2
            Entorno.NORMAL -> "10.0.2.2"

            // IP de nuestro PC: conexion desde la red o la publica desde fuera (192...)
            Entorno.REMOTO -> "172.15.54.183"

        }
    }

    // Configuramos el puerto
    const val PUERTO_SERVIDOR = 5000

}

