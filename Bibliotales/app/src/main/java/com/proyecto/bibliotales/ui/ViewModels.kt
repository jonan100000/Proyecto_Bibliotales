package com.proyecto.bibliotales.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConexionViewModel : ViewModel() {

    // Estado observable: true = conectado, false = desconectado
    private val _estaConectado = MutableStateFlow(false)
    val estaConectado: StateFlow<Boolean> = _estaConectado

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

}