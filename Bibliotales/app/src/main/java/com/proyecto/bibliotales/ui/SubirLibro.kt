package com.proyecto.bibliotales.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.CompraLibro
import com.proyecto.bibliotales.data.models.Libro
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SubirLibro : AppCompatActivity() {

    private lateinit var etTitulo: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnSeleccionarPDF: Button
    private lateinit var tvNombrePDF: TextView
    private lateinit var btnCancelar: Button
    private lateinit var btnSubir: Button
    private lateinit var btnSeleccionarPortada: Button
    private lateinit var ivPortada: ImageView

    private var pdfUri: Uri? = null
    private var portadaUri: Uri? = null
    private val gson = Gson()

    // Contract para seleccionar PDF
    private val seleccionarPDFLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                pdfUri = uri
                val nombreArchivo = obtenerNombreArchivo(uri)
                tvNombrePDF.text = nombreArchivo
                habilitarBotonSubir()
            }
        }
    }

    // Contract para seleccionar imagen
    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                portadaUri = uri
                ivPortada.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subir_libro)

        // Inicializar vistas
        initViews()

        // Configurar listeners
        configurarListeners()
    }

    private fun initViews() {
        etTitulo = findViewById(R.id.etTitulo)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        btnSeleccionarPDF = findViewById(R.id.btnSeleccionarPDF)
        tvNombrePDF = findViewById(R.id.tvNombrePDF)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnSubir = findViewById(R.id.btnSubir)
        btnSeleccionarPortada = findViewById(R.id.btnSeleccionarPortada)
        ivPortada = findViewById(R.id.ivPortada)
    }

    private fun configurarListeners() {
        // Botón para seleccionar portada
        btnSeleccionarPortada.setOnClickListener {
            abrirSelectorImagen()
        }

        // Botón para seleccionar PDF
        btnSeleccionarPDF.setOnClickListener {
            abrirSelectorPDF()
        }

        // Botón cancelar
        btnCancelar.setOnClickListener {
            limpiarCampos()
            finish()
        }

        // Botón subir
        btnSubir.setOnClickListener {
            subirLibro()
        }

        // Habilitar botón subir cuando haya título y PDF
        etTitulo.setOnKeyListener { _, _, _ ->
            habilitarBotonSubir()
            false
        }
    }

    private fun abrirSelectorImagen() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        seleccionarImagenLauncher.launch(intent)
    }

    private fun abrirSelectorPDF() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        seleccionarPDFLauncher.launch(intent)
    }

    private fun obtenerNombreArchivo(uri: Uri): String {
        var nombreArchivo = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nombreIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nombreIndex != -1) {
                    nombreArchivo = cursor.getString(nombreIndex)
                }
            }
        }
        return nombreArchivo.ifEmpty { "archivo.pdf" }
    }

    private fun habilitarBotonSubir() {
        val tituloCompleto = etTitulo.text.toString().trim().isNotEmpty()
        val pdfSeleccionado = pdfUri != null
        btnSubir.isEnabled = tituloCompleto && pdfSeleccionado
    }

    private fun limpiarCampos() {
        etTitulo.text.clear()
        etDescripcion.text.clear()
        etPrecio.text.clear()
        tvNombrePDF.text = "Ningún archivo seleccionado"
        ivPortada.setImageResource(R.drawable.portada_default)
        pdfUri = null
        portadaUri = null
        btnSubir.isEnabled = false
    }

    private fun subirLibro() {
        // Obtener datos del formulario
        val titulo = etTitulo.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precioTexto = etPrecio.text.toString().trim()

        // Validaciones básicas
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Debes ingresar un título", Toast.LENGTH_SHORT).show()
            return
        }

        if (pdfUri == null) {
            Toast.makeText(this, "Debes seleccionar un archivo PDF", Toast.LENGTH_SHORT).show()
            return
        }

        // Procesar precio (0.0 si está vacío)
        val precio = try {
            if (precioTexto.isEmpty()) 0.0 else precioTexto.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }

        // Obtener ID del usuario actual (simulado por ahora)
        val idUsuario = obtenerIdUsuarioActual()

        // Generar nuevo ID para el libro
        val nuevoIdLibro = generarNuevoIdLibro()

        // Obtener fecha actual
        val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // Copiar archivo PDF a assets interno (simulación)
        val nombreArchivoPDF = "libro_${nuevoIdLibro}.pdf"
        val nombrePortada = if (portadaUri != null) "portada_${nuevoIdLibro}.jpg" else ""

        // Crear objeto Libro
        val nuevoLibro = Libro(
            id_libro = nuevoIdLibro,
            titulo = titulo,
            descripcion = descripcion,
            fecha_publicacion = fechaActual,
            url_archivo = nombreArchivoPDF, // Nombre del archivo en assets
            id_usuario = idUsuario,
            id_tipo = 1, // 1 = eBook
            costo_dinero = precio,
            autor = "", // Por ahora vacío
            id_genero = 1, // 1 = Sin género
            puntuacion_promedio = 0.0,
            precio_puntos = 0,
            disponible = true,
            portada = nombrePortada // Nombre de la portada si se seleccionó
        )

        // Crear objeto CompraLibro para el usuario que sube el libro
        val nuevaCompra = CompraLibro(
            id_compra = generarNuevoIdCompra(),
            id_usuario = idUsuario,
            id_libro = nuevoIdLibro,
            fecha_libro_compra = fechaActual
        )

        // Guardar en SharedPreferences (temporal)
        guardarLibroTemporal(nuevoLibro, nuevaCompra)

        // Mostrar mensaje de éxito
        Toast.makeText(this, "Libro subido correctamente", Toast.LENGTH_SHORT).show()

        // Redirigir a Biblioteca
        val intent = Intent(this, Biblioteca::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun obtenerIdUsuarioActual(): Int {
        // Obtener ID del usuario desde SharedPreferences
        val prefs = getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("id_usuario", 1) // Default 1 si no existe
    }

    private fun generarNuevoIdLibro(): Int {
        // Cargar libros existentes y generar nuevo ID
        return try {
            val jsonString = assets.open("data/libros.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<Libro>>>() {}.type
            val data: Map<String, List<Libro>> = gson.fromJson(jsonString, type)
            val libros = data["libros"] ?: emptyList()
            (libros.maxByOrNull { it.id_libro }?.id_libro ?: 0) + 1
        } catch (e: Exception) {
            // Si hay error, usar timestamp como ID
            System.currentTimeMillis().toInt()
        }
    }

    private fun generarNuevoIdCompra(): Int {
        // Similar a generarNuevoIdLibro pero para compras
        return try {
            val jsonString = assets.open("data/compras_libro.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Map<String, List<CompraLibro>>>() {}.type
            val data: Map<String, List<CompraLibro>> = gson.fromJson(jsonString, type)
            val compras = data["compras_libro"] ?: emptyList()
            (compras.maxByOrNull { it.id_compra }?.id_compra ?: 0) + 1
        } catch (e: Exception) {
            System.currentTimeMillis().toInt()
        }
    }

    private fun guardarLibroTemporal(libro: Libro, compra: CompraLibro) {
        // Guardar libro temporal en SharedPreferences
        val prefs = getSharedPreferences("libros_temporales", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Guardar libro
        val librosJson = prefs.getString("libros", "[]")
        val typeLibros = object : TypeToken<List<Libro>>() {}.type
        val libros = gson.fromJson<List<Libro>>(librosJson, typeLibros)?.toMutableList() ?: mutableListOf()
        libros.add(libro)

        // Guardar compra
        val comprasJson = prefs.getString("compras", "[]")
        val typeCompras = object : TypeToken<List<CompraLibro>>() {}.type
        val compras = gson.fromJson<List<CompraLibro>>(comprasJson, typeCompras)?.toMutableList() ?: mutableListOf()
        compras.add(compra)

        // Guardar en SharedPreferences
        editor.putString("libros", gson.toJson(libros))
        editor.putString("compras", gson.toJson(compras))
        editor.apply()

        // También guardar en la sesión actual para que aparezca inmediatamente
        val sessionPrefs = getSharedPreferences("usuario_prefs", Context.MODE_PRIVATE)
        val sessionEditor = sessionPrefs.edit()
        sessionEditor.putBoolean("tiene_libro_subido_${libro.id_libro}", true)
        sessionEditor.apply()
    }

    // Función para copiar archivo (no implementada completamente ya que requiere permisos)
    private fun copiarArchivoALocal(uri: Uri, nombreDestino: String): Boolean {
        // Esta función copiaría el archivo seleccionado a la carpeta interna de la app
        // Por ahora solo es un placeholder
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val file = File(filesDir, nombreDestino)
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}