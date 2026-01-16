package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.proyecto.bibliotales.ui.PerfilUsuario
import com.proyecto.bibliotales.R

class Ajustes : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.ajustes)

        setupUI()
    }

    private fun setupUI() {

        // Switch Modo Oscuro
        val switchModoOscuro = findViewById<SwitchCompat>(R.id.switchModoOscuro)
        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Modo oscuro activado", Toast.LENGTH_SHORT).show()
                // Aquí cambiarías el tema de la app
            } else {
                Toast.makeText(this, "Modo claro activado", Toast.LENGTH_SHORT).show()
                // Aquí cambiarías el tema de la app
            }
        }

        // Switch Notificaciones Push
        val switchNotificaciones = findViewById<SwitchCompat>(R.id.switchNotificaciones)
        switchNotificaciones.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                "Notificaciones ${if (isChecked) "activadas" else "desactivadas"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Switch Notificaciones Email
        val switchEmail = findViewById<SwitchCompat>(R.id.switchEmail)
        switchEmail.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(
                this,
                "Notificaciones por email ${if (isChecked) "activadas" else "desactivadas"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Botón Limpiar Caché
        val layoutLimpiarCache = findViewById<LinearLayout>(R.id.layoutLimpiarCache)
        layoutLimpiarCache?.setOnClickListener {
            limpiarCache()
        }

        // Barra de progreso de espacio
        val progressBarEspacio = findViewById<ProgressBar>(R.id.progressBarEspacio)
        val textoEspacio = findViewById<TextView>(R.id.textoEspacio)

        // Simulación inicial
        progressBarEspacio.progress = 65
        textoEspacio.text = "1.3 GB / 2 GB"

        // Versión de la app
        val textoVersion = findViewById<TextView>(R.id.textoVersion)
        textoVersion.text = "1.0.0"

        // Botón Cerrar Sesión - LLAMA AL MÉTODO DE BaseActivity
        val botonCerrarSesion = findViewById<Button>(R.id.botonCerrarSesion)
        botonCerrarSesion.setOnClickListener {
            sessionManager.logout() // ← ESTO ES LO IMPORTANTE: Usa el método heredado
        }

        // Configurar clicks generales
        configurarClicks()
    }

    private fun configurarClicks() {

        // Perfil
        findViewById<LinearLayout>(R.id.layoutPerfil)?.setOnClickListener {
            val intent = Intent(this, PerfilUsuario::class.java)
            startActivity(intent)
        }

        // Cambiar contraseña
        findViewById<LinearLayout>(R.id.layoutCambiarPassword)?.setOnClickListener {
            Toast.makeText(this, "Cambiar contraseña (próximamente)", Toast.LENGTH_SHORT).show()
        }

        // Tamaño de texto
        findViewById<LinearLayout>(R.id.layoutTamanoTexto)?.setOnClickListener {
            mostrarDialogoTamanoTexto()
        }

        // Política de privacidad
        findViewById<LinearLayout>(R.id.layoutPoliticaPrivacidad)?.setOnClickListener {
            Toast.makeText(this, "Política de privacidad (próximamente)", Toast.LENGTH_SHORT).show()
        }

        // Términos de servicio
        findViewById<LinearLayout>(R.id.layoutTerminosServicio)?.setOnClickListener {
            Toast.makeText(this, "Términos de servicio (próximamente)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCache() {
        // Lógica real para limpieza de caché debería ir aquí
        Toast.makeText(this, "Caché limpiada exitosamente", Toast.LENGTH_SHORT).show()

        val textoCache = findViewById<TextView>(R.id.textoCache)
        textoCache.text = "0 MB"
    }

    private fun mostrarDialogoTamanoTexto() {
        Toast.makeText(
            this,
            "Seleccionar tamaño de texto (próximamente)",
            Toast.LENGTH_SHORT
        ).show()
        // Aquí se podría mostrar un diálogo con opciones: Pequeño, Medio, Grande
    }


}