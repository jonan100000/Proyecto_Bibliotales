package com.proyecto.bibliotales.data.models

/**
 * Corresponde a la tabla: compra_liem (compra de ítems)
 * Campos: id_compra, id_usuario, id_item, fecha_item_compra
 */
data class CompraItem(
    val id_compra: Int,
    val id_usuario: Int,      // FK a usuario (comprador)
    val id_item: Int,         // FK a marketplace
    val fecha_item_compra: String  // Formato: YYYY-MM-DD
)