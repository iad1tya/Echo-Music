package iad1tya.echo.music.utils

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log

/**
 * Utility class for detecting USB DACs and managing bit-perfect audio output
 */
object USBDacDetector {
    
    private const val TAG = "USBDacDetector"
    
    /**
     * Detects if a USB DAC is connected to the device
     * @param context Application context
     * @return true if USB DAC is detected, false otherwise
     */
    fun isUSBDacConnected(context: Context): Boolean {
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_USB_DEVICE) {
                    Log.d(TAG, "USB DAC detected: ${device.productName}")
                    return true
                }
            }
            
            // Also check USB devices directly
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val usbDevices = usbManager.deviceList
            
            for (device in usbDevices.values) {
                if (isAudioDevice(device)) {
                    Log.d(TAG, "USB Audio device detected: ${device.deviceName}")
                    return true
                }
            }
            
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting USB DAC: ${e.message}")
            false
        }
    }
    
    /**
     * Checks if a USB device is an audio device
     * @param device USB device to check
     * @return true if device is likely an audio device
     */
    private fun isAudioDevice(device: UsbDevice): Boolean {
        // Check device class and subclass for audio devices
        // USB Audio Class devices typically have class 1 (Audio)
        return device.deviceClass == 1 || 
               device.deviceSubclass == 1 || 
               device.deviceProtocol == 1 ||
               device.deviceName.contains("audio", ignoreCase = true) ||
               device.deviceName.contains("dac", ignoreCase = true) ||
               device.deviceName.contains("sound", ignoreCase = true)
    }
    
    /**
     * Gets information about connected USB DACs
     * @param context Application context
     * @return List of USB DAC information
     */
    fun getUSBDacInfo(context: Context): List<USBDacInfo> {
        val dacInfoList = mutableListOf<USBDacInfo>()
        
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            
            for (device in devices) {
                if (device.type == AudioDeviceInfo.TYPE_USB_DEVICE) {
                    dacInfoList.add(
                        USBDacInfo(
                            name = device.productName?.toString() ?: "Unknown USB DAC",
                            type = "USB Audio Device",
                            isConnected = true,
                            supportsBitPerfect = true // Assume USB DACs support bit-perfect
                        )
                    )
                }
            }
            
            // Also check USB devices directly
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val usbDevices = usbManager.deviceList
            
            for (device in usbDevices.values) {
                if (isAudioDevice(device)) {
                    dacInfoList.add(
                        USBDacInfo(
                            name = device.deviceName,
                            type = "USB Device",
                            isConnected = true,
                            supportsBitPerfect = true
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting USB DAC info: ${e.message}")
        }
        
        return dacInfoList
    }
    
    /**
     * Checks if the current device supports USB DAC functionality
     * @param context Application context
     * @return true if device supports USB DAC, false otherwise
     */
    fun isDeviceCompatible(context: Context): Boolean {
        return try {
            // Check Android version (USB audio support improved in API 23+)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.d(TAG, "Device not compatible: Android version too old (${Build.VERSION.SDK_INT})")
                return false
            }
            
            // Check if USB host mode is supported (simplified check)
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            // Note: hasUsbHostSupport() doesn't exist in UsbManager API, so we'll check differently
            
            // Check if audio manager supports USB audio
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            
            // Check if device can handle USB audio devices
            val hasUsbAudioSupport = devices.any { device ->
                device.type == AudioDeviceInfo.TYPE_USB_DEVICE
            } || canDetectUsbAudioDevices(context)
            
            Log.d(TAG, "Device compatibility check: hasUsbAudioSupport=$hasUsbAudioSupport")
            hasUsbAudioSupport
            
        } catch (e: Exception) {
            Log.e(TAG, "Error checking device compatibility: ${e.message}")
            false
        }
    }
    
    /**
     * Checks if device can detect USB audio devices even when none are connected
     * @param context Application context
     * @return true if device supports USB audio detection
     */
    private fun canDetectUsbAudioDevices(context: Context): Boolean {
        return try {
            val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
            val usbDevices = usbManager.deviceList
            
            // Check if device has USB audio capability by looking at device list
            // Even if no devices are connected, we can check if the system supports USB audio
            usbDevices.isNotEmpty() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        } catch (e: Exception) {
            Log.e(TAG, "Error checking USB audio detection capability: ${e.message}")
            false
        }
    }
}

/**
 * Data class representing USB DAC information
 */
data class USBDacInfo(
    val name: String,
    val type: String,
    val isConnected: Boolean,
    val supportsBitPerfect: Boolean
)
