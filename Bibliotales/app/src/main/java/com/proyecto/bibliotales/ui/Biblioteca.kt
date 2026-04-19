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

class Biblioteca : BaseActivity() {

    private lateinit var bibliotecaRecyclerView: RecyclerView
    private lateinit var btnSubirLibro: Button

    private var librosList: List<Libro> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()
    private val gson = Gson()

    // NUEVO: ViewModel y mapa id_libro -> portada desde BD
    private val conexionViewModel: ConexionViewModel by viewModels()
    private val portadasBD: MutableMap<Int, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.biblioteca)

        bibliotecaRecyclerView = findViewById(R.id.bibliotecaRecyclerView)
        btnSubirLibro = findViewById(R.id.btnSubirLibro)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bibliotecaRecyclerView.layoutManager = layoutManager

        btnSubirLibro.setOnClickListener {
            val intent = Intent(this, SubirLibro::class.java)
            startActivity(intent)
        }

        if (!sessionManager.isLogged()) return

        // NUEVO: Observa portadas desde BD y pide READALL_LIBRO
        observarPortadasDesdeBD()
        conexionViewModel.leerTodosLibros()

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

    // NUEVO
    private fun observarPortadasDesdeBD() {
        lifecycleScope.launch {
            conexionViewModel.librosState.collect { st ->
                if (st.exito && st.libros.isNotEmpty()) {
                    portadasBD.clear()
                    st.libros.forEach { libroBD ->
                        val id = libroBD.id_libro ?: return@forEach
                        val portada = libroBD.portada
                        if (!portada.isNullOrBlank()) {
                            portadasBD[id] = portada
                        }
                    }
                    // refresca el recycler para repintar portadas
                    bibliotecaRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
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
        val idUsuarioActual = sessionManager.getUser()?.id_usuario ?: -1
        if (idUsuarioActual == -1) return

        val comprasJSON = comprasList.filter { it.id_usuario == idUsuarioActual }
        val comprasTemp = sessionManager.getTemporalPurchases().filter { it.id_usuario == idUsuarioActual }
        val comprasTemporalesLibros = cargarComprasTemporales().filter { it.id_usuario == idUsuarioActual }

        val todasCompras = comprasJSON + comprasTemp + comprasTemporalesLibros
        if (todasCompras.isEmpty()) return

        val librosCompradosIds = todasCompras.map { it.id_libro }
        val librosBiblioteca = librosList.filter { it.id_libro in librosCompradosIds }
        if (librosBiblioteca.isEmpty()) return

        val fechaCompraPorLibro = todasCompras.associate { it.id_libro to it.fecha_libro_compra }

        val librosOrdenados = librosBiblioteca.sortedWith(
            compareByDescending<Libro> { libro ->
                fechaCompraPorLibro[libro.id_libro]
            }.thenByDescending { it.id_libro }
        )

        bibliotecaRecyclerView.adapter = BibliotecaAdapter(librosOrdenados)
    }

    // CAMBIADO: ahora usa portada de BD si existe y carga desde Supabase
    private fun cargarImagenPortada(libro: Libro, imageView: ImageView) {
        val portada = portadasBD[libro.id_libro]
            ?: (if (libro.portada.isNotEmpty()) libro.portada else "portada_default.jpg")

        val url = "https://byoiqofayvaxwiapbdiq.supabase.co/storage/v1/object/public/portadas/$portada"

        imageView.load(url) {
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

    override fun onResume() {
        super.onResume()
        if (sessionManager.isLogged()) {
            lifecycleScope.launch {
                comprasList = withContext(Dispatchers.IO) {
                    cargarComprasDesdeJSONSeguro()
                }
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
            holder.titleTextView.text = libro.titulo
            cargarImagenPortada(libro, holder.imageView)
        }

        override fun getItemCount(): Int = libros.size
    }
}