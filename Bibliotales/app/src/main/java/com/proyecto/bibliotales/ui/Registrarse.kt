package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Registrarse : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.registrarse)

        setupUI()
    }

    private fun setupUI() {
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val birthDateInput = findViewById<EditText>(R.id.birthDateInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)

        registerButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            val birthDate = birthDateInput.text.toString().trim()

            if (validarRegistro(email, password, confirmPassword, birthDate)) {

                val nombreUsuario = email
                    .substringBefore("@")
                    .replace(".", " ")
                    .replaceFirstChar { it.uppercase() }

                val usuario = Usuario(
                    id_usuario = 2,               // ⚠ temporal
                    nombre_usuario = nombreUsuario,
                    correo = email,
                    contraseña = password,
                    tipo_usuario = "1",
                    puntos = 100,
                    fecha_registro = obtenerFechaActual(),
                    fecha_nacimiento = birthDate
                )

                sessionManager.saveUser(usuario)

                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, PerfilUsuario::class.java))
                finish()
            }
        }

        backToLoginButton.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun validarRegistro(
        email: String,
        password: String,
        confirmPassword: String,
        birthDate: String
    ): Boolean {

        if (email.isEmpty()) {
            Toast.makeText(this, "El email es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email no válido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        val regex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        if (!regex.matches(birthDate)) {
            Toast.makeText(this, "Formato de fecha incorrecto (YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
