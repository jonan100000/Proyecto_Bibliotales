package com.proyecto.bibliotales.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.data.models.Usuario

class UserRepository(private val context: Context) {

    fun getUsuarios(): List<Usuario> {
        return try {
            val json = context.assets
                .open("data/usuarios.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<Map<String, List<Usuario>>>() {}.type
            val data: Map<String, List<Usuario>> = Gson().fromJson(json, type)

            data["usuarios"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun buscarUsuario(email: String, password: String): Usuario? {
        return getUsuarios().find {
            it.correo == email && it.contraseña == password
        }
    }
}
