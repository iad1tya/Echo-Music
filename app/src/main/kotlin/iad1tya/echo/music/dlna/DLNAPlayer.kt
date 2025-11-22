package iad1tya.echo.music.dlna

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class DLNAPlayer(
    private val device: DLNADevice
) {
    private val TAG = "DLNAPlayer"
    
    suspend fun setAVTransportURI(mediaUrl: String, metadata: String = "") = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:AVTransport:1#SetAVTransportURI"
            val soapBody = buildSetAVTransportURIRequest(mediaUrl, metadata)
            
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Set media URL: $mediaUrl")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting AV transport URI", e)
            false
        }
    }
    
    suspend fun play() = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:AVTransport:1#Play"
            val soapBody = buildPlayRequest()
            
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Sent play command")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error playing", e)
            false
        }
    }
    
    suspend fun pause() = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:AVTransport:1#Pause"
            val soapBody = buildPauseRequest()
            
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Sent pause command")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
            false
        }
    }
    
    suspend fun stop() = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:AVTransport:1#Stop"
            val soapBody = buildStopRequest()
            
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Sent stop command")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping", e)
            false
        }
    }
    
    suspend fun seek(position: String) = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:AVTransport:1#Seek"
            val soapBody = buildSeekRequest(position)
            
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Sent seek command to $position")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking", e)
            false
        }
    }
    
    suspend fun setVolume(volume: Int) = withContext(Dispatchers.IO) {
        try {
            val soapAction = "urn:schemas-upnp-org:service:RenderingControl:1#SetVolume"
            val soapBody = buildSetVolumeRequest(volume)
            
            // Note: Volume control URL might be different from transport control URL
            sendSOAPRequest(device.controlUrl, soapAction, soapBody)
            Log.d(TAG, "Set volume to $volume")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting volume", e)
            false
        }
    }
    
    private fun sendSOAPRequest(url: String, soapAction: String, soapBody: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
        connection.setRequestProperty("SOAPAction", "\"$soapAction\"")
        connection.doOutput = true
        connection.doInput = true
        
        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(soapBody)
            writer.flush()
        }
        
        val responseCode = connection.responseCode
        val response = if (responseCode == 200) {
            BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
        } else {
            BufferedReader(InputStreamReader(connection.errorStream ?: connection.inputStream)).use { it.readText() }
        }
        
        connection.disconnect()
        
        if (responseCode != 200) {
            throw Exception("SOAP request failed with code $responseCode: $response")
        }
        
        return response
    }
    
    private fun buildSetAVTransportURIRequest(mediaUrl: String, metadata: String): String {
        val metadataXml = if (metadata.isNotEmpty()) {
            metadata.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
        } else {
            ""
        }
        
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:SetAVTransportURI xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                        <InstanceID>0</InstanceID>
                        <CurrentURI>$mediaUrl</CurrentURI>
                        <CurrentURIMetaData>$metadataXml</CurrentURIMetaData>
                    </u:SetAVTransportURI>
                </s:Body>
            </s:Envelope>"""
    }
    
    private fun buildPlayRequest(): String {
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:Play xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                        <InstanceID>0</InstanceID>
                        <Speed>1</Speed>
                    </u:Play>
                </s:Body>
            </s:Envelope>"""
    }
    
    private fun buildPauseRequest(): String {
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:Pause xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                        <InstanceID>0</InstanceID>
                    </u:Pause>
                </s:Body>
            </s:Envelope>"""
    }
    
    private fun buildStopRequest(): String {
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:Stop xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                        <InstanceID>0</InstanceID>
                    </u:Stop>
                </s:Body>
            </s:Envelope>"""
    }
    
    private fun buildSeekRequest(position: String): String {
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:Seek xmlns:u="urn:schemas-upnp-org:service:AVTransport:1">
                        <InstanceID>0</InstanceID>
                        <Unit>REL_TIME</Unit>
                        <Target>$position</Target>
                    </u:Seek>
                </s:Body>
            </s:Envelope>"""
    }
    
    private fun buildSetVolumeRequest(volume: Int): String {
        return """<?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:SetVolume xmlns:u="urn:schemas-upnp-org:service:RenderingControl:1">
                        <InstanceID>0</InstanceID>
                        <Channel>Master</Channel>
                        <DesiredVolume>$volume</DesiredVolume>
                    </u:SetVolume>
                </s:Body>
            </s:Envelope>"""
    }
}
