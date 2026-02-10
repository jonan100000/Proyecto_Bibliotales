package com.proyecto.bibliotales.ui

import android.content.Intent
import android.graphics.Color // Añadido para los colores del ping
import android.os.Bundle
import android.util.Log
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
// --- NUEVOS IMPORTS PARA LA COMUNICACIÓN ---
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import com.proyecto.bibliotales.ui.viewmodels.ConexionViewModel
import androidx.activity.viewModels

import kotlinx.coroutines.*

class MainActivity : BaseActivity() {
    private var librosList: List<Libro> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()
    private lateinit var libraryRecyclerView: RecyclerView
    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var newRecyclerView: RecyclerView

    // Vista para el estado de la conexión
    private lateinit var viewStatus: View

    private var libroPopular: Libro? = null

    // Obtenemos el ViewModel (necesitarás la dependencia de 'fragment-ktx' o 'activity-ktx')
    private val conexionViewModel: ConexionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isLogged()) {
            setContentLayout(R.layout.activity_main_logueado)
        } else {
            setContentLayout(R.layout.activity_main)
        }

        // 1. INICIALIZAR VISTAS (Incluyendo el indicador de estado)
        viewStatus = findViewById(R.id.viewStatus)

        if (sessionManager.isLogged()) {
            libraryRecyclerView = findViewById(R.id.libraryRecyclerView)
        }
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView)
        newRecyclerView = findViewById(R.id.newRecyclerView)

        // NOS SUSCRIBIMOS AL ESTADO
        lifecycleScope.launch {
            conexionViewModel.estaConectado.collect { conectado ->
                // Este código se ejecuta CADA VEZ que el valor cambie en el ViewModel
                viewStatus.setBackgroundColor(if (conectado) Color.GREEN else Color.RED)
            }
        }


        // 3. CARGA DE DATOS EXISTENTE
        lifecycleScope.launch {
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



    // --- EL RESTO DE TU CÓDIGO SE MANTIENE IGUAL ---

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

        // 1. Libro Popular
        libroPopular = librosList.maxByOrNull { it.puntuacion_promedio }
        libroPopular?.let { libro ->
            findViewById<TextView>(R.id.popularBookTitle)?.text = libro.titulo
            findViewById<TextView>(R.id.popularBookPoints)?.text = "Puntuación: ${libro.puntuacion_promedio}"
            findViewById<TextView>(R.id.popularBookAuthor)?.text = "Autor: ${libro.autor}"
            cargarImagenPortada(libro, findViewById(R.id.popularBookImage))
        }

        // 2. Recomendados
        val recomendados = librosList.sortedByDescending { it.puntuacion_promedio }.take(10)
        recommendedRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView.adapter = LibroAdapter(recomendados)

        // 3. Novedades
        val novedades = librosList.sortedByDescending { it.id_libro }.take(10)
        newRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newRecyclerView.adapter = LibroAdapter(novedades)

        // 4. Mi Biblioteca
        if (sessionManager.isLogged()) {
            val idUsuarioActual = sessionManager.getUser()?.id_usuario ?: -1
            val comprasJSON = comprasList.filter { it.id_usuario == idUsuarioActual }
            val comprasTemp = sessionManager.getTemporalPurchases().filter { it.id_usuario == idUsuarioActual }
            val todasCompras = comprasJSON + comprasTemp
            val comprasUsuario = todasCompras.sortedByDescending { it.id_compra }.take(10)

            val librosCompradosIds = comprasUsuario.map { it.id_libro }
            val librosBiblioteca = librosList.filter { it.id_libro in librosCompradosIds }

            val bibliotecaCompleta = mutableListOf<Libro?>()
            comprasUsuario.forEach { compra ->
                val libro = librosBiblioteca.find { it.id_libro == compra.id_libro }
                if (libro != null) bibliotecaCompleta.add(libro)
            }
            while (bibliotecaCompleta.size < 10) bibliotecaCompleta.add(null)

            libraryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            libraryRecyclerView.adapter = BibliotecaAdapter(bibliotecaCompleta)
        }
    }

    private fun cargarImagenPortada(libro: Libro?, imageView: ImageView?) {
        imageView?.let { view ->
            if (libro != null) {
                view.load("file:///android_asset/portadas/${libro.portada}") {
                    placeholder(R.drawable.portada_default)
                    error(R.drawable.portada_default)
                    size(300, 450)
                    crossfade(true)
                }
            } else {
                view.setImageResource(R.drawable.portada_default)
            }
        }
    }

    private fun setupBookClicks() {
        findViewById<View>(R.id.popularBookImage)?.setOnClickListener {
            libroPopular?.let { goToLibrosActivity(it) } ?: goToLibrosActivity()
        }
    }

    private fun goToLibrosActivity(libro: Libro? = null) {
        val intent = Intent(this, Libros::class.java)
        libro?.let { intent.putExtra("LIBRO_ID", it.id_libro) }
        startActivity(intent)
    }

    // --- ADAPTERS ---
    inner class LibroAdapter(private val libros: List<Libro>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {
        inner class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)
            init {
                itemView.setOnClickListener { goToLibrosActivity(libros[adapterPosition]) }
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

    inner class BibliotecaAdapter(private val libros: List<Libro?>) : RecyclerView.Adapter<BibliotecaAdapter.BibliotecaViewHolder>() {
        inner class BibliotecaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)
            init {
                itemView.setOnClickListener {
                    libros[adapterPosition]?.let { goToLibrosActivity(it) }
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
                holder.titleTextView.text = libro.titulo
                cargarImagenPortada(libro, holder.imageView)
            } else {
                holder.titleTextView.text = "Disponible"
                cargarImagenPortada(null, holder.imageView)
            }
        }
        override fun getItemCount(): Int = libros.size
    }
}