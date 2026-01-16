package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.session.SessionManager

open class BaseActivity : AppCompatActivity() {

    protected lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        sessionManager = SessionManager(this)
        setupTopBar()
    }

    protected fun setContentLayout(layoutResId: Int) {
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        LayoutInflater.from(this).inflate(layoutResId, contentFrame, true)
    }

    private fun setupTopBar() {
        findViewById<ImageView>(R.id.navMenuButton).setOnClickListener {
            openNavigationMenu()
        }

        findViewById<ImageView>(R.id.userMenuButton).setOnClickListener {
            openUserMenu()
        }

        findViewById<ImageView>(R.id.appLogo).setOnClickListener {
            irAMainActivity()
        }
    }

    private fun irAMainActivity() {
        if (this !is MainActivity) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }
    }

    protected open fun openNavigationMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.navMenuButton))
        popupMenu.menuInflater.inflate(R.menu.menu_navegacion, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_filtro_libros -> abrirFiltroLibros()
                R.id.menu_ajustes -> abrirAjustes()
                else -> false
            }
        }

        popupMenu.show()
    }

    protected open fun openUserMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.userMenuButton))

        if (sessionManager.isLogged()) {
            popupMenu.menuInflater.inflate(R.menu.menu_usuario_logueado, popupMenu.menu)
        } else {
            popupMenu.menuInflater.inflate(R.menu.menu_usuario_no_logueado, popupMenu.menu)
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_iniciar_sesion -> abrirLogin()
                R.id.menu_registrarse -> abrirRegistro()
                R.id.menu_perfil_usuario -> abrirPerfilUsuario()
                R.id.menu_biblioteca -> abrirBiblioteca()
                R.id.menu_cerrar_sesion -> cerrarSesion()
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun abrirBiblioteca() = abrirActivity(Biblioteca::class.java)
    private fun abrirFiltroLibros() = abrirActivity(FiltroLibros::class.java)
    private fun abrirAjustes() = abrirActivity(Ajustes::class.java)
    private fun abrirLogin(): Boolean = abrirActivity(Login::class.java)
    private fun abrirRegistro(): Boolean = abrirActivity(Registrarse::class.java)
    private fun abrirPerfilUsuario(): Boolean = abrirActivity(PerfilUsuario::class.java)

    private fun <T> abrirActivity(activity: Class<T>): Boolean {
        startActivity(Intent(this, activity))
        return true
    }

    private fun cerrarSesion(): Boolean {
        sessionManager.logout()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
        return true
    }
}
