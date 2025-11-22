# DLNA/UPnP Setup Guide

Echo Music now supports streaming to DLNA/UPnP compatible devices on your local network!

## What is DLNA/UPnP?

DLNA (Digital Living Network Alliance) and UPnP (Universal Plug and Play) are protocols that allow devices on your network to discover and stream media to each other. This means you can play music from Echo Music on:

- Smart TVs
- Network speakers (Sonos, Denon, etc.)
- Home theater systems
- Media receivers
- Any device that supports DLNA/UPnP media rendering

## Requirements

1. **Same Network**: Your Android device and DLNA device must be on the same WiFi network
2. **WiFi Enabled**: WiFi must be enabled on your device
3. **Permissions**: Echo Music needs WiFi and network permissions (granted automatically)
4. **Compatible Device**: A DLNA/UPnP compatible media renderer on your network

## How to Use

### 1. Enable DLNA Discovery

DLNA device discovery starts automatically when you open Echo Music. The app will search for compatible devices on your network.

### 2. Find DLNA Devices

1. Open the **Now Playing** screen
2. Tap the **Audio Output** button (speaker icon)
3. Scroll down to the **DLNA/UPnP DEVICES** section
4. Available devices will appear automatically

### 3. Connect to a Device

1. Tap on any discovered DLNA device
2. Wait for the connection to establish
3. Your music will now play on the selected device!

### 4. Control Playback

Once connected:
- Use normal playback controls (play, pause, skip)
- Adjust volume from your phone
- Seek to different positions in the track
- Switch between songs in your queue

### 5. Disconnect

To switch back to your phone's speaker:
1. Tap the **Audio Output** button again
2. Select your phone or another output device
3. Or simply close the connection from the device list

## Troubleshooting

### No Devices Found

If you don't see any DLNA devices:

1. **Check WiFi**: Ensure both devices are on the same network
2. **Scan Again**: Tap the "Scan for Devices" button
3. **Device Compatibility**: Verify your device supports DLNA/UPnP
4. **Firewall**: Check if your router's firewall blocks UPnP/DLNA
5. **Device Power**: Make sure the DLNA device is powered on and network-connected

### Connection Issues

If you have trouble connecting:

1. **Restart App**: Close and reopen Echo Music
2. **Restart Device**: Power cycle your DLNA device
3. **Network**: Try disconnecting and reconnecting to WiFi
4. **Distance**: Move closer to your WiFi router for better signal

### Playback Problems

If audio isn't playing correctly:

1. **Format Support**: Some devices may not support all audio formats
2. **Network Speed**: Ensure you have good WiFi signal strength
3. **Device Volume**: Check the volume on both your phone and the DLNA device
4. **Restart Playback**: Stop and restart the track

## Supported Features

✅ **Device Discovery**: Automatic discovery of DLNA/UPnP devices
✅ **Playback Control**: Play, pause, stop, seek
✅ **Volume Control**: Adjust volume from your phone
✅ **Queue Management**: Full playlist and queue support
✅ **YouTube Music**: Stream YouTube Music content
✅ **Metadata**: Song title, artist, and artwork display

## Popular Compatible Devices

- **Smart TVs**: Samsung, LG, Sony, etc.
- **Speakers**: Sonos, Denon HEOS, Bose SoundTouch
- **Receivers**: Yamaha, Onkyo, Pioneer
- **Media Players**: Roku, Amazon Fire TV, Apple TV (via AirPlay bridge)
- **NAS Devices**: Synology, QNAP (when acting as renderer)
- **Software Renderers**: VLC, Kodi, Plex

## Technical Details

### Protocols Used
- **UPnP/DLNA**: For device discovery and control
- **HTTP Streaming**: For audio delivery to devices
- **SOAP**: For device control commands

### Network Requirements
- **Ports**: Dynamic port allocation (typically 1024-65535)
- **Multicast**: UDP multicast for device discovery (239.255.255.250:1900)
- **Protocol**: SSDP (Simple Service Discovery Protocol)

### Audio Formats
Most DLNA devices support:
- MP3
- AAC
- WAV
- FLAC

Echo Music will automatically transcode if needed for compatibility.

## Privacy & Security

- **Local Network Only**: DLNA works only on your local WiFi network
- **No Internet**: Device discovery doesn't require internet connection
- **No Data Collection**: No usage data is sent to external servers
- **Direct Streaming**: Audio streams directly from your device to the DLNA device

## Advanced Tips

### Multiple Devices
You can switch between different DLNA devices:
1. Disconnect from current device
2. Select a new device from the list
3. Playback will resume on the new device

### Network Performance
For best performance:
- Use 5GHz WiFi when possible
- Keep devices close to the router
- Avoid network congestion
- Use Quality of Service (QoS) settings on your router

### Compatibility Testing
To test if a device supports DLNA:
1. Look for DLNA/UPnP logo on the device
2. Check manufacturer's specifications
3. Try connecting using another DLNA app
4. Consult device manual for UPnP/DLNA settings

## Feedback & Support

If you encounter issues or have suggestions:
- Open an issue on GitHub
- Include device model and Android version
- Describe the problem in detail
- Attach logs if possible

## Comparison with Chromecast

| Feature | DLNA/UPnP | Chromecast |
|---------|-----------|------------|
| Local Network | ✅ Yes | ✅ Yes |
| Device Discovery | Automatic | Automatic |
| Internet Required | ❌ No | ✅ Yes (initial) |
| Device Support | Wide compatibility | Google devices |
| Protocol | UPnP/DLNA | Google Cast |
| Open Standard | ✅ Yes | ❌ No |

Both protocols are supported in Echo Music, giving you maximum flexibility!

---

Enjoy wireless music streaming with DLNA/UPnP! 🎵
