package com.proyecto.bibliotales.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.proyecto.bibliotales.R
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import com.proyecto.bibliotales.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import pojospi.Usuario as UsuarioPI
import com.proyecto.bibliotales.data.models.Usuario as UsuarioApp

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

        if (password.length != 1) {
            Toast.makeText(this, "La contraseña debe ser de 1 caracter (BD: CHAR(1))", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Mapeo de usuario de base de datos
    private fun mapUsuario(pi: UsuarioPI): UsuarioApp {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val fechaReg = pi.fechaRegistro?.let { sdf.format(it) } ?: ""
        val fechaNac = pi.fechaNacimiento?.let { sdf.format(it) } ?: ""

        return UsuarioApp(
            id_usuario = pi.id_usuario ?: -1,
            nombre_usuario = pi.nombre_usuario ?: "",
            correo = pi.correo ?: "",
            contraseña = pi.contrasena ?: "",
            tipo_usuario = pi.tipoUsuario ?: "R",
            puntos = pi.puntos ?: 0,
            fecha_registro = fechaReg,
            fecha_nacimiento = fechaNac
        )
    }

    private fun intentarLogin(email: String, password: String) {

        /*
        * ============ JSON =============
        *
        *  // 1️⃣ Buscar en JSON
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
        *
        * */

        /*
        * =========== UsuarioLogin =============
        */

        val cred = UsuarioPI().apply {
            setCorreo(email)
            setContrasena(password) // debe ser 1 char
        }

        val peticion = Peticion(Peticion.TipoOperacion.LOGIN_USUARIO, cred)

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
                if (respuesta?.isExito == true && respuesta.usuario != null) {
                    val usuarioApp = mapUsuario(respuesta.usuario)
                    sessionManager.saveUser(usuarioApp)
                    redirigirAPerfil()
                } else {
                    Toast.makeText(
                        this@Login,
                        respuesta?.mensaje ?: "Error de conexión / sin respuesta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun redirigirAPerfil() {
        startActivity(Intent(this, PerfilUsuario::class.java))
        finish()
    }
}
