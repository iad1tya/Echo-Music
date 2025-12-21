package iad1tya.echo.music.dlna

import android.content.Context
import android.util.Log
import fi.iki.elonen.NanoHTTPD
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.net.NetworkInterface

class DLNAMediaServer(
    private val context: Context,
    port: Int = 8080
) : NanoHTTPD(port) {
    private val TAG = "DLNAMediaServer"
    private var currentMediaUrl: String? = null
    
    fun getServerUrl(): String {
        val ipAddress = getLocalIpAddress()
        return "http://$ipAddress:$listeningPort"
    }
    
    fun setMediaUrl(url: String) {
        currentMediaUrl = url
    }
    
    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        Log.d(TAG, "Serving request: $uri")
        
        return when {
            uri.startsWith("/media") -> serveMedia(session)
            else -> newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
        }
    }
    
    private fun serveMedia(session: IHTTPSession): Response {
        currentMediaUrl?.let { url ->
            try {
                // For local files
                if (url.startsWith("file://")) {
                    val file = File(url.removePrefix("file://"))
                    if (file.exists()) {
                        val mimeType = getMimeType(file.extension)
                        val fis = FileInputStream(file)
                        return newChunkedResponse(Response.Status.OK, mimeType, fis)
                    }
                }
                
                // For HTTP URLs, proxy the content
                val client = okhttp3.OkHttpClient()
                val requestBuilder = okhttp3.Request.Builder().url(url)
                
                // Forward range header if present
                val range = session.headers["range"]
                if (range != null) {
                    requestBuilder.header("Range", range)
                }
                
                val response = client.newCall(requestBuilder.build()).execute()
                val inputStream = response.body?.byteStream()
                
                if (inputStream != null) {
                    val mimeType = response.header("Content-Type", "audio/mpeg") ?: "audio/mpeg"
                    val status = if (response.code == 206) Response.Status.PARTIAL_CONTENT else Response.Status.OK
                    
                    val nanoResponse = newChunkedResponse(status, mimeType, inputStream)
                    
                    // Forward content length if available
                    val contentLength = response.header("Content-Length")
                    if (contentLength != null) {
                         nanoResponse.addHeader("Content-Length", contentLength)
                    }
                    
                    // Forward content range if available
                    val contentRange = response.header("Content-Range")
                    if (contentRange != null) {
                        nanoResponse.addHeader("Content-Range", contentRange)
                    }
                    
                    return nanoResponse
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error serving media", e)
            }
        }
        
        return newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            MIME_PLAINTEXT,
            "Media not available"
        )
    }
    
    private fun getMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "mp3" -> "audio/mpeg"
            "m4a" -> "audio/mp4"
            "aac" -> "audio/aac"
            "flac" -> "audio/flac"
            "ogg" -> "audio/ogg"
            "wav" -> "audio/wav"
            else -> "audio/*"
        }
    }
    
    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address.address.size == 4) {
                        return address.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting local IP", e)
        }
        return "127.0.0.1"
    }
}
