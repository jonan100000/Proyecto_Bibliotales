package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.Libro
import com.proyecto.bibliotales.data.models.Genero
import com.proyecto.bibliotales.data.models.TipoLibro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FiltroLibros : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textoResultados: TextView
    private lateinit var layoutSinResultados: LinearLayout
    private lateinit var editTextBusqueda: EditText
    private lateinit var botonLimpiarBusqueda: ImageView

    private var librosList: List<Libro> = emptyList()
    private var generosList: List<Genero> = emptyList()
    private var tiposList: List<TipoLibro> = emptyList()

    // Mapa para acceso rápido a nombres de género y tipo por ID
    private val generoMap = mutableMapOf<Int, String>()
    private val tipoMap = mutableMapOf<Int, String>()

    // Filtro actual
    private var textoBusqueda: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.filtro_libros)

        inicializarVistas()
        configurarRecyclerView()
        configurarListeners()

        // Cargar datos
        lifecycleScope.launch {
            cargarDatos()
        }
    }

    private fun inicializarVistas() {
        recyclerView = findViewById(R.id.recyclerViewResultados)
        textoResultados = findViewById(R.id.textoResultados)
        layoutSinResultados = findViewById(R.id.layoutSinResultados)
        editTextBusqueda = findViewById(R.id.editTextBusqueda)
        botonLimpiarBusqueda = findViewById(R.id.botonLimpiarBusqueda)
    }

    private fun configurarRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = LibroAdapter(emptyList())
    }

    private fun configurarListeners() {
        // Búsqueda por texto
        editTextBusqueda.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textoBusqueda = s.toString()
                botonLimpiarBusqueda.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                filtrarLibros()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        botonLimpiarBusqueda.setOnClickListener {
            editTextBusqueda.setText("")
            textoBusqueda = ""
            botonLimpiarBusqueda.visibility = View.GONE
            mostrarTodosLosLibros()
        }
    }

    private suspend fun cargarDatos() {
        val (libros, generos, tipos) = withContext(Dispatchers.IO) {
            val librosCargados = cargarLibrosDesdeJSONSeguro()
            val generosCargados = cargarGenerosDesdeJSONSeguro()
            val tiposCargados = cargarTiposDesdeJSONSeguro()
            Triple(librosCargados, generosCargados, tiposCargados)
        }

        librosList = libros
        generosList = generos
        tiposList = tipos

        // Crear mapas para acceso rápido
        generosList.forEach { generoMap[it.id_genero] = it.nombre_genero }
        tiposList.forEach { tipoMap[it.id_tipo] = it.nombre_tipo }

        withContext(Dispatchers.Main) {
            mostrarTodosLosLibros()
        }
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

    private fun cargarGenerosDesdeJSONSeguro(): List<Genero> {
        return try {
            val jsonString = assets.open("data/generos.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Genero>>>() {}.type
            val data: Map<String, List<Genero>> = Gson().fromJson(jsonString, type)
            data["generos"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarTiposDesdeJSONSeguro(): List<TipoLibro> {
        return try {
            val jsonString = assets.open("data/tipos_libro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<TipoLibro>>>() {}.type
            val data: Map<String, List<TipoLibro>> = Gson().fromJson(jsonString, type)
            data["tipos_libro"] ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun filtrarLibros() {
        if (textoBusqueda.isEmpty()) {
            mostrarTodosLosLibros()
            return
        }

        val busquedaNormalizada = textoBusqueda.lowercase().trim()

        val librosFiltrados = librosList.filter { libro ->
            // Obtener nombres de género y tipo para este libro
            val nombreGenero = generoMap[libro.id_genero] ?: ""
            val nombreTipo = tipoMap[libro.id_tipo] ?: ""

            // Buscar en todos los campos
            libro.titulo.lowercase().contains(busquedaNormalizada) ||
                    libro.autor.lowercase().contains(busquedaNormalizada) ||
                    nombreGenero.lowercase().contains(busquedaNormalizada) ||
                    nombreTipo.lowercase().contains(busquedaNormalizada)
        }

        mostrarResultados(librosFiltrados)
    }

    private fun mostrarResultados(libros: List<Libro>) {
        if (libros.isEmpty()) {
            recyclerView.visibility = View.GONE
            layoutSinResultados.visibility = View.VISIBLE

            val mensaje = if (textoBusqueda.isNotEmpty()) {
                "No se encontraron libros para '$textoBusqueda'"
            } else {
                "No hay libros disponibles"
            }

            val textoMensaje = findViewById<TextView>(R.id.textoMensaje)
            textoMensaje.text = mensaje
        } else {
            recyclerView.visibility = View.VISIBLE
            layoutSinResultados.visibility = View.GONE

            (recyclerView.adapter as LibroAdapter).actualizarLibros(libros)
        }

        // Actualizar contador de resultados
        textoResultados.text = if (textoBusqueda.isEmpty()) {
            "Todos los libros (${libros.size})"
        } else {
            "${libros.size} libro${if (libros.size != 1) "s" else ""} encontrado${if (libros.size != 1) "s" else ""} para '$textoBusqueda'"
        }
    }

    private fun mostrarTodosLosLibros() {
        mostrarResultados(librosList)
    }

    private fun irADetalleLibro(libroId: Int) {
        val intent = Intent(this, Libros::class.java)
        intent.putExtra("LIBRO_ID", libroId)
        startActivity(intent)
    }

    // Adapter para RecyclerView
    inner class LibroAdapter(private var libros: List<Libro>) : RecyclerView.Adapter<LibroAdapter.LibroViewHolder>() {

        inner class LibroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.libroImageView)
            val titleTextView: TextView = itemView.findViewById(R.id.libroTitleTextView)
            val autorTextView: TextView = itemView.findViewById(R.id.libroAutorTextView)
            val generoTextView: TextView = itemView.findViewById(R.id.libroGeneroTextView)
            val tipoTextView: TextView = itemView.findViewById(R.id.libroTipoTextView)
            val descripcionTextView: TextView = itemView.findViewById(R.id.libroDescripcionTextView)

            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val libro = libros[position]
                        irADetalleLibro(libro.id_libro)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_libro_filtro, parent, false)
            return LibroViewHolder(view)
        }

        override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
            val libro = libros[position]

            // Configurar datos
            holder.titleTextView.text = libro.titulo
            holder.autorTextView.text = "Autor: ${libro.autor}"

            // Obtener nombre del género y tipo desde los mapas
            holder.generoTextView.text = generoMap[libro.id_genero] ?: "Desconocido"
            holder.tipoTextView.text = tipoMap[libro.id_tipo] ?: "Desconocido"

            holder.descripcionTextView.text = libro.descripcion

            // Cargar imagen
            holder.imageView.load("file:///android_asset/portadas/${libro.portada}") {
                placeholder(R.drawable.portada_default)
                error(R.drawable.portada_default)
                crossfade(true)
            }
        }

        override fun getItemCount(): Int = libros.size

        fun actualizarLibros(nuevosLibros: List<Libro>) {
            libros = nuevosLibros
            notifyDataSetChanged()
        }
    }
}