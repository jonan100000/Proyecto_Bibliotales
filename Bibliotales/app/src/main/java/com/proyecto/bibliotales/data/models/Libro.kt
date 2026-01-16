package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: libro
 * Campos según modelo relacional:
 * - id_libro, titulo, descripcion, fecha_publicacion, url_archivo,
 *   id_usuario, id_tipo, costo_dinero
 *
 * Campos adicionales del JSON actual:
 * - autor (string directo, en BD sería relación con usuario)
 * - id_genero, puntuacion_promedio, precio_puntos, disponible, portada
 */
data class Libro(
    // Campos del modelo relacional
    val id_libro: Int,
    val titulo: String,
    val descripcion: String,
    val fecha_publicacion: String,
    val url_archivo: String,
    val id_usuario: Int,  // Autor (FK a usuario)
    val id_tipo: Int,     // FK a tipo_libro
    val costo_dinero: Double,

    // Campos adicionales del JSON actual
    val autor: String = "",           // Temporal: para mostrar sin hacer join
    val id_genero: Int = 1,          // Temporal: FK a genero
    val puntuacion_promedio: Double = 0.0,
    val precio_puntos: Int = 0,
    val disponible: Boolean = true,
    val portada: String = ""
)