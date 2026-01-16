package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: reseria (reseña)
 * Campos: id_resenia, puntuacion_resena, comentario_resena,
 *         fecha_resena, id_libro, id_usuario
 */
data class Resenia(
    val id_resenia: Int,
    val puntuacion_resena: Int,      // 1-5
    val comentario_resena: String,
    val fecha_resena: String,        // Formato: YYYY-MM-DD
    val id_libro: Int,               // FK a libro
    val id_usuario: Int              // FK a usuario
)