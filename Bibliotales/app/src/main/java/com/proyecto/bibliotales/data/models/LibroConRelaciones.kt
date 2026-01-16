package com.proyecto.bibliotales.data.models

/**
 * Clase para representar un libro con sus relaciones
 * Útil para consultas que necesitan datos de múltiples tablas
 */
data class LibroConRelaciones(
    val libro: Libro,
    val autor: Usuario? = null,
    val tipoLibro: TipoLibro? = null,
    val genero: Genero? = null,
    val promedioResenias: Double = 0.0
)