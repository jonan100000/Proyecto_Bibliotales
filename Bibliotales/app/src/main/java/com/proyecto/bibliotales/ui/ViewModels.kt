package com.proyecto.bibliotales.ui.viewmodels

import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pojospi.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



data class RegistroState(
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val mensaje: String? = null
)
class ConexionViewModel : ViewModel() {

    // Estado observable: true = conectado, false = desconectado
    private val _estaConectado = MutableStateFlow(false)
    val estaConectado: StateFlow<Boolean> = _estaConectado

    private val _registroState = MutableStateFlow(RegistroState())
    val registroState: StateFlow<RegistroState> = _registroState



    init {
        iniciarMonitor()
    }

    private fun iniciarMonitor() {
        // El viewModelScope vive mientras la app necesite este ViewModel
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val resultado = try {
                    val cliente = ClienteSocket(
                        ClienteConfig.getServerIP(),
                        ClienteConfig.PUERTO_SERVIDOR
                    )
                    val respuesta = cliente.enviarPeticion(Peticion(Peticion.TipoOperacion.PING))
                    respuesta?.isExito == true
                } catch (e: Exception) {
                    false
                }

                _estaConectado.value = resultado
                delay(5000) // Cada 5 segundos
            }
        }
    }

    fun registrarUsuario(
        nombreUsuario: String,
        correo: String,
        pass: String,
        confirmPass: String,
        fechaNacimientoStr: String
    ) {
        // Validaciones rápidas
        if (nombreUsuario.isBlank() || correo.isBlank() || pass.isBlank() || confirmPass.isBlank() || fechaNacimientoStr.isBlank()) {
            _registroState.value = RegistroState(exito = false, mensaje = "Por favor, complete todos los campos.")
            return
        }
        if (pass != confirmPass) {
            _registroState.value = RegistroState(exito = false, mensaje = "Las contraseñas no coinciden.")
            return
        }

        // Parse fecha nacimiento (YYYY-MM-DD)
        val fechaNacimiento: Date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(fechaNacimientoStr.trim())
                ?: run {
                    _registroState.value = RegistroState(exito = false, mensaje = "Fecha inválida. Usa YYYY-MM-DD.")
                    return
                }
        } catch (e: Exception) {
            _registroState.value = RegistroState(exito = false, mensaje = "Fecha inválida. Usa YYYY-MM-DD.")
            return
        }

        // Construir Usuario completo (tu INSERT lo exige)
        val usuario = Usuario().apply {
            setNombre_usuario(nombreUsuario.trim())
            setCorreo(correo.trim())
            setContrasena(pass)
            setTipoUsuario("R")       // según tu CHECK: 'R' o 'A'
            setPuntos(0)              // CHECK puntos>=0
            setFechaRegistro(Date())  // obligatorio
            setFechaNacimiento(fechaNacimiento)
        }

        _registroState.value = RegistroState(cargando = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cliente = ClienteSocket(
                    ClienteConfig.getServerIP(),
                    ClienteConfig.PUERTO_SERVIDOR
                )

                val peticion = Peticion(Peticion.TipoOperacion.CREATE_USUARIO, usuario)
                val respuesta = cliente.enviarPeticion(peticion)

                _registroState.value =
                    if (respuesta?.isExito == true) {
                        RegistroState(cargando = false, exito = true, mensaje = respuesta.mensaje ?: "Usuario registrado.")
                    } else {
                        RegistroState(cargando = false, exito = false, mensaje = respuesta?.mensaje ?: "No se pudo registrar.")
                    }

            } catch (e: Exception) {
                _registroState.value = RegistroState(
                    cargando = false,
                    exito = false,
                    mensaje = "Error de conexión: ${e.message}"
                )
            }
        }
    }








}