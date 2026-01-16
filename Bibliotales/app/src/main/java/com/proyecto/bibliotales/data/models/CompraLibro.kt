package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: compra_libro
 * Campos: id_compra, id_usuario, id_libro, fecha_libro_compra
 */
data class CompraLibro(
    val id_compra: Int,
    val id_usuario: Int,      // FK a usuario
    val id_libro: Int,        // FK a libro
    val fecha_libro_compra: String  // Formato: YYYY-MM-DD
)