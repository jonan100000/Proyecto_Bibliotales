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
import pojospi.Libro



data class RegistroState(
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val mensaje: String? = null
)

data class UpdateState(
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val mensaje: String? = null
)

data class LibroState(
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val mensaje: String? = null,
    val libro: Libro? = null
)

data class LibrosState(
    val cargando: Boolean = false,
    val exito: Boolean = false,
    val mensaje: String? = null,
    val libros: List<Libro> = emptyList()
)

class ConexionViewModel : ViewModel() {

    // Estado observable: true = conectado, false = desconectado
    private val _estaConectado = MutableStateFlow(false)
    val estaConectado: StateFlow<Boolean> = _estaConectado

    private val _registroState = MutableStateFlow(RegistroState())
    val registroState: StateFlow<RegistroState> = _registroState

    // NUEVO: estado del update
    private val _updateState = MutableStateFlow(UpdateState())
    val updateState: StateFlow<UpdateState> = _updateState

    // NUEVO: estado para READ_LIBRO
    private val _libroState = MutableStateFlow(LibroState())
    val libroState: StateFlow<LibroState> = _libroState

    // NUEVO: estado para READALL_LIBRO
    private val _librosState = MutableStateFlow(LibrosState())
    val librosState: StateFlow<LibrosState> = _librosState





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

    fun actualizarUsuario(
        idUsuario: Int,
        nombreUsuarioNuevo: String,
        passNueva: String,
        confirmPass: String,
        tipoUsuarioActual: String,
        puntosActuales: Int,
        fechaRegistroMs: Long,
        fechaNacimientoMs: Long
    ) {
        // Validaciones
        if (idUsuario <= 0) {
            _updateState.value = UpdateState(exito = false, mensaje = "ID de usuario inválido.")
            return
        }
        if (nombreUsuarioNuevo.isBlank()) {
            _updateState.value = UpdateState(exito = false, mensaje = "El nombre de usuario no puede estar vacío.")
            return
        }
        if (passNueva.isBlank() || confirmPass.isBlank()) {
            _updateState.value = UpdateState(exito = false, mensaje = "La contraseña no puede estar vacía.")
            return
        }
        if (passNueva != confirmPass) {
            _updateState.value = UpdateState(exito = false, mensaje = "Las contraseñas no coinciden.")
            return
        }

        // Convertir fechas desde millis (sin parsear strings)
        val fechaRegistro = if (fechaRegistroMs > 0) Date(fechaRegistroMs) else Date()
        val fechaNacimiento = if (fechaNacimientoMs > 0) Date(fechaNacimientoMs) else Date()

        // Construir pojospi.Usuario completo (lo exige el procedimiento)
        val usuarioPojo = Usuario().apply {
            setId_usuario(idUsuario)                 // no lo usa el SP, pero está bien
            setNombre_usuario(nombreUsuarioNuevo.trim())
            setContrasena(passNueva)
            setTipoUsuario(tipoUsuarioActual)        // 'R' o 'A'
            setPuntos(puntosActuales)
            setFechaRegistro(fechaRegistro)          // no lo actualiza el SP, pero el POJO queda completo
            setFechaNacimiento(fechaNacimiento)
            // NO tocamos correo aquí, porque el SP no lo actualiza
        }

        _updateState.value = UpdateState(cargando = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cliente = ClienteSocket(
                    ClienteConfig.getServerIP(),
                    ClienteConfig.PUERTO_SERVIDOR
                )

                val peticion = Peticion(Peticion.TipoOperacion.UPDATE_USUARIO, idUsuario, usuarioPojo)
                val respuesta = cliente.enviarPeticion(peticion)

                _updateState.value =
                    if (respuesta?.isExito == true) {
                        UpdateState(cargando = false, exito = true, mensaje = respuesta.mensaje ?: "Usuario actualizado.")
                    } else {
                        UpdateState(cargando = false, exito = false, mensaje = respuesta?.mensaje ?: "No se pudo actualizar.")
                    }

            } catch (e: Exception) {
                _updateState.value = UpdateState(
                    cargando = false,
                    exito = false,
                    mensaje = "Error de conexión: ${e.message}"
                )
            }
        }
    }


    fun leerLibro(idLibro: Int) {
        if (idLibro <= 0) {
            _libroState.value = LibroState(exito = false, mensaje = "ID de libro inválido.")
            return
        }

        _libroState.value = LibroState(cargando = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cliente = ClienteSocket(
                    ClienteConfig.getServerIP(),
                    ClienteConfig.PUERTO_SERVIDOR
                )

                val peticion = Peticion(Peticion.TipoOperacion.READ_LIBRO).apply {
                    setIdLibro(idLibro)
                }

                val respuesta = cliente.enviarPeticion(peticion)

                _libroState.value =
                    if (respuesta?.isExito == true && respuesta.libro != null) {
                        LibroState(cargando = false, exito = true, mensaje = respuesta.mensaje, libro = respuesta.libro)
                    } else {
                        LibroState(cargando = false, exito = false, mensaje = respuesta?.mensaje ?: "No se pudo leer el libro.")
                    }

            } catch (e: Exception) {
                _libroState.value = LibroState(
                    cargando = false,
                    exito = false,
                    mensaje = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun leerTodosLibros() {
        _librosState.value = LibrosState(cargando = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val cliente = ClienteSocket(
                    ClienteConfig.getServerIP(),
                    ClienteConfig.PUERTO_SERVIDOR
                )

                val peticion = Peticion(Peticion.TipoOperacion.READALL_LIBRO)
                val respuesta = cliente.enviarPeticion(peticion)

                _librosState.value =
                    if (respuesta?.isExito == true) {
                        LibrosState(
                            cargando = false,
                            exito = true,
                            mensaje = respuesta.mensaje,
                            libros = respuesta.libros ?: emptyList()
                        )
                    } else {
                        LibrosState(
                            cargando = false,
                            exito = false,
                            mensaje = respuesta?.mensaje ?: "No se pudieron leer los libros."
                        )
                    }

            } catch (e: Exception) {
                _librosState.value = LibrosState(
                    cargando = false,
                    exito = false,
                    mensaje = "Error de conexión: ${e.message}"
                )
            }
        }
    }







}