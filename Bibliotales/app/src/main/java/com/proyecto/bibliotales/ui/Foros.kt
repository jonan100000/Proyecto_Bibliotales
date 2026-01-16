package com.proyecto.bibliotales.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.MensajeForo
import com.proyecto.bibliotales.data.models.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Foros : BaseActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var entrada: EditText
    private lateinit var btnEnviar: Button

    private var libroId: Int = -1
    private var libroTitulo: String = ""
    private var mensajesList: List<MensajeForo> = emptyList()
    private var usuariosList: List<Usuario> = emptyList()
    private var mensajesTemporales: MutableList<MensajeForo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.foros)

        // Obtener el ID y título del libro del intent
        libroId = intent.getIntExtra("LIBRO_ID", -1)
        libroTitulo = intent.getStringExtra("LIBRO_TITULO") ?: ""

        if (libroId == -1) {
            Toast.makeText(this, "Error: No se especificó el libro", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar título del foro con el nombre del libro
        val tituloForo = findViewById<TextView>(R.id.tituloForo)
        tituloForo.text = "Foro: $libroTitulo"

        contenedor = findViewById(R.id.contenedorMensajes)
        entrada = findViewById(R.id.entradaMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)

        // Cargar datos
        lifecycleScope.launch {
            val (mensajes, usuarios) = withContext(Dispatchers.IO) {
                val mensajesCargados = cargarMensajesDesdeJSONSeguro()
                val usuariosCargados = cargarUsuariosDesdeJSONSeguro()
                Pair(mensajesCargados, usuariosCargados)
            }

            mensajesList = mensajes
            usuariosList = usuarios

            // Filtrar mensajes por libro y cargarlos
            cargarMensajesDelLibro()
        }

        btnEnviar.setOnClickListener {
            val texto = entrada.text.toString()
            if (texto.isNotEmpty()) {
                enviarMensajeNuevo(texto)
                entrada.text.clear()
            }
        }
    }

    private fun cargarMensajesDesdeJSONSeguro(): List<MensajeForo> {
        return try {
            val jsonString = assets.open("data/mensajes_foro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<MensajeForo>>>() {}.type
            val data: Map<String, List<MensajeForo>> = Gson().fromJson(jsonString, type)
            data["mensajes_foro"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarUsuariosDesdeJSONSeguro(): List<Usuario> {
        return try {
            val jsonString = assets.open("data/usuarios.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Usuario>>>() {}.type
            val data: Map<String, List<Usuario>> = Gson().fromJson(jsonString, type)
            data["usuarios"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarMensajesDelLibro() {
        // Limpiar contenedor
        contenedor.removeAllViews()

        // Combinar mensajes del JSON y temporales
        val todosMensajes = mensajesList + mensajesTemporales

        // Filtrar mensajes por libro
        val mensajesDelLibro = todosMensajes.filter { it.id_libro == libroId }

        // Separar mensajes padres (id_mensaje_padre == null) y respuestas
        val mensajesPadre = mensajesDelLibro.filter { it.id_mensaje_padre == null }
        val respuestas = mensajesDelLibro.filter { it.id_mensaje_padre != null }

        // Ordenar mensajes padres por fecha (más reciente primero)
        val mensajesPadreOrdenados = mensajesPadre.sortedByDescending { it.fecha_mensaje_foro }

        // Para cada mensaje padre, crear la vista y agregar respuestas
        mensajesPadreOrdenados.forEach { mensajePadre ->
            crearMensajeEnUI(mensajePadre, respuestas, esPadre = true)
        }
    }

    private fun crearMensajeEnUI(mensaje: MensajeForo, respuestas: List<MensajeForo>, esPadre: Boolean) {
        val inflater = LayoutInflater.from(this)
        val tarjetaMensaje = inflater.inflate(R.layout.item_mensaje, contenedor, false) as MaterialCardView
        val tvPadre = tarjetaMensaje.findViewById<TextView>(R.id.texto_del_mensaje)
        val tvUsuario = tarjetaMensaje.findViewById<TextView>(R.id.usuario_mensaje)
        val tvFecha = tarjetaMensaje.findViewById<TextView>(R.id.fecha_mensaje)
        val contenedorHijos = tarjetaMensaje.findViewById<LinearLayout>(R.id.contenedor_hijos)

        // Configurar contenido
        tvPadre.text = mensaje.contenido

        // Obtener nombre de usuario
        val usuario = usuariosList.find { it.id_usuario == mensaje.id_usuario }
        tvUsuario.text = "Usuario: ${usuario?.nombre_usuario ?: "Desconocido"}"

        // Configurar fecha
        tvFecha.text = "Fecha: ${mensaje.fecha_mensaje_foro}"

        // Si es un mensaje padre, mostrar título si existe
        if (esPadre && !mensaje.titulo.isNullOrEmpty()) {
            val tvTitulo = tarjetaMensaje.findViewById<TextView>(R.id.titulo_mensaje)
            tvTitulo.text = mensaje.titulo
            tvTitulo.visibility = TextView.VISIBLE
        }

        // Configurar clic para responder (solo si el usuario está logueado)
        if (sessionManager.isLogged()) {
            tarjetaMensaje.setOnClickListener {
                mostrarDialogoRespuesta(mensaje.id_mensaje, contenedorHijos)
            }
        }

        // Agregar al contenedor
        contenedor.addView(tarjetaMensaje)

        // Buscar respuestas para este mensaje
        val respuestasDeEsteMensaje = respuestas.filter { it.id_mensaje_padre == mensaje.id_mensaje }
            .sortedBy { it.fecha_mensaje_foro }

        // Agregar cada respuesta
        respuestasDeEsteMensaje.forEach { respuesta ->
            crearRespuestaEnUI(respuesta, contenedorHijos)
        }
    }

    private fun crearRespuestaEnUI(respuesta: MensajeForo, contenedorHijos: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        val respuestaView = inflater.inflate(R.layout.item_respuesta, contenedorHijos, false)

        val tvRespuesta = respuestaView.findViewById<TextView>(R.id.texto_de_la_respuesta)
        val tvUsuario = respuestaView.findViewById<TextView>(R.id.usuario_respuesta)
        val tvFecha = respuestaView.findViewById<TextView>(R.id.fecha_respuesta)

        tvRespuesta.text = respuesta.contenido

        // Obtener nombre de usuario
        val usuario = usuariosList.find { it.id_usuario == respuesta.id_usuario }
        tvUsuario.text = "Usuario: ${usuario?.nombre_usuario ?: "Desconocido"}"

        // Configurar fecha
        tvFecha.text = "Fecha: ${respuesta.fecha_mensaje_foro}"

        // Configurar clic para responder (solo si el usuario está logueado)
        if (sessionManager.isLogged()) {
            respuestaView.setOnClickListener {
                mostrarDialogoRespuesta(respuesta.id_mensaje, contenedorHijos)
            }
        }

        contenedorHijos.addView(respuestaView)
    }

    private fun mostrarDialogoRespuesta(idMensajePadre: Int, contenedorHijos: LinearLayout) {
        if (!sessionManager.isLogged()) {
            Toast.makeText(this, "Debes iniciar sesión para responder", Toast.LENGTH_SHORT).show()
            return
        }

        val cajaTexto = EditText(this).apply {
            hint = "Escribe tu respuesta…"
            setTextColor(getColor(R.color.text_primary_dark))
            setPadding(32, 24, 32, 24)
            setBackgroundColor(getColor(R.color.surface_dark))
        }

        val contenedorDialog = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, 0)
            setBackgroundColor(getColor(R.color.surface_dark))
            addView(cajaTexto)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Responder mensaje")
            .setView(contenedorDialog)
            .setPositiveButton("Responder") { _, _ ->
                val respuesta = cajaTexto.text.toString()
                if (respuesta.isNotEmpty()) {
                    enviarRespuesta(idMensajePadre, respuesta, contenedorHijos)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        // ESTO ES LO CLAVE: cambiar el fondo del decorView del dialog
        dialog.window?.setBackgroundDrawableResource(R.color.surface_dark)

        dialog.show()
    }



    private fun enviarMensajeNuevo(texto: String) {
        if (!sessionManager.isLogged()) {
            Toast.makeText(this, "Debes iniciar sesión para enviar un mensaje", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoId = (mensajesList + mensajesTemporales).maxByOrNull { it.id_mensaje }?.id_mensaje ?: 0 + 1
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val usuarioActual = sessionManager.getUser()

        val nuevoMensaje = MensajeForo(
            id_mensaje = nuevoId,
            contenido = texto,
            fecha_mensaje_foro = fechaActual,
            id_usuario = usuarioActual?.id_usuario ?: 1,
            id_mensaje_padre = null,
            titulo = null, // Podríamos añadir un campo para título en el futuro
            id_libro = libroId
        )

        // Agregar a temporales
        mensajesTemporales.add(nuevoMensaje)

        // Recargar mensajes
        cargarMensajesDelLibro()
    }

    private fun enviarRespuesta(idMensajePadre: Int, texto: String, contenedorHijos: LinearLayout) {
        val nuevoId = (mensajesList + mensajesTemporales).maxByOrNull { it.id_mensaje }?.id_mensaje ?: 0 + 1
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val usuarioActual = sessionManager.getUser()

        val nuevaRespuesta = MensajeForo(
            id_mensaje = nuevoId,
            contenido = texto,
            fecha_mensaje_foro = fechaActual,
            id_usuario = usuarioActual?.id_usuario ?: 1,
            id_mensaje_padre = idMensajePadre,
            titulo = null,
            id_libro = libroId
        )

        // Agregar a temporales
        mensajesTemporales.add(nuevaRespuesta)

        // Recargar mensajes
        cargarMensajesDelLibro()
    }
}