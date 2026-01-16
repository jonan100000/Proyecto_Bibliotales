package com.proyecto.bibliotales.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.Libro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Biblioteca : BaseActivity() {

    private lateinit var bibliotecaRecyclerView: RecyclerView
    private lateinit var btnSubirLibro: Button

    private var librosList: List<Libro> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()
    private val gson = Gson() // Añadir esta línea para tener la instancia de Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Establecer el layout usando BaseActivity
        setContentLayout(R.layout.biblioteca)

        // Inicializar RecyclerView
        bibliotecaRecyclerView = findViewById(R.id.bibliotecaRecyclerView)
        btnSubirLibro = findViewById(R.id.btnSubirLibro)

        // Configurar LayoutManager horizontal
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bibliotecaRecyclerView.layoutManager = layoutManager

        // Configurar el botón para subir libro
        btnSubirLibro.setOnClickListener {
            val intent = Intent(this, SubirLibro::class.java)
            startActivity(intent)
        }

        // Verificar si el usuario está logueado
        if (!sessionManager.isLogged()) {
            return
        }

        // Cargar datos
        lifecycleScope.launch {
            val (libros, compras) = withContext(Dispatchers.IO) {
                val librosCargados = cargarLibrosDesdeJSONSeguro()
                val comprasCargadas = cargarComprasDesdeJSONSeguro()
                Pair(librosCargados, comprasCargadas)
            }

            librosList = libros
            comprasList = compras
            configurarBiblioteca()
        }
    }

    private fun cargarLibrosDesdeJSONSeguro(): List<Libro> {
        val librosJSON = try {
            val jsonString = assets.open("data/libros.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Libro>>>() {}.type
            val data: Map<String, List<Libro>> = gson.fromJson(jsonString, type)
            data["libros"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // Agregar libros temporales
        val librosTemporales = cargarLibrosTemporales()

        return librosJSON + librosTemporales
    }

    private fun cargarComprasDesdeJSONSeguro(): List<CompraLibro> {
        return try {
            val jsonString = assets.open("data/compras_libro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<CompraLibro>>>() {}.type
            val data: Map<String, List<CompraLibro>> = gson.fromJson(jsonString, type)
            data["compras_libro"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Función para cargar libros temporales desde SharedPreferences
    private fun cargarLibrosTemporales(): List<Libro> {
        return try {
            val prefs = getSharedPreferences("libros_temporales", Context.MODE_PRIVATE)
            val json = prefs.getString("libros", "[]") ?: "[]"
            val type = object : TypeToken<List<Libro>>() {}.type
            gson.fromJson<List<Libro>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Función para cargar compras temporales desde SharedPreferences
    private fun cargarComprasTemporales(): List<CompraLibro> {
        return try {
            val prefs = getSharedPreferences("libros_temporales", Context.MODE_PRIVATE)
            val json = prefs.getString("compras", "[]") ?: "[]"
            val type = object : TypeToken<List<CompraLibro>>() {}.type
            gson.fromJson<List<CompraLibro>>(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun configurarBiblioteca() {
        // Obtener el ID del usuario actual
        val idUsuarioActual = sessionManager.getUser()?.id_usuario ?: -1

        if (idUsuarioActual == -1) {
            return
        }

        // Obtener TODAS las compras del usuario (JSON + temporales de sesión + temporales de libros)
        val comprasJSON = comprasList.filter { it.id_usuario == idUsuarioActual }
        val comprasTemp = sessionManager.getTemporalPurchases().filter { it.id_usuario == idUsuarioActual }
        val comprasTemporalesLibros = cargarComprasTemporales().filter { it.id_usuario == idUsuarioActual }

        val todasCompras = comprasJSON + comprasTemp + comprasTemporalesLibros

        if (todasCompras.isEmpty()) {
            // No hay libros comprados
            return
        }

        // Obtener IDs de libros comprados
        val librosCompradosIds = todasCompras.map { it.id_libro }

        // Filtrar libros que el usuario ha comprado (incluyendo temporales)
        val librosBiblioteca = librosList.filter { it.id_libro in librosCompradosIds }

        if (librosBiblioteca.isEmpty()) {
            return
        }

        // Crear mapa de fecha de compra por libro para ordenar
        val fechaCompraPorLibro = todasCompras.associate { it.id_libro to it.fecha_libro_compra }

        // Ordenar libros por fecha de compra (más reciente primero) y luego por ID
        val librosOrdenados = librosBiblioteca.sortedWith(
            compareByDescending<Libro> { libro ->
                fechaCompraPorLibro[libro.id_libro]
            }.thenByDescending { it.id_libro }
        )

        // Configurar RecyclerView
        val adapter = BibliotecaAdapter(librosOrdenados)
        bibliotecaRecyclerView.adapter = adapter
    }

    private fun cargarImagenPortada(libro: Libro, imageView: ImageView) {
        // Para libros temporales, usar el campo "portada" o default
        val nombrePortada = if (libro.portada.isNotEmpty()) libro.portada else "portada_default.jpg"

        // Intentar cargar desde assets
        imageView.load("file:///android_asset/portadas/${nombrePortada}") {
            placeholder(R.drawable.portada_default)
            error(R.drawable.portada_default)
            size(300, 450)
            crossfade(true)
        }
    }

    private fun goToLibrosActivity(libro: Libro) {
        val intent = Intent(this, Libros::class.java)
        intent.putExtra("LIBRO_ID", libro.id_libro)
        startActivity(intent)
    }

    // Recargar cuando vuelva a la actividad para mostrar compras nuevas
    override fun onResume() {
        super.onResume()
        if (sessionManager.isLogged()) {
            lifecycleScope.launch {
                // Recargar compras del JSON
                comprasList = withContext(Dispatchers.IO) {
                    cargarComprasDesdeJSONSeguro()
                }
                // Reconfigurar biblioteca con datos actualizados
                configurarBiblioteca()
            }
        }
    }

    inner class BibliotecaAdapter(private val libros: List<Libro>) :
        RecyclerView.Adapter<BibliotecaAdapter.BibliotecaViewHolder>() {

        inner class BibliotecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val libro = libros[position]
                        goToLibrosActivity(libro)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_libro, parent, false)
            return BibliotecaViewHolder(view)
        }

        override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
            val libro = libros[position]

            // Configurar título
            holder.titleTextView.text = libro.titulo

            // Cargar imagen de portada
            cargarImagenPortada(libro, holder.imageView)
        }

        override fun getItemCount(): Int = libros.size
    }
}