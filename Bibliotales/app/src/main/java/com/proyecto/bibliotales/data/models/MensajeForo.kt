package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: mensaje_foro
 * Campos: id_mensaje, contenido, fecha_mensaje_foro,
 *         id_usuario, id_mensaje_padre, titulo, id_libro
 */
data class MensajeForo(
    val id_mensaje: Int,
    val contenido: String,
    val fecha_mensaje_foro: String,  // Formato: YYYY-MM-DD HH:MM:SS
    val id_usuario: Int,             // FK a usuario (autor)
    val id_mensaje_padre: Int?,      // FK a mensaje_foro (null si es mensaje raíz)
    val titulo: String?,
    val id_libro: Int?               // FK a libro (opcional, si el mensaje está asociado a un libro)
)