package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.MensajeForo
import com.proyecto.bibliotales.data.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PerfilUsuario : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.perfil_usuario)

        // Verificar si el usuario está logueado
        if (!sessionManager.isLogged()) {
            Log.d("PerfilUsuario", "Usuario no logueado, redirigiendo a Login")
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Configurar listeners
        configurarListeners()
    }

    private fun configurarListeners() {
        // Clic en "Libros Comprados" para ir a la biblioteca
        findViewById<androidx.cardview.widget.CardView>(R.id.cardBooks).setOnClickListener {
            try {
                val intent = Intent(this, Biblioteca::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("PerfilUsuario", "Error al abrir Biblioteca: ${e.message}")
            }
        }

        // Botón para editar perfil
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditProfile).setOnClickListener {
            try {
                val intent = Intent(this, EditarUsuario::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("PerfilUsuario", "Error al abrir EditarUsuario: ${e.message}")
            }
        }
    }

    private fun cargarDatosUsuario() {
        val usuario = sessionManager.getUser()

        if (usuario == null) {
            Log.e("PerfilUsuario", "Usuario es nulo a pesar de estar logueado")
            sessionManager.logout()
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        try {
            // Mostrar datos del usuario
            findViewById<android.widget.TextView>(R.id.tvUsername).text = usuario.nombre_usuario
            findViewById<android.widget.TextView>(R.id.tvEmail).text = usuario.correo
            findViewById<android.widget.TextView>(R.id.tvPoints).text = usuario.puntos.toString()
            findViewById<android.widget.TextView>(R.id.tvRegistrationDate).text = usuario.fecha_registro

            // Cargar descripción guardada
            val descripcion = sessionManager.getUserDescription(usuario.id_usuario)
            findViewById<android.widget.TextView>(R.id.tvDescription).text =
                descripcion ?: "¡Hola! Soy un apasionado de la lectura. Me encanta explorar nuevos mundos a través de los libros."

            // Aplicar marco y background guardados
            aplicarMarcoYBackground(usuario)

            // Cargar estadísticas en segundo plano
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Cargar datos desde JSON
                    val totalLibros = calcularLibrosComprados(usuario.id_usuario)
                    val totalComentarios = calcularComentariosForo(usuario.id_usuario)

                    // Actualizar UI en el hilo principal
                    launch(Dispatchers.Main) {
                        findViewById<android.widget.TextView>(R.id.tvBooksCount).text = "($totalLibros)"
                        findViewById<android.widget.TextView>(R.id.tvForumComments).text = totalComentarios.toString()
                    }
                } catch (e: Exception) {
                    Log.e("PerfilUsuario", "Error en corrutina: ${e.message}")
                    launch(Dispatchers.Main) {
                        findViewById<android.widget.TextView>(R.id.tvBooksCount).text = "(0)"
                        findViewById<android.widget.TextView>(R.id.tvForumComments).text = "0"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PerfilUsuario", "Error al cargar datos: ${e.message}")
        }
    }

    private fun aplicarMarcoYBackground(usuario: Usuario) {
        val frameId = sessionManager.getEquippedItem(usuario.id_usuario, "marco")
        val backgroundId = sessionManager.getEquippedItem(usuario.id_usuario, "background")

        aplicarMarco(frameId)
        aplicarBackground(backgroundId)
    }

    private fun aplicarMarco(marcoId: Int) {
        // Definir los marcos disponibles (debe coincidir con EditarUsuario)
        val marcosDisponibles = listOf(
            ItemDecoracion(-1, R.drawable.borde_perfil, "Marco por defecto", "marco"),
            ItemDecoracion(1, R.color.marco_azul, "Marco Azul", "marco"),
            ItemDecoracion(2, R.color.marco_verde, "Marco Verde", "marco")
        )

        val marco = marcosDisponibles.find { it.id == marcoId }
        val profileContainer = findViewById<android.widget.RelativeLayout>(R.id.profileContainer)

        marco?.let {
            if (it.recurso == R.drawable.borde_perfil) {
                // Marco por defecto (drawable)
                profileContainer.setBackgroundResource(it.recurso)
                profileContainer.setPadding(4, 4, 4, 4)
            } else {
                // Marco de color
                try {
                    val color = ContextCompat.getColor(this, it.recurso)
                    profileContainer.setBackgroundColor(color)
                    profileContainer.setPadding(4, 4, 4, 4)
                } catch (e: Exception) {
                    // Si falla, usar el marco por defecto
                    profileContainer.setBackgroundResource(R.drawable.borde_perfil)
                }
            }
        } ?: run {
            // Si no hay marco seleccionado, usar el por defecto
            profileContainer.setBackgroundResource(R.drawable.borde_perfil)
        }
    }

    private fun aplicarBackground(backgroundId: Int) {
        // Definir los backgrounds disponibles (debe coincidir con EditarUsuario)
        val backgroundsDisponibles = listOf(
            ItemDecoracion(-1, R.color.background_dark, "Background por defecto", "background"),
            ItemDecoracion(1, R.color.background_gris_oscuro, "Background Gris Oscuro", "background"),
            ItemDecoracion(2, R.color.background_gris_claro, "Background Gris Claro", "background"),
            ItemDecoracion(3, R.color.background_naranja, "Background Naranja", "background"),
            ItemDecoracion(4, R.color.marco_azul, "Background Azul", "background")
        )

        val background = backgroundsDisponibles.find { it.id == backgroundId }

        background?.let {
            try {
                val color = ContextCompat.getColor(this, it.recurso)
                // Aplicar al contenido principal
                findViewById<android.view.View>(android.R.id.content).setBackgroundColor(color)
            } catch (e: Exception) {
                // Si falla, usar el background por defecto
                findViewById<android.view.View>(android.R.id.content).setBackgroundColor(
                    ContextCompat.getColor(this, R.color.background_dark)
                )
            }
        } ?: run {
            // Si no hay background seleccionado, usar el por defecto
            findViewById<android.view.View>(android.R.id.content).setBackgroundColor(
                ContextCompat.getColor(this, R.color.background_dark)
            )
        }
    }

    private suspend fun calcularLibrosComprados(idUsuario: Int): Int {
        return try {
            // Cargar compras desde JSON
            val jsonString = assets.open("data/compras_libro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<CompraLibro>>>() {}.type
            val data: Map<String, List<CompraLibro>> = Gson().fromJson(jsonString, type)
            val compras = data["compras_libro"] ?: emptyList()

            // Filtrar por usuario
            val comprasUsuario = compras.filter { it.id_usuario == idUsuario }

            // Sumar compras temporales
            val comprasTemp = sessionManager.getTemporalPurchases().filter { it.id_usuario == idUsuario }

            comprasUsuario.size + comprasTemp.size
        } catch (e: Exception) {
            Log.e("PerfilUsuario", "Error calcularLibrosComprados: ${e.message}")
            0
        }
    }

    private suspend fun calcularComentariosForo(idUsuario: Int): Int {
        return try {
            // Cargar mensajes desde JSON
            val jsonString = assets.open("data/mensajes_foro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<MensajeForo>>>() {}.type
            val data: Map<String, List<MensajeForo>> = Gson().fromJson(jsonString, type)
            val mensajes = data["mensajes_foro"] ?: emptyList()

            // Filtrar por usuario
            mensajes.count { it.id_usuario == idUsuario }
        } catch (e: Exception) {
            Log.e("PerfilUsuario", "Error calcularComentariosForo: ${e.message}")
            0
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualizar datos cuando se regrese de editar perfil
        if (sessionManager.isLogged()) {
            cargarDatosUsuario()
        }
    }

    companion object {
        private const val TAG = "PerfilUsuario"
    }
}

// Clase auxiliar para manejar items de decoración
data class ItemDecoracion(
    val id: Int,
    val recurso: Int,
    val nombre: String,
    val tipo: String
)