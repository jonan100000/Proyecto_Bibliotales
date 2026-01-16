package com.proyecto.bibliotales.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.Genero
import com.proyecto.bibliotales.data.models.Libro
import com.proyecto.bibliotales.data.models.TipoLibro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class Libros : BaseActivity() {

    private var libroId: Int = -1
    private lateinit var libroActual: Libro

    private var librosList: List<Libro> = emptyList()
    private var tiposList: List<TipoLibro> = emptyList()
    private var generosList: List<Genero> = emptyList()
    private var comprasList: List<CompraLibro> = emptyList()

    private lateinit var recyclerViewSimilares: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.libros)

        libroId = intent.getIntExtra("LIBRO_ID", -1)
        if (libroId == -1) {
            Toast.makeText(this, "Error al cargar el libro", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        recyclerViewSimilares = findViewById(R.id.recyclerViewLibrosSimilares)
        recyclerViewSimilares.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                librosList = cargarLibrosDesdeJSONSeguro()
                tiposList = cargarTiposDesdeJSONSeguro()
                generosList = cargarGenerosDesdeJSONSeguro()
                comprasList = cargarComprasDesdeJSONSeguro()
            }

            val libro = librosList.find { it.id_libro == libroId }
            if (libro == null) {
                Toast.makeText(this@Libros, "Libro no encontrado", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            libroActual = libro
            setupUI(libroActual)
            configurarBotonAccion()
        }
    }

    // ------------------ CARGA JSON ------------------

    private fun cargarLibrosDesdeJSONSeguro(): List<Libro> = try {
        val json = assets.open("data/libros.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<Libro>>>() {}.type
        Gson().fromJson<Map<String, List<Libro>>>(json, type)["libros"] ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    private fun cargarTiposDesdeJSONSeguro(): List<TipoLibro> = try {
        val json = assets.open("data/tipos_libro.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<TipoLibro>>>() {}.type
        Gson().fromJson<Map<String, List<TipoLibro>>>(json, type)["tipos_libro"] ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    private fun cargarGenerosDesdeJSONSeguro(): List<Genero> = try {
        val json = assets.open("data/generos.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<Genero>>>() {}.type
        Gson().fromJson<Map<String, List<Genero>>>(json, type)["generos"] ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    private fun cargarComprasDesdeJSONSeguro(): List<CompraLibro> = try {
        val json = assets.open("data/compras_libro.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<CompraLibro>>>() {}.type
        Gson().fromJson<Map<String, List<CompraLibro>>>(json, type)["compras_libro"] ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    // ------------------ UI ------------------

    private fun setupUI(libro: Libro) {
        cargarImagenPortada(libro, findViewById(R.id.portadaLibro))

        val tipo = tiposList.find { it.id_tipo == libro.id_tipo }
        findViewById<TextView>(R.id.tipoLibro).text =
            tipo?.nombre_tipo?.uppercase() ?: "LIBRO"

        findViewById<TextView>(R.id.nombreOriginal).text = libro.titulo
        findViewById<TextView>(R.id.nombreTraducido).visibility = View.GONE

        findViewById<RatingBar>(R.id.ratingBar).rating =
            libro.puntuacion_promedio.toFloat()
        findViewById<TextView>(R.id.textoRating).text =
            "${libro.puntuacion_promedio}/5"

        val contenedor = findViewById<LinearLayout>(R.id.contenedorGeneros)
        contenedor.removeAllViews()

        generosList.find { it.id_genero == libro.id_genero }?.let {
            val chip = TextView(this)
            chip.text = it.nombre_genero
            contenedor.addView(chip)
        }

        findViewById<TextView>(R.id.descripcionLibro).text = libro.descripcion

// Botón foro
        findViewById<Button>(R.id.botonForo).setOnClickListener {
            val intent = Intent(this, Foros::class.java)
            intent.putExtra("LIBRO_ID", libro.id_libro)
            intent.putExtra("LIBRO_TITULO", libro.titulo)  // Pasar el título del libro
            startActivity(intent)
        }

        configurarLibrosSimilares(libro)
    }

    // ------------------ LIBROS SIMILARES ------------------

    private fun configurarLibrosSimilares(libro: Libro) {
        val similares = librosList.filter {
            it.id_genero == libro.id_genero && it.id_libro != libro.id_libro
        }

        recyclerViewSimilares.adapter = LibrosSimilaresAdapter(similares)
    }

    inner class LibrosSimilaresAdapter(private val libros: List<Libro>) :
        RecyclerView.Adapter<LibrosSimilaresAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val portada: ImageView = view.findViewById(R.id.libroImageView)
            val titulo: TextView = view.findViewById(R.id.libroTitleTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_libro, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val libro = libros[position]
            holder.titulo.text = libro.titulo
            cargarImagenPortada(libro, holder.portada)

            holder.itemView.setOnClickListener {
                startActivity(
                    Intent(this@Libros, Libros::class.java)
                        .putExtra("LIBRO_ID", libro.id_libro)
                )
            }
        }

        override fun getItemCount(): Int = libros.size
    }

    // ------------------ PORTADA ------------------

    private fun cargarImagenPortada(libro: Libro, imageView: ImageView) {
        imageView.load("file:///android_asset/portadas/${libro.portada}") {
            placeholder(R.drawable.portada_default)
            error(R.drawable.portada_default)
        }
    }

    // ------------------ COMPRA ------------------

    private fun configurarBotonAccion() {
        val boton = findViewById<Button>(R.id.botonAccionLibro)
        val userId = sessionManager.getUser()?.id_usuario

        val comprado = comprasList.any {
            it.id_usuario == userId && it.id_libro == libroId
        } || sessionManager.getTemporalPurchases().any {
            it.id_usuario == userId && it.id_libro == libroId
        }

        when {
            !sessionManager.isLogged() -> {
                boton.text = "🛒 Comprar"
                boton.setOnClickListener {
                    startActivity(Intent(this, Login::class.java))
                }
            }
            comprado -> {
                boton.text = "▶️ Ir a leer"
                boton.setOnClickListener {
                    startActivity(
                        Intent(this, PdfActivity::class.java)
                            .putExtra("LIBRO_ID", libroId)
                            .putExtra("PDF_NAME", libroActual.url_archivo)
                    )
                }
            }
            else -> {
                boton.text = "🛒 Comprar"
                boton.setOnClickListener { mostrarDialogoCompra() }
            }
        }
    }

    private fun mostrarDialogoCompra() {
        val puntos = sessionManager.getUserPoints()
        val precio = libroActual.precio_puntos

        AlertDialog.Builder(this)
            .setTitle("Confirmar compra")
            .setMessage("¿Gastar $precio puntos?")
            .setPositiveButton("Comprar") { _, _ ->
                if (puntos >= precio) realizarCompraSimulada(puntos, precio)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun realizarCompraSimulada(puntos: Int, precio: Int) {
        sessionManager.addTemporalPurchase(
            CompraLibro(
                generarNuevoIdCompra(),
                sessionManager.getUser()?.id_usuario ?: -1,
                libroId,
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            )
        )
        sessionManager.updateUserPoints(puntos - precio)
        configurarBotonAccion()
    }

    private fun generarNuevoIdCompra(): Int {
        val todas = comprasList + sessionManager.getTemporalPurchases()
        return (todas.maxByOrNull { it.id_compra }?.id_compra ?: 0) + 1
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            comprasList = withContext(Dispatchers.IO) {
                cargarComprasDesdeJSONSeguro()
            }
            configurarBotonAccion()
        }
    }
}
