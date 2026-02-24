package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.proyecto.bibliotales.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pojospi.Usuario

class Registrarse : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.registrarse)

        setupUI()
    }

    private fun setupUI() {
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordInput)
        val birthDateInput = findViewById<EditText>(R.id.birthDateInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val backToLoginButton = findViewById<Button>(R.id.backToLoginButton)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()
            val birthDate = birthDateInput.text.toString().trim()

            // Puedes reusar tu validarRegistro, pero ahora también valida username:
            if (username.isEmpty()) {
                Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (validarRegistro(email, password, confirmPassword, birthDate)) {

                // Parse a Date para el POJO (tu servidor usa java.sql.Date(usuario.getFechaNacimiento().getTime()))
                val fechaNacimiento = try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthDate)
                } catch (e: Exception) {
                    null
                }

                if (fechaNacimiento == null) {
                    Toast.makeText(this, "Fecha inválida (YYYY-MM-DD)", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Construir pojospi.Usuario COMPLETO (obligatorio para tu INSERT)
                val usuario = Usuario().apply {
                    setNombre_usuario(username)
                    setCorreo(email)
                    setContrasena(password)     // OJO: en tu BD es CHAR(1) ahora mismo
                    setTipoUsuario("R")         // CHECK: 'R' o 'A'
                    setPuntos(0)                // CHECK: puntos>=0
                    setFechaRegistro(Date())    // obligatorio
                    setFechaNacimiento(fechaNacimiento)
                }

                val peticion = Peticion(Peticion.TipoOperacion.CREATE_USUARIO, usuario)

                // Enviar al servidor (IO)
                registerButton.isEnabled = false

                CoroutineScope(Dispatchers.IO).launch {
                    val respuesta = try {
                        val cliente = ClienteSocket(
                            ClienteConfig.getServerIP(),
                            ClienteConfig.PUERTO_SERVIDOR
                        )
                        cliente.enviarPeticion(peticion)
                    } catch (e: Exception) {
                        null
                    }

                    withContext(Dispatchers.Main) {
                        registerButton.isEnabled = true

                        if (respuesta?.isExito == true) {
                            Toast.makeText(this@Registrarse, respuesta.mensaje ?: "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@Registrarse, Login::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@Registrarse,
                                "Error: ${respuesta?.mensaje ?: "No se pudo registrar (sin respuesta)"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
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

        if (password.length < 1) {
            Toast.makeText(this, "La contraseña debe tener al menos 1 caracteres", Toast.LENGTH_SHORT).show()
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
