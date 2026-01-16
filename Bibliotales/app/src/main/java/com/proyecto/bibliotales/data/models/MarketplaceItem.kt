package com.proyecto.bibliotales.data.models

data class ItemMarketplace(
    val id_item: Int,
    val nombre_item: String,
    val descripcion: String,
    val costo_puntos: Int,
    val tipo_item: String, // "background" o "marco"
    val id_usuario: Int? // null si es global
)