package com.proyecto.bibliotales.data.network

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.proyecto.bibliotales.data.models.ClienteConfig
import com.proyecto.bibliotales.data.models.ClienteSocket
import com.proyecto.bibliotales.data.models.Peticion
import com.proyecto.bibliotales.data.models.Respuesta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket



