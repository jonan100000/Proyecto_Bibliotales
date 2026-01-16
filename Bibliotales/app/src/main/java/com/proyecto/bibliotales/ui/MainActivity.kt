package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class MainActivity : BaseActivity() {
    private var librosList: List<Libro> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()
    private lateinit var libraryRecyclerView: RecyclerView
    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var newRecyclerView: RecyclerView

    // Agregar variable para libro popular
    private var libroPopular: Libro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isLogged()) {
            setContentLayout(R.layout.activity_main_logueado)
        } else {
            setContentLayout(R.layout.activity_main)
        }

        // Inicializar RecyclerViews
        if (sessionManager.isLogged()) {
            libraryRecyclerView = findViewById(R.id.libraryRecyclerView)
        }
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView)
        newRecyclerView = findViewById(R.id.newRecyclerView)

        lifecycleScope.launch {
            // Cargar datos en segundo plano
            val (libros, compras) = withContext(Dispatchers.IO) {
                val librosCargados = cargarLibrosDesdeJSONSeguro()
                val comprasCargadas = cargarComprasDesdeJSONSeguro()
                Pair(librosCargados, comprasCargadas)
            }

            librosList = libros
            comprasList = compras
            configurarVistasConDatos()
        }

        setupBookClicks()
    }

    private fun cargarLibrosDesdeJSONSeguro(): List<Libro> {
        return try {
            val jsonString = assets.open("data/libros.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Libro>>>() {}.type
            val data: Map<String, List<Libro>> = Gson().fromJson(jsonString, type)
            data["libros"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarComprasDesdeJSONSeguro(): List<CompraLibro> {
        return try {
            val jsonString = assets.open("data/compras_libro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<CompraLibro>>>() {}.type
            val data: Map<String, List<CompraLibro>> = Gson().fromJson(jsonString, type)
            data["compras_libro"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun configurarVistasConDatos() {
        if (librosList.isEmpty()) return

        // 1. Libro Popular: El de mayor puntuación
        libroPopular = librosList.maxByOrNull { it.puntuacion_promedio }
        libroPopular?.let { libro ->
            findViewById<TextView>(R.id.popularBookTitle)?.text = libro.titulo
            findViewById<TextView>(R.id.popularBookPoints)?.text = "Puntuación: ${libro.puntuacion_promedio}"
            findViewById<TextView>(R.id.popularBookAuthor)?.text = "Autor: ${libro.autor}"

            cargarImagenPortada(libro, findViewById(R.id.popularBookImage))
        }

        // 2. Libros Recomendados: Top 10 por puntuación
        val recomendados = librosList
            .sortedByDescending { it.puntuacion_promedio }
            .take(10)

        val recommendedAdapter = LibroAdapter(recomendados)
        recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView.adapter = recommendedAdapter

        // 3. Novedades: Top 10 por ID (más recientes)
        val novedades = librosList
            .sortedByDescending { it.id_libro }
            .take(10)

        val newAdapter = LibroAdapter(novedades)
        newRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newRecyclerView.adapter = newAdapter

        // 4. Mi Biblioteca (solo para usuarios logueados)
        if (sessionManager.isLogged()) {
            val idUsuarioActual = sessionManager.getUser()?.id_usuario ?: -1

            // Obtener TODAS las compras del usuario (JSON + temporales)
            val comprasJSON = comprasList.filter { it.id_usuario == idUsuarioActual }
            val comprasTemp = sessionManager.getTemporalPurchases().filter { it.id_usuario == idUsuarioActual }
            val todasCompras = comprasJSON + comprasTemp

            // Ordenar por id_compra descendente (más recientes primero)
            val comprasUsuario = todasCompras.sortedByDescending { it.id_compra }
                .take(10) // Tomar máximo 10

            // Obtener los libros correspondientes a las compras
            val librosCompradosIds = comprasUsuario.map { it.id_libro }
            val librosBiblioteca = librosList.filter { it.id_libro in librosCompradosIds }

            // Crear lista combinada manteniendo el orden de las compras
            val bibliotecaCompleta = mutableListOf<Libro?>()

            // Asegurar que los libros estén en el mismo orden que las compras
            comprasUsuario.forEach { compra ->
                val libro = librosBiblioteca.find { it.id_libro == compra.id_libro }
                if (libro != null) {
                    bibliotecaCompleta.add(libro)
                }
            }

            // Añadir elementos nulos para completar hasta 10 (si es necesario)
            while (bibliotecaCompleta.size < 10) {
                bibliotecaCompleta.add(null)
            }

            val libraryAdapter = BibliotecaAdapter(bibliotecaCompleta)
            libraryRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            libraryRecyclerView.adapter = libraryAdapter
        }
    }

    private fun cargarImagenPortada(libro: Libro?, imageView: ImageView?) {
        imageView?.let { view ->
            if (libro != null) {
                // Cargar imagen real usando el campo "portada" del JSON
                view.load("file:///android_asset/portadas/${libro.portada}") {
                    placeholder(R.drawable.portada_default)
                    error(R.drawable.portada_default)
                    size(300, 450)
                    crossfade(true)
                }
            } else {
                // Cargar placeholder
                view.setImageResource(R.drawable.portada_default)
            }
        }
    }

    private fun setupBookClicks() {
        findViewById<View>(R.id.popularBookImage)?.setOnClickListener {
            libroPopular?.let { libro ->
                goToLibrosActivity(libro)
            } ?: goToLibrosActivity()
        }
    }

    private fun goToLibrosActivity(libro: Libro? = null) {
        val intent = Intent(this, Libros::class.java)
        libro?.let {
            intent.putExtra("LIBRO_ID", it.id_libro)
        }
        startActivity(intent)
    }

    // Adapter para libros normales
    inner class LibroAdapter(private val libros: List<Libro>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

        inner class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)

            init {
                itemView.setOnClickListener {
                    val libro = libros[adapterPosition]
                    goToLibrosActivity(libro)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
            return LibroViewHolder(view)
        }

        override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
            val libro = libros[position]
            holder.titleTextView.text = libro.titulo
            cargarImagenPortada(libro, holder.imageView)
        }

        override fun getItemCount(): Int = libros.size
    }

    // Adapter especial para la biblioteca (con elementos vacíos)
    inner class BibliotecaAdapter(private val libros: List<Libro?>) : RecyclerView.Adapter<BibliotecaAdapter.BibliotecaViewHolder>() {

        inner class BibliotecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)

            init {
                itemView.setOnClickListener {
                    val libro = libros[adapterPosition]
                    if (libro != null) {
                        goToLibrosActivity(libro)
                    }
                    // Si es null (placeholder), no hacer nada
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_libro, parent, false)
            return BibliotecaViewHolder(view)
        }

        override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
            val libro = libros[position]

            if (libro != null) {
                // Libro real
                holder.titleTextView.text = libro.titulo
                cargarImagenPortada(libro, holder.imageView)
            } else {
                // Placeholder
                holder.titleTextView.text = "No hay más libros comprados"
                cargarImagenPortada(null, holder.imageView)
            }
        }

        override fun getItemCount(): Int = libros.size
    }
}