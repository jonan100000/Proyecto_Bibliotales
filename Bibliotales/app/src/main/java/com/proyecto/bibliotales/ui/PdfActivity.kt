package com.proyecto.bibliotales.ui

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.proyecto.bibliotales.R
import java.io.File
import java.io.FileOutputStream

class PdfActivity : BaseActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.activity_pdf)

        webView = findViewById(R.id.webViewPDF)
        cargarPDFenWebView()
        configurarNavegacionAtras()
    }

    private fun cargarPDFenWebView() {
        // Obtener el nombre del PDF del intent
        val pdfName = intent.getStringExtra("PDF_NAME") ?: "Rebuild-World-volumen-1.1.pdf"

        // Mostrar mensaje para depuración
        Toast.makeText(this, "Cargando: $pdfName", Toast.LENGTH_SHORT).show()

        // 1. Ruta completa en assets
        val rutaAssets = "libros/$pdfName"
        val pdfFile = File(filesDir, pdfName)

        try {
            // Verificar si el archivo ya existe, si no copiarlo desde assets
            if (!pdfFile.exists()) {
                // Verificar si el archivo existe en assets
                try {
                    assets.open(rutaAssets).use { input ->
                        FileOutputStream(pdfFile).use { output ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: PDF no encontrado", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
            }

            // 2. Configuración MÍNIMA del WebView
            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true

            // 3. WebViewClient simple
            webView.webViewClient = WebViewClient()

            // 4. Cargar PDF.js
            val viewerUrl = "file:///android_asset/pdfjs/web/viewer.html?file=${pdfFile.absolutePath}"
            webView.loadUrl(viewerUrl)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun configurarNavegacionAtras() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()  // Navegación dentro del PDF.js
                } else {
                    isEnabled = false
                    finish()  // Cerrar activity
                }
            }
        })
    }
}