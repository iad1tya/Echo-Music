package iad1tya.echo.music.dlna

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DLNAManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "DLNAManager"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val ssdpDiscovery = SSDPDiscovery()
    private val mediaServer = DLNAMediaServer(context)
    
    private val _devices = MutableStateFlow<List<DLNADevice>>(emptyList())
    val devices: StateFlow<List<DLNADevice>> = _devices
    
    private val _selectedDevice = MutableStateFlow<DLNADevice?>(null)
    val selectedDevice: StateFlow<DLNADevice?> = _selectedDevice
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled
    
    private var currentPlayer: DLNAPlayer? = null
    
    fun start() {
        if (_isEnabled.value) return
        
        Log.d(TAG, "Starting DLNA service")
        _isEnabled.value = true
        
        try {
            mediaServer.start()
            Log.d(TAG, "Media server started on ${mediaServer.getServerUrl()}")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting media server", e)
        }
        
        startDiscovery()
    }
    
    fun stop() {
        Log.d(TAG, "Stopping DLNA service")
        _isEnabled.value = false
        
        ssdpDiscovery.stopDiscovery()
        
        try {
            mediaServer.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping media server", e)
        }
        
        _devices.value = emptyList()
        _selectedDevice.value = null
        currentPlayer = null
    }
    
    fun ensureStarted() {
        if (!_isEnabled.value) {
            start()
        }
    }

    fun prepareProxy(url: String): String {
        ensureStarted()
        mediaServer.setMediaUrl(url)
        return "${mediaServer.getServerUrl()}/media"
    }

    fun startDiscovery() {
        if (!_isEnabled.value) return
        
        Log.d(TAG, "Starting device discovery")
        val discoveredDevices = mutableSetOf<DLNADevice>()
        
        ssdpDiscovery.startDiscovery(scope) { device ->
            if (discoveredDevices.add(device)) {
                Log.d(TAG, "Discovered device: ${device.name}")
                _devices.value = discoveredDevices.toList()
            }
        }
    }
    
    fun selectDevice(device: DLNADevice?) {
        _selectedDevice.value = device
        currentPlayer = device?.let { DLNAPlayer(it) }
        Log.d(TAG, "Selected device: ${device?.name ?: "None"}")
    }
    
    suspend fun playMedia(mediaUrl: String, title: String = "", artist: String = ""): Boolean {
        val player = currentPlayer ?: return false
        
        return try {
            // Set media URL on server
            mediaServer.setMediaUrl(mediaUrl)
            
            // Always use server URL to proxy content to DLNA/Cast devices
            // This is required because:
            // 1. YouTube URLs are often IP-bound
            // 2. Some devices don't support HTTPS
            val streamUrl = "${mediaServer.getServerUrl()}/media"
            
            val metadata = buildMetadata(title, artist)
            
            if (player.setAVTransportURI(streamUrl, metadata)) {
                player.play()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing media", e)
            false
        }
    }
    
    suspend fun pause(): Boolean {
        return currentPlayer?.pause() ?: false
    }
    
    suspend fun resume(): Boolean {
        return currentPlayer?.play() ?: false
    }
    
    suspend fun stopPlayback(): Boolean {
        return currentPlayer?.stop() ?: false
    }
    
    suspend fun seek(positionMs: Long): Boolean {
        val player = currentPlayer ?: return false
        val position = formatTime(positionMs)
        return player.seek(position)
    }
    
    suspend fun setVolume(volume: Int): Boolean {
        return currentPlayer?.setVolume(volume.coerceIn(0, 100)) ?: false
    }
    
    private fun buildMetadata(title: String, artist: String): String {
        return """
            <DIDL-Lite xmlns="urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/" 
                       xmlns:dc="http://purl.org/dc/elements/1.1/" 
                       xmlns:upnp="urn:schemas-upnp-org:metadata-1-0/upnp/">
                <item id="1" parentID="0" restricted="1">
                    <dc:title>$title</dc:title>
                    <dc:creator>$artist</dc:creator>
                    <upnp:class>object.item.audioItem.musicTrack</upnp:class>
                </item>
            </DIDL-Lite>
        """.trimIndent()
    }
    
    private fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}
