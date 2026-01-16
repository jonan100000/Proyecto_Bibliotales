package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: usuario
 * Campos: id_usuario, nombre_usuario, correo, contraseña,
 *         tipo_usuario, puntos, fecha_registro, fecha_nacimiento
 */
data class Usuario(
    val id_usuario: Int,  // Este no debería cambiar
    var nombre_usuario: String,  // Cambiado a var
    var correo: String,          // Cambiado a var
    var contraseña: String,      // Cambiado a var
    val tipo_usuario: String,    // Este no debería cambiar
    var puntos: Int,             // Cambiado a var
    val fecha_registro: String,  // Este no debería cambiar
    var fecha_nacimiento: String // Cambiado a var
)