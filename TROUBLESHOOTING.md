# Echo Music - Troubleshooting Guide

This comprehensive troubleshooting guide helps you resolve common issues with Echo Music.

## Table of Contents

- [Common Issues](#common-issues)
- [Installation Issues](#installation-issues)
- [Playback Issues](#playback-issues)
- [Login Issues](#login-issues)
- [Performance Issues](#performance-issues)
- [Network Issues](#network-issues)
- [UI Issues](#ui-issues)
- [Data Issues](#data-issues)
- [Build Issues](#build-issues)
- [Getting Help](#getting-help)

## Common Issues

### App Won't Start

#### Symptoms
- App crashes immediately on startup
- Black screen appears
- App freezes on splash screen

#### Solutions

1. **Check Device Compatibility**
   ```
   Minimum Requirements:
   - Android 8.0 (API 26) or higher
   - 2GB RAM minimum
   - 100MB free storage
   ```

2. **Clear App Data**
   ```
   Settings → Apps → Echo Music → Storage → Clear Data
   ```

3. **Restart Device**
   ```
   Power off → Wait 10 seconds → Power on
   ```

4. **Reinstall App**
   ```
   Uninstall → Restart device → Reinstall from trusted source
   ```

### Music Won't Play

#### Symptoms
- Play button doesn't respond
- Music starts but stops immediately
- "Song not available" error

#### Solutions

1. **Check Internet Connection**
   ```
   - Ensure stable internet connection
   - Try switching between WiFi and mobile data
   - Check if other apps can access internet
   ```

2. **Clear Cache**
   ```
   Settings → Apps → Echo Music → Storage → Clear Cache
   ```

3. **Check Permissions**
   ```
   Settings → Apps → Echo Music → Permissions
   Ensure these are enabled:
   - Storage
   - Microphone (for voice search)
   - Notifications
   ```

4. **Restart Music Service**
   ```
   Settings → Apps → Echo Music → Force Stop
   Wait 5 seconds → Open app again
   ```

## Installation Issues

### Installation Fails

#### Symptoms
- "App not installed" error
- Installation progress stops
- "Package appears to be corrupt" error

#### Solutions

1. **Enable Unknown Sources**
   ```
   Settings → Security → Unknown Sources → Enable
   ```

2. **Free Up Storage Space**
   ```
   - Delete unused apps
   - Clear cache and temporary files
   - Move photos/videos to cloud storage
   ```

3. **Check APK Integrity**
   ```
   - Re-download APK from official source
   - Verify file size matches expected size
   - Check if APK is corrupted
   ```

4. **Disable Antivirus**
   ```
   Temporarily disable antivirus software
   Install app → Re-enable antivirus
   ```

### Update Issues

#### Symptoms
- Update fails to install
- App crashes after update
- Features missing after update

#### Solutions

1. **Clear Update Cache**
   ```
   Settings → Apps → Google Play Store → Storage → Clear Cache
   ```

2. **Uninstall and Reinstall**
   ```
   Uninstall current version → Install latest version
   ```

3. **Check Storage Space**
   ```
   Ensure at least 200MB free space for updates
   ```

## Playback Issues

### Audio Quality Problems

#### Symptoms
- Poor audio quality
- Audio cutting out
- Distorted sound

#### Solutions

1. **Check Audio Settings**
   ```
   App Settings → Audio → Quality
   Try different quality settings
   ```

2. **Disable Audio Effects**
   ```
   App Settings → Audio → Equalizer → Disable
   ```

3. **Check Device Audio**
   ```
   - Test with other music apps
   - Check device volume levels
   - Try different headphones/speakers
   ```

### Playback Stuttering

#### Symptoms
- Music pauses frequently
- Audio skips or stutters
- Buffering issues

#### Solutions

1. **Check Network Connection**
   ```
   - Use WiFi instead of mobile data
   - Move closer to router
   - Check network speed
   ```

2. **Reduce Quality**
   ```
   App Settings → Audio → Quality → Lower setting
   ```

3. **Clear Cache**
   ```
   Settings → Apps → Echo Music → Storage → Clear Cache
   ```

4. **Restart App**
   ```
   Force stop app → Wait 10 seconds → Restart
   ```

### Background Playback Issues

#### Symptoms
- Music stops when app is minimized
- No lock screen controls
- Notification controls missing

#### Solutions

1. **Check Battery Optimization**
   ```
   Settings → Battery → Battery Optimization
   Find Echo Music → Don't Optimize
   ```

2. **Enable Background App Refresh**
   ```
   Settings → Apps → Echo Music → Battery → Background Activity → Allow
   ```

3. **Check Notification Settings**
   ```
   Settings → Apps → Echo Music → Notifications → Allow
   ```

## Login Issues

### YouTube Login Problems

#### Symptoms
- Login fails repeatedly
- "Invalid credentials" error
- Login page doesn't load

#### Solutions

1. **Check Credentials**
   ```
   - Verify email and password
   - Try logging in on YouTube website first
   - Check if 2FA is enabled
   ```

2. **Clear Login Data**
   ```
   App Settings → Accounts → YouTube → Sign Out
   Clear app data → Try logging in again
   ```

3. **Check Network**
   ```
   - Ensure stable internet connection
   - Try different network
   - Disable VPN if using one
   ```

### Spotify Login Issues

#### Symptoms
- Spotify login fails
- "Authentication error" message
- Spotify features not working

#### Solutions

1. **Check Spotify Account**
   ```
   - Verify Spotify account is active
   - Check if account has premium features
   - Try logging in on Spotify app first
   ```

2. **Clear Spotify Data**
   ```
   App Settings → Accounts → Spotify → Sign Out
   Clear app data → Try logging in again
   ```

3. **Check API Keys**
   ```
   - Verify Spotify API keys are correct
   - Check if API keys have expired
   - Contact developer for API key issues
   ```

## Performance Issues

### App Runs Slowly

#### Symptoms
- App takes long to load
- UI is sluggish
- Frequent freezes

#### Solutions

1. **Close Background Apps**
   ```
   Recent Apps → Close unnecessary apps
   ```

2. **Clear Cache**
   ```
   Settings → Apps → Echo Music → Storage → Clear Cache
   ```

3. **Restart Device**
   ```
   Power off → Wait 10 seconds → Power on
   ```

4. **Check Storage Space**
   ```
   Ensure at least 1GB free storage
   ```

### High Battery Usage

#### Symptoms
- Battery drains quickly
- App uses excessive battery
- Device heats up

#### Solutions

1. **Check Battery Usage**
   ```
   Settings → Battery → App Usage
   Check Echo Music battery usage
   ```

2. **Optimize Settings**
   ```
   App Settings → Performance → Battery Saver Mode
   ```

3. **Disable Background Activity**
   ```
   Settings → Apps → Echo Music → Battery → Background Activity → Restrict
   ```

4. **Update App**
   ```
   Install latest version for performance improvements
   ```

## Network Issues

### Connection Problems

#### Symptoms
- "No internet connection" error
- Slow loading times
- Frequent disconnections

#### Solutions

1. **Check Network Connection**
   ```
   - Test internet with other apps
   - Try different network
   - Restart router/modem
   ```

2. **Check Firewall Settings**
   ```
   - Disable firewall temporarily
   - Add Echo Music to firewall exceptions
   - Check corporate network restrictions
   ```

3. **Use Different DNS**
   ```
   Network Settings → DNS → Use 8.8.8.8 and 8.8.4.4
   ```

### Proxy Issues

#### Symptoms
- App won't connect through proxy
- "Proxy error" messages
- Features not working with proxy

#### Solutions

1. **Check Proxy Settings**
   ```
   App Settings → Network → Proxy
   Verify proxy configuration
   ```

2. **Try Different Proxy**
   ```
   - Test with different proxy server
   - Check proxy authentication
   - Verify proxy supports HTTPS
   ```

3. **Disable Proxy**
   ```
   Temporarily disable proxy to test
   ```

## UI Issues

### Display Problems

#### Symptoms
- UI elements not visible
- Text is too small/large
- Colors are wrong

#### Solutions

1. **Check Display Settings**
   ```
   Settings → Display → Font Size → Adjust
   Settings → Display → Display Size → Adjust
   ```

2. **Check App Theme**
   ```
   App Settings → Appearance → Theme
   Try different theme options
   ```

3. **Restart App**
   ```
   Force stop → Wait 5 seconds → Restart
   ```

### Navigation Issues

#### Symptoms
- Can't navigate between screens
- Back button doesn't work
- Menu items not responding

#### Solutions

1. **Check Gesture Settings**
   ```
   Settings → System → Gestures
   Verify navigation gestures are enabled
   ```

2. **Restart App**
   ```
   Force stop → Wait 5 seconds → Restart
   ```

3. **Clear App Data**
   ```
   Settings → Apps → Echo Music → Storage → Clear Data
   ```

## Data Issues

### Playlist Problems

#### Symptoms
- Playlists not loading
- Playlist items missing
- Can't create new playlists

#### Solutions

1. **Sync Playlists**
   ```
   App Settings → Playlists → Sync Now
   ```

2. **Clear Playlist Cache**
   ```
   App Settings → Storage → Clear Playlist Cache
   ```

3. **Recreate Playlists**
   ```
   Delete problematic playlists → Create new ones
   ```

### Download Issues

#### Symptoms
- Downloads fail
- Downloaded songs won't play
- Downloads disappear

#### Solutions

1. **Check Storage Space**
   ```
   Ensure sufficient storage for downloads
   ```

2. **Check Download Settings**
   ```
   App Settings → Downloads → Quality
   Try different quality settings
   ```

3. **Clear Download Cache**
   ```
   App Settings → Storage → Clear Download Cache
   ```

## Build Issues

### Development Setup Problems

#### Symptoms
- Project won't build
- Gradle sync fails
- Dependencies not found

#### Solutions

1. **Check Android Studio Version**
   ```
   Use Android Studio Arctic Fox or later
   ```

2. **Check SDK Version**
   ```
   Android SDK 26 or later required
   ```

3. **Clean and Rebuild**
   ```bash
   ./gradlew clean
   ./gradlew build
   ```

4. **Check Internet Connection**
   ```
   Gradle needs internet to download dependencies
   ```

### Build Errors

#### Symptoms
- Compilation errors
- Resource not found errors
- Permission errors

#### Solutions

1. **Check Code Syntax**
   ```
   Fix any syntax errors in code
   ```

2. **Check Resource Files**
   ```
   Verify all resource files exist
   ```

3. **Check Permissions**
   ```
   Verify all required permissions are declared
   ```

## Getting Help

### Before Asking for Help

1. **Check This Guide**
   - Look for your specific issue
   - Try the suggested solutions

2. **Gather Information**
   - Device model and Android version
   - App version
   - Error messages (screenshots)
   - Steps to reproduce the issue

3. **Try Basic Troubleshooting**
   - Restart the app
   - Restart the device
   - Clear cache and data
   - Update to latest version

### Contact Methods

#### GitHub Issues
- **Bug Reports**: [Create an issue](https://github.com/iad1tya/Echo-Music/issues/new?template=bug_report.md)
- **Feature Requests**: [Create an issue](https://github.com/iad1tya/Echo-Music/issues/new?template=feature_request.md)

#### GitHub Discussions
- **General Questions**: [GitHub Discussions](https://github.com/iad1tya/Echo-Music/discussions)
- **Community Help**: Ask other users for help

#### Direct Contact
- **Developer**: [iad1tya](https://github.com/iad1tya)
- **Email**: Check GitHub profile for contact information

### Information to Include

When reporting issues, please include:

1. **Device Information**
   ```
   - Device model
   - Android version
   - RAM and storage
   ```

2. **App Information**
   ```
   - App version
   - Build variant (FOSS/Full)
   - Installation method
   ```

3. **Issue Details**
   ```
   - Description of the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots or videos
   ```

4. **Logs**
   ```
   - Logcat output (if available)
   - Crash logs
   - Error messages
   ```

### Community Support

- **Reddit**: Check relevant subreddits
- **Discord**: Join community Discord server
- **Telegram**: Join community Telegram group
- **Forums**: Check Android development forums

---

This troubleshooting guide covers the most common issues users encounter with Echo Music. If you don't find your issue here, please create a GitHub issue with detailed information about your problem.
