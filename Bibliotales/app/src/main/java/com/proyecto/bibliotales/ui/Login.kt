package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.repository.UserRepository

class Login : BaseActivity() {

    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.login)

        userRepository = UserRepository(this)

        if (sessionManager.isLogged()) {
            redirigirAPerfil()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val btnRegistro = findViewById<View>(R.id.btnRegistro)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validarCampos(email, password)) {
                intentarLogin(email, password)
            }
        }

        btnRegistro.setOnClickListener {
            startActivity(Intent(this, Registrarse::class.java))
        }
    }

    private fun validarCampos(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "El email es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "La contraseña es obligatoria", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun intentarLogin(email: String, password: String) {

        // 1️⃣ Buscar en JSON
        val usuarioJSON = userRepository.buscarUsuario(email, password)
        if (usuarioJSON != null) {
            sessionManager.saveUser(usuarioJSON)
            redirigirAPerfil()
            return
        }

        // 2️⃣ Buscar en SharedPreferences (usuario registrado)
        val usuarioSP = sessionManager.getUser()
        if (
            usuarioSP != null &&
            usuarioSP.correo == email &&
            usuarioSP.contraseña == password
        ) {
            redirigirAPerfil()
            return
        }

        Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
    }

    private fun redirigirAPerfil() {
        startActivity(Intent(this, PerfilUsuario::class.java))
        finish()
    }
}
