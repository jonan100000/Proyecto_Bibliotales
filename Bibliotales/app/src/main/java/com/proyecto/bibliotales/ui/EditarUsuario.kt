package com.proyecto.bibliotales.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.Usuario
import com.proyecto.bibliotales.data.session.SessionManager

class EditarUsuario : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val COSTO_DESBLOQUEO = 100
    }

    private lateinit var sessionManager: SessionManager
    private lateinit var ivProfileImage: ImageView
    private lateinit var profileImageContainer: View
    private lateinit var mainContainer: View
    private lateinit var framesContainer: LinearLayout
    private lateinit var backgroundsContainer: LinearLayout
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var tvUserPoints: TextView

    private var selectedFrameId: Int = -1
    private var selectedBackgroundId: Int = -1
    private var selectedProfileImageResource: String = "goku_pelado"

    // Datos de marcos y backgrounds con costo
    private val marcosDisponibles = listOf(
        ItemDecoracion(-1, R.drawable.borde_perfil, "Marco por defecto", "marco", 0, true),
        ItemDecoracion(1, R.color.marco_azul, "Marco Azul", "marco", COSTO_DESBLOQUEO, false),
        ItemDecoracion(2, R.color.marco_verde, "Marco Verde", "marco", COSTO_DESBLOQUEO, false),
        ItemDecoracion(3, R.color.marco_rojo, "Marco Rojo", "marco", COSTO_DESBLOQUEO, false),
        ItemDecoracion(4, R.color.marco_naranja, "Marco Naranja", "marco", COSTO_DESBLOQUEO, false)
    )

    private val backgroundsDisponibles = listOf(
        ItemDecoracion(-1, R.color.background_dark, "Background por defecto", "background", 0, true),
        ItemDecoracion(1, R.color.background_gris_oscuro, "Background Gris Oscuro", "background", COSTO_DESBLOQUEO, false),
        ItemDecoracion(2, R.color.background_gris_claro, "Background Gris Claro", "background", COSTO_DESBLOQUEO, false),
        ItemDecoracion(3, R.color.background_naranja, "Background Naranja", "background", COSTO_DESBLOQUEO, false),
        ItemDecoracion(4, R.color.background_azul, "Background Azul", "background", COSTO_DESBLOQUEO, false),
        ItemDecoracion(5, R.color.background_verde, "Background Verde", "background", COSTO_DESBLOQUEO, false),
        ItemDecoracion(6, R.color.background_morado, "Background Morado", "background", COSTO_DESBLOQUEO, false)
    )

    data class ItemDecoracion(
        val id: Int,
        val recurso: Int,
        val nombre: String,
        val tipo: String,
        val costo: Int,
        var desbloqueado: Boolean
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        sessionManager = SessionManager(this)

        // Inicializar vistas
        ivProfileImage = findViewById(R.id.ivProfileImage)
        profileImageContainer = findViewById(R.id.profileImageContainer)
        mainContainer = findViewById(R.id.mainContainer)
        framesContainer = findViewById(R.id.framesContainer)
        backgroundsContainer = findViewById(R.id.backgroundsContainer)
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        tvUserPoints = findViewById(R.id.tvUserPoints)

        // Cargar datos actuales
        cargarDatosActuales()

        // Cargar items disponibles
        cargarItemsDisponibles()

        // Configurar listeners
        findViewById<View>(R.id.fabChangePhoto).setOnClickListener {
            mostrarDialogoSeleccionarFoto()
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatosActuales() {
        val usuario = sessionManager.getUser()
        usuario?.let {
            etUsername.setText(it.nombre_usuario)
            actualizarPuntosEnUI(it.puntos)

            // Cargar items equipados actualmente
            selectedFrameId = sessionManager.getEquippedItem(it.id_usuario, "marco")
            selectedBackgroundId = sessionManager.getEquippedItem(it.id_usuario, "background")

            // Verificar qué items están desbloqueados
            verificarItemsDesbloqueados(it.id_usuario)

            // Aplicar los items equipados
            aplicarMarco(selectedFrameId)
            aplicarBackground(selectedBackgroundId)

            // Cargar imagen de perfil actual
            val profileImage = sessionManager.getProfileImage(it.id_usuario)
            profileImage?.let { imageName ->
                if (imageName.startsWith("content://")) {
                    // Es una imagen de la galería
                    try {
                        ivProfileImage.setImageURI(Uri.parse(imageName))
                    } catch (e: Exception) {
                        // Si falla, cargar imagen por defecto
                        ivProfileImage.setImageResource(R.drawable.goku_pelado)
                        selectedProfileImageResource = "goku_pelado"
                    }
                } else {
                    // Es una imagen del drawable
                    val resourceId = resources.getIdentifier(imageName, "drawable", packageName)
                    if (resourceId != 0) {
                        ivProfileImage.setImageResource(resourceId)
                        selectedProfileImageResource = imageName
                    }
                }
            }
        }
    }

    private fun actualizarPuntosEnUI(puntos: Int) {
        tvUserPoints.text = "Puntos disponibles: $puntos"
    }

    private fun verificarItemsDesbloqueados(usuarioId: Int) {
        // Verificar marcos desbloqueados
        marcosDisponibles.forEach { item ->
            if (item.id != -1) { // El por defecto siempre está desbloqueado
                item.desbloqueado = sessionManager.isItemUnlocked(usuarioId, item.id, item.tipo)
            }
        }

        // Verificar backgrounds desbloqueados
        backgroundsDisponibles.forEach { item ->
            if (item.id != -1) { // El por defecto siempre está desbloqueado
                item.desbloqueado = sessionManager.isItemUnlocked(usuarioId, item.id, item.tipo)
            }
        }
    }

    private fun cargarItemsDisponibles() {
        // Limpiar contenedores
        framesContainer.removeAllViews()
        backgroundsContainer.removeAllViews()

        // Cargar marcos disponibles
        marcosDisponibles.forEach { item ->
            val frameView = crearItemView(item)
            framesContainer.addView(frameView)
        }

        // Cargar backgrounds disponibles
        backgroundsDisponibles.forEach { item ->
            val backgroundView = crearItemView(item)
            backgroundsContainer.addView(backgroundView)
        }
    }

    private fun crearItemView(item: ItemDecoracion): View {
        val view = layoutInflater.inflate(R.layout.item_seleccionable_con_candado, null)
        val imageView = view.findViewById<ImageView>(R.id.ivItem)
        val checkView = view.findViewById<View>(R.id.ivCheck)
        val lockView = view.findViewById<ImageView>(R.id.ivLock)
        val tvCosto = view.findViewById<TextView>(R.id.tvCosto)

        // Establecer el color o drawable como fondo
        if (item.recurso == R.drawable.borde_perfil) {
            // Es un drawable de marco
            imageView.setBackgroundResource(item.recurso)
        } else if (item.tipo == "marco") {
            // Es un color para marco (usar como fondo sólido)
            val color = ContextCompat.getColor(this, item.recurso)
            imageView.setBackgroundColor(color)
            // Agregar borde para simular marco
            imageView.setBackgroundResource(R.drawable.item_color_border)
            imageView.setColorFilter(color)
        } else {
            // Es un color para background
            val color = ContextCompat.getColor(this, item.recurso)
            imageView.setBackgroundColor(color)
        }

        // Configurar estado de bloqueo
        if (item.desbloqueado) {
            // Item desbloqueado
            lockView.visibility = View.GONE
            tvCosto.visibility = View.GONE
            view.alpha = 1.0f

            // Marcar si está seleccionado
            val isSelected = when (item.tipo) {
                "marco" -> item.id == selectedFrameId
                "background" -> item.id == selectedBackgroundId
                else -> false
            }

            checkView.visibility = if (isSelected) View.VISIBLE else View.GONE
        } else {
            // Item bloqueado
            lockView.visibility = View.VISIBLE
            tvCosto.visibility = View.VISIBLE
            tvCosto.text = "${item.costo} pts"
            view.alpha = 0.7f
            checkView.visibility = View.GONE
        }

        // Guardar el item como tag
        view.tag = item

        view.setOnClickListener {
            if (item.desbloqueado) {
                // Item desbloqueado: seleccionar
                when (item.tipo) {
                    "marco" -> {
                        selectedFrameId = item.id
                        aplicarMarco(item.id)
                        actualizarSeleccionMarcos()
                    }
                    "background" -> {
                        selectedBackgroundId = item.id
                        aplicarBackground(item.id)
                        actualizarSeleccionBackgrounds()
                    }
                }
            } else {
                // Item bloqueado: mostrar diálogo de compra
                mostrarDialogoComprarItem(item)
            }
        }

        val params = LinearLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.item_size),
            resources.getDimensionPixelSize(R.dimen.item_size)
        ).apply {
            marginEnd = resources.getDimensionPixelSize(R.dimen.item_margin)
        }

        view.layoutParams = params
        return view
    }

    private fun mostrarDialogoComprarItem(item: ItemDecoracion) {
        val usuario = sessionManager.getUser() ?: return

        AlertDialog.Builder(this)
            .setTitle("Desbloquear ${item.nombre}")
            .setMessage("¿Deseas desbloquear este ${item.tipo} por ${item.costo} puntos?\n\nTus puntos actuales: ${usuario.puntos}")
            .setPositiveButton("Comprar") { dialog, _ ->
                if (usuario.puntos >= item.costo) {
                    // Desbloquear item
                    desbloquearItem(item, usuario)
                } else {
                    // Puntos insuficientes
                    AlertDialog.Builder(this)
                        .setTitle("Puntos insuficientes")
                        .setMessage("No tienes suficientes puntos para desbloquear este item.\n\nNecesitas: ${item.costo} puntos\nTienes: ${usuario.puntos} puntos")
                        .setPositiveButton("Aceptar", null)
                        .show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun desbloquearItem(item: ItemDecoracion, usuario: Usuario) {
        // Restar puntos
        val nuevosPuntos = usuario.puntos - item.costo

        // Actualizar usuario
        val usuarioActualizado = Usuario(
            id_usuario = usuario.id_usuario,
            nombre_usuario = usuario.nombre_usuario,
            correo = usuario.correo,
            contraseña = usuario.contraseña,
            tipo_usuario = usuario.tipo_usuario,
            puntos = nuevosPuntos,
            fecha_registro = usuario.fecha_registro,
            fecha_nacimiento = usuario.fecha_nacimiento
        )

        // Guardar cambios
        sessionManager.saveUser(usuarioActualizado)
        sessionManager.saveUnlockedItem(usuario.id_usuario, item.id, item.tipo)

        // Actualizar item
        item.desbloqueado = true

        // Actualizar vista de puntos
        actualizarPuntosEnUI(nuevosPuntos)

        // Recargar vista del item
        recargarItemView(item)

        // Mostrar mensaje de éxito
        AlertDialog.Builder(this)
            .setTitle("¡Item desbloqueado!")
            .setMessage("Has desbloqueado ${item.nombre} por ${item.costo} puntos.\n\nPuntos restantes: $nuevosPuntos")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun recargarItemView(item: ItemDecoracion) {
        val container = when (item.tipo) {
            "marco" -> framesContainer
            "background" -> backgroundsContainer
            else -> return
        }

        for (i in 0 until container.childCount) {
            val view = container.getChildAt(i)
            val viewItem = view.tag as? ItemDecoracion
            if (viewItem?.id == item.id && viewItem.tipo == item.tipo) {
                container.removeView(view)
                val newView = crearItemView(item)
                container.addView(newView, i)
                break
            }
        }
    }

    private fun aplicarMarco(marcoId: Int) {
        val marco = marcosDisponibles.find { it.id == marcoId && it.desbloqueado }
        marco?.let {
            if (it.recurso == R.drawable.borde_perfil) {
                // Marco por defecto (drawable)
                profileImageContainer.setBackgroundResource(it.recurso)
            } else {
                // Marco de color
                val color = ContextCompat.getColor(this, it.recurso)
                profileImageContainer.setBackgroundColor(color)
                // Agregar padding para que se vea como borde
                profileImageContainer.setPadding(4, 4, 4, 4)
            }

            // Guardar en preferencias para aplicar en PerfilUsuario
            sessionManager.getUser()?.let { usuario ->
                sessionManager.saveEquippedItem(usuario.id_usuario, marcoId, "marco")
            }
        }
    }

    private fun aplicarBackground(backgroundId: Int) {
        val background = backgroundsDisponibles.find { it.id == backgroundId && it.desbloqueado }
        background?.let {
            val color = ContextCompat.getColor(this, it.recurso)
            mainContainer.setBackgroundColor(color)

            // Guardar en preferencias para aplicar en PerfilUsuario
            sessionManager.getUser()?.let { usuario ->
                sessionManager.saveEquippedItem(usuario.id_usuario, backgroundId, "background")
            }
        }
    }

    private fun actualizarSeleccionMarcos() {
        for (i in 0 until framesContainer.childCount) {
            val view = framesContainer.getChildAt(i)
            val checkView = view.findViewById<View>(R.id.ivCheck)
            val item = view.tag as? ItemDecoracion

            checkView.visibility = if (item?.id == selectedFrameId && item.desbloqueado) View.VISIBLE else View.GONE
        }
    }

    private fun actualizarSeleccionBackgrounds() {
        for (i in 0 until backgroundsContainer.childCount) {
            val view = backgroundsContainer.getChildAt(i)
            val checkView = view.findViewById<View>(R.id.ivCheck)
            val item = view.tag as? ItemDecoracion

            checkView.visibility = if (item?.id == selectedBackgroundId && item.desbloqueado) View.VISIBLE else View.GONE
        }
    }

    private fun mostrarDialogoSeleccionarFoto() {
        val opciones = arrayOf("Elegir de la galería", "Goku", "Default", "Cancelar")

        AlertDialog.Builder(this)
            .setTitle("Seleccionar foto de perfil")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> { // Elegir de la galería
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*"
                        startActivityForResult(intent, PICK_IMAGE_REQUEST)
                    }
                    1 -> { // Goku
                        ivProfileImage.setImageResource(R.drawable.goku_pelado)
                        selectedProfileImageResource = "goku_pelado"
                    }
                    2 -> { // Default
                        ivProfileImage.setImageResource(R.drawable.portada_default)
                        selectedProfileImageResource = "portada_default"
                    }
                    // 3 es Cancelar, no hace nada
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                ivProfileImage.setImageURI(it)
                // Guardar la URI como string para poder cargarla después
                selectedProfileImageResource = it.toString()
            }
        }
    }

    private fun guardarCambios() {
        val nuevoUsername = etUsername.text.toString().trim()
        val nuevaPassword = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (nuevoUsername.isEmpty()) {
            etUsername.error = "El nombre de usuario no puede estar vacío"
            return
        }

        if (nuevaPassword.isNotEmpty() && nuevaPassword != confirmPassword) {
            etConfirmPassword.error = "Las contraseñas no coinciden"
            return
        }

        // Obtener usuario actual (con los puntos actualizados)
        val usuario = sessionManager.getUser()
        usuario?.let { usuarioActual ->
            // Crear un NUEVO objeto Usuario con los cambios
            val usuarioActualizado = Usuario(
                id_usuario = usuarioActual.id_usuario,
                nombre_usuario = nuevoUsername,
                correo = usuarioActual.correo,
                contraseña = if (nuevaPassword.isNotEmpty()) nuevaPassword else usuarioActual.contraseña,
                tipo_usuario = usuarioActual.tipo_usuario,
                puntos = usuarioActual.puntos, // Usar puntos actuales (pueden haber cambiado por compras)
                fecha_registro = usuarioActual.fecha_registro,
                fecha_nacimiento = usuarioActual.fecha_nacimiento
            )

            // Guardar cambios en SessionManager
            sessionManager.saveUser(usuarioActualizado)

            // Guardar items equipados (ya se guardaron al aplicar)
            // Guardar imagen de perfil seleccionada
            sessionManager.saveProfileImage(usuarioActual.id_usuario, selectedProfileImageResource)

            // Regresar al perfil
            finish()
        }
    }
}