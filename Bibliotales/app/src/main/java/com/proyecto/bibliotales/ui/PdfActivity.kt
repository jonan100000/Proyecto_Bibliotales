package com.proyecto.bibliotales.ui

import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.proyecto.bibliotales.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfActivity : BaseActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentLayout(R.layout.activity_pdf)

        webView = findViewById(R.id.webViewPDF)
        configurarWebView()
        cargarPDFenWebView()
        configurarNavegacionAtras()
    }

    private fun configurarWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        webView.webViewClient = WebViewClient()
    }

    private fun cargarPDFenWebView() {
        // Ahora PDF_NAME viene de la BD (url_archivo)
        val pdfName = intent.getStringExtra("PDF_NAME") ?: run {
            Toast.makeText(this, "Error: PDF_NAME vacío", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        Toast.makeText(this, "Cargando: $pdfName", Toast.LENGTH_SHORT).show()

        val pdfFile = File(filesDir, pdfName)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1) Si no existe en cache local, descargar desde Supabase
                if (!pdfFile.exists()) {
                    val encodedName = Uri.encode(pdfName)
                    val pdfUrl =
                        "https://byoiqofayvaxwiapbdiq.supabase.co/storage/v1/object/public/libros/$encodedName"

                    descargarArchivo(pdfUrl, pdfFile)
                }

                // 2) Abrir con PDF.js en el hilo principal
                withContext(Dispatchers.Main) {
                    val viewerUrl =
                        "file:///android_asset/pdfjs/web/viewer.html?file=${pdfFile.absolutePath}"
                    webView.loadUrl(viewerUrl)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PdfActivity, "Error al cargar el PDF: ${e.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun descargarArchivo(urlStr: String, destino: File) {
        val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 30000
            instanceFollowRedirects = true
        }

        // Si el servidor devuelve error (404, 403...), lo lanzamos
        val code = conn.responseCode
        if (code !in 200..299) {
            conn.disconnect()
            throw RuntimeException("HTTP $code al descargar PDF")
        }

        conn.inputStream.use { input ->
            FileOutputStream(destino).use { output ->
                input.copyTo(output)
            }
        }
        conn.disconnect()
    }

    private fun configurarNavegacionAtras() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    finish()
                }
            }
        })
    }
}