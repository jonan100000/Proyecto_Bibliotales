package com.proyecto.bibliotales.data.session

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.data.models.CompraItem
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.Usuario

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "usuario_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val editor: SharedPreferences.Editor = prefs.edit()

    fun isLogged(): Boolean = prefs.getBoolean("logueado", false)

    fun logout() {
        clearTemporalPurchases()
        clearTemporalItemPurchases()
        clearEquippedItems()
        editor.clear()
        editor.apply()
    }

    fun saveUser(usuario: Usuario) {
        editor.apply {
            putBoolean("logueado", true)
            putInt("id_usuario", usuario.id_usuario)
            putString("nombre_usuario", usuario.nombre_usuario)
            putString("correo", usuario.correo)
            putString("contraseña", usuario.contraseña)
            putString("tipo_usuario", usuario.tipo_usuario)
            putInt("puntos", usuario.puntos)
            putString("fecha_registro", usuario.fecha_registro)
            putString("fecha_nacimiento", usuario.fecha_nacimiento)
            apply()
        }
    }

    fun getUser(): Usuario? {
        if (!isLogged()) {
            Log.d("SessionManager", "Usuario no logueado")
            return null
        }

        return try {
            Usuario(
                id_usuario = prefs.getInt("id_usuario", -1),
                nombre_usuario = prefs.getString("nombre_usuario", "") ?: "",
                correo = prefs.getString("correo", "") ?: "",
                contraseña = prefs.getString("contraseña", "") ?: "",
                tipo_usuario = prefs.getString("tipo_usuario", "1") ?: "1",
                puntos = prefs.getInt("puntos", 0),
                fecha_registro = prefs.getString("fecha_registro", "") ?: "",
                fecha_nacimiento = prefs.getString("fecha_nacimiento", "") ?: ""
            ).also {
                Log.d("SessionManager", "Usuario obtenido: ${it.nombre_usuario}")
            }
        } catch (e: Exception) {
            Log.e("SessionManager", "Error al obtener usuario: ${e.message}")
            null
        }
    }

    // Funciones para manejar compras temporales de libros
    fun addTemporalPurchase(compra: CompraLibro) {
        val comprasTemp = getTemporalPurchases().toMutableList()
        comprasTemp.add(compra)
        editor.putString("compras_temporales", gson.toJson(comprasTemp))
        editor.apply()
    }

    fun getTemporalPurchases(): List<CompraLibro> {
        val json = prefs.getString("compras_temporales", "[]")
        return try {
            val type = object : TypeToken<List<CompraLibro>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearTemporalPurchases() {
        editor.remove("compras_temporales")
        editor.apply()
    }

    // Funciones para manejar puntos del usuario
    fun updateUserPoints(newPoints: Int) {
        editor.putInt("puntos", newPoints)
        editor.apply()
    }

    fun getUserPoints(): Int = prefs.getInt("puntos", 0)

    // Funciones para manejar items temporales comprados
    fun addTemporalItemPurchase(compra: CompraItem) {
        val comprasTemp = getTemporalItemPurchases().toMutableList()
        comprasTemp.add(compra)
        editor.putString("compras_items_temporales", gson.toJson(comprasTemp))
        editor.apply()
    }

    fun getTemporalItemPurchases(): List<CompraItem> {
        val json = prefs.getString("compras_items_temporales", "[]")
        return try {
            val type = object : TypeToken<List<CompraItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearTemporalItemPurchases() {
        editor.remove("compras_items_temporales")
        editor.apply()
    }

    // Funciones para guardar y obtener el item equipado
    fun saveEquippedItem(usuarioId: Int, itemId: Int, tipo: String) {
        editor.putInt("equipped_${usuarioId}_$tipo", itemId)
        editor.apply()
    }

    fun getEquippedItem(usuarioId: Int, tipo: String): Int {
        return prefs.getInt("equipped_${usuarioId}_$tipo", -1)
    }

    // Función para limpiar items equipados al cerrar sesión
    fun clearEquippedItems() {
        val keysToRemove = prefs.all.keys.filter { it.startsWith("equipped_") }
        val edit = prefs.edit()
        keysToRemove.forEach { edit.remove(it) }
        edit.apply()
    }

    // Funciones para la descripción del usuario
    fun saveUserDescription(usuarioId: Int, descripcion: String) {
        editor.putString("descripcion_$usuarioId", descripcion)
        editor.apply()
    }

    fun getUserDescription(usuarioId: Int): String? {
        return prefs.getString("descripcion_$usuarioId", null)
    }

    // Funciones para la imagen de perfil
    fun saveProfileImage(usuarioId: Int, imageResource: String) {
        editor.putString("profile_image_$usuarioId", imageResource)
        editor.apply()
    }

    fun getProfileImage(usuarioId: Int): String? {
        return prefs.getString("profile_image_$usuarioId", "goku_pelado")
    }

    fun saveUnlockedItem(usuarioId: Int, itemId: Int, tipo: String) {
        editor.putBoolean("unlocked_${usuarioId}_${tipo}_$itemId", true)
        editor.apply()
    }

    fun isItemUnlocked(usuarioId: Int, itemId: Int, tipo: String): Boolean {
        return prefs.getBoolean("unlocked_${usuarioId}_${tipo}_$itemId", false)
    }
}