package com.proyecto.bibliotales.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.Libro
import com.proyecto.bibliotales.ui.viewmodels.ConexionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pojospi.Libro as LibroBD

class MainActivity : BaseActivity() {

    private var librosList: List<Libro> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()

    private lateinit var libraryRecyclerView: RecyclerView
    private lateinit var recommendedRecyclerView: RecyclerView
    private lateinit var newRecyclerView: RecyclerView

    private lateinit var viewStatus: View

    private var libroPopular: Libro? = null

    private val conexionViewModel: ConexionViewModel by viewModels()

    // Portadas desde BD (id -> nombreArchivo.jpg)
    private val portadasBD: MutableMap<Int, String> = mutableMapOf()

    // NUEVO: Libros completos desde BD (id -> pojospi.Libro)
    private val librosBD: MutableMap<Int, LibroBD> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (sessionManager.isLogged()) {
            setContentLayout(R.layout.activity_main_logueado)
        } else {
            setContentLayout(R.layout.activity_main)
        }

        // Inicializar vistas
        viewStatus = findViewById(R.id.viewStatus)

        if (sessionManager.isLogged()) {
            libraryRecyclerView = findViewById(R.id.libraryRecyclerView)
        }
        recommendedRecyclerView = findViewById(R.id.recommendedRecyclerView)
        newRecyclerView = findViewById(R.id.newRecyclerView)

        // Monitor conexión (PING)
        lifecycleScope.launch {
            conexionViewModel.estaConectado.collect { conectado ->
                viewStatus.setBackgroundColor(if (conectado) Color.GREEN else Color.RED)
            }
        }

        // Cargar JSON (para puntuación/ordenación y fallback)
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

        // Escuchar READALL_LIBRO (BD) para portadas + títulos/autor
        lifecycleScope.launch {
            conexionViewModel.librosState.collect { st ->
                if (st.exito && st.libros.isNotEmpty()) {
                    portadasBD.clear()
                    librosBD.clear()

                    st.libros.forEach { lb ->
                        val id = lb.id_libro ?: return@forEach
                        librosBD[id] = lb
                        val portada = lb.portada
                        if (!portada.isNullOrBlank()) {
                            portadasBD[id] = portada
                        }
                    }

                    // Repintar home (ahora también cambia texto si BD difiere)
                    configurarVistasConDatos()
                }
            }
        }

        // Pedir lista de libros al servidor
        conexionViewModel.leerTodosLibros()

        setupBookClicks()
    }

    // ------------------ CARGA JSON ------------------

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

    // ------------------ Helpers BD -> Texto ------------------

    private fun tituloBD(idLibro: Int, fallback: String): String {
        val lb = librosBD[idLibro]
        val t = lb?.titulo
        return if (!t.isNullOrBlank()) t else fallback
    }

    private fun autorBD(idLibro: Int, fallback: String): String {
        val lb = librosBD[idLibro]
        val a = lb?.usuario?.nombre_usuario
        return if (!a.isNullOrBlank()) a else fallback
    }

    // ------------------ UI HOME ------------------

    private fun configurarVistasConDatos() {
        if (librosList.isEmpty()) return

        // 1) Libro Popular (orden por JSON, texto por BD si existe)
        libroPopular = librosList.maxByOrNull { it.puntuacion_promedio }
        libroPopular?.let { libro ->
            val titulo = tituloBD(libro.id_libro, libro.titulo)
            val autor = autorBD(libro.id_libro, libro.autor)

            findViewById<TextView>(R.id.popularBookTitle)?.text = titulo
            findViewById<TextView>(R.id.popularBookPoints)?.text = "Puntuación: ${libro.puntuacion_promedio}"
            findViewById<TextView>(R.id.popularBookAuthor)?.text = "Autor: $autor"

            cargarImagenPortada(libro, findViewById(R.id.popularBookImage))
        }

        // 2) Recomendados (orden por JSON, texto por BD si existe)
        val recomendados = librosList.sortedByDescending { it.puntuacion_promedio }.take(10)
        recommendedRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recommendedRecyclerView.adapter = LibroAdapter(recomendados)

        // 3) Novedades (orden por JSON id)
        val novedades = librosList.sortedByDescending { it.id_libro }.take(10)
        newRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        newRecyclerView.adapter = LibroAdapter(novedades)

        // 4) Mi Biblioteca (sigue por compras del JSON/sesión)
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

            libraryRecyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            libraryRecyclerView.adapter = BibliotecaAdapter(bibliotecaCompleta)
        }
    }

    // ------------------ PORTADAS (SUPABASE) ------------------

    private fun cargarImagenPortada(libro: Libro?, imageView: ImageView?) {
        imageView?.let { view ->
            if (libro != null) {
                val portada = portadasBD[libro.id_libro] ?: libro.portada
                val url = "https://byoiqofayvaxwiapbdiq.supabase.co/storage/v1/object/public/portadas/$portada"

                view.load(url) {
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

    // ------------------ CLICKS ------------------

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

    // ------------------ ADAPTERS ------------------

    inner class LibroAdapter(private val libros: List<Libro>) :
        RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

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
            holder.titleTextView.text = tituloBD(libro.id_libro, libro.titulo)
            cargarImagenPortada(libro, holder.imageView)
        }

        override fun getItemCount(): Int = libros.size
    }

    inner class BibliotecaAdapter(private val libros: List<Libro?>) :
        RecyclerView.Adapter<BibliotecaAdapter.BibliotecaViewHolder>() {

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
                holder.titleTextView.text = tituloBD(libro.id_libro, libro.titulo)
                cargarImagenPortada(libro, holder.imageView)
            } else {
                holder.titleTextView.text = "Disponible"
                cargarImagenPortada(null, holder.imageView)
            }
        }

        override fun getItemCount(): Int = libros.size
    }
}