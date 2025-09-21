# Echo Music Setup Guide 🚀

This guide will help you set up Echo Music for development and production use.

## 📋 Prerequisites

### Required Software

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Android SDK**: API level 26 or later
- **Git**: Latest version
- **Gradle**: 8.0 or later (included with Android Studio)

### System Requirements

- **Operating System**: Windows 10+, macOS 10.15+, or Linux Ubuntu 18.04+
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 10GB free space
- **Internet**: Required for downloading dependencies

## 🛠️ Development Setup

### 1. Clone the Repository

```bash
git clone https://github.com/iad1tya/Echo-Music.git
cd Echo-Music
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open an existing Android Studio project"
3. Navigate to the Echo-Music directory
4. Click "OK"

### 3. Configure Local Properties

1. Copy the template file:
   ```bash
   cp local.properties.template local.properties
   ```

2. Edit `local.properties` and add your Android SDK path:
   ```properties
   # Android SDK location
   sdk.dir=/path/to/your/Android/sdk
   
   # Optional: Sentry DSN for crash reporting
   SENTRY_DSN=your_sentry_dsn_here
   
   # Optional: Sentry Auth Token for uploads
   SENTRY_AUTH_TOKEN=your_sentry_auth_token_here
   ```

### 4. Sync Project

1. Android Studio will prompt you to sync the project
2. Click "Sync Now" or go to `File > Sync Project with Gradle Files`
3. Wait for the sync to complete

### 5. Build the Project

```bash
# Debug build
./gradlew assembleDebug

# FOSS build (no Google services)
./gradlew assembleFossDebug

# Release build
./gradlew assembleRelease
```

## 🔥 Firebase Setup (Optional)

Firebase is used for analytics and crash reporting. The app works without it, but some features may be limited.

### 1. Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: "Echo Music"
4. Follow the setup wizard

### 2. Add Android App

1. In your Firebase project, click "Add app" and select Android
2. Enter package name: `iad1tya.echo.music`
3. Enter app nickname: "Echo Music"
4. Download the `google-services.json` file
5. Place it in the `app/` directory

### 3. Add Debug App (Optional)

For debug builds, add another app:
1. Package name: `iad1tya.echo.music.dev`
2. Download another `google-services.json`
3. Merge the configurations or use the template

### 4. Enable Services

In Firebase Console, enable:
- **Analytics**: For usage tracking
- **Crashlytics**: For crash reporting
- **Performance Monitoring**: For app performance

## 🐛 Sentry Setup (Optional)

Sentry provides advanced crash reporting and error tracking.

### 1. Create Sentry Project

1. Go to [Sentry.io](https://sentry.io/)
2. Create a new project for Android
3. Get your DSN from project settings

### 2. Configure Sentry

Add to `local.properties`:
```properties
SENTRY_DSN=https://your-dsn@sentry.io/project-id
SENTRY_AUTH_TOKEN=your_auth_token_here
```

## 🎵 Music Service Setup

### YouTube Music

No additional setup required. The app uses public APIs.

### Spotify Integration

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new app
3. Note your Client ID and Client Secret
4. Add redirect URI: `echo-music://callback`

**Note**: Spotify credentials should be added to the app configuration.

## 🏗️ Build Variants

Echo Music supports multiple build variants:

### FOSS Build
- No Google services
- No Firebase
- No Sentry
- Open source only

```bash
./gradlew assembleFossDebug
```

### Full Build
- Includes all services
- Firebase Analytics
- Sentry crash reporting
- All features enabled

```bash
./gradlew assembleFullDebug
```

## 🧪 Testing

### Unit Tests

```bash
./gradlew test
```

### Instrumented Tests

```bash
./gradlew connectedAndroidTest
```

### Lint Checks

```bash
./gradlew lint
```

### All Tests

```bash
./gradlew check
```

## 📱 Running the App

### On Device/Emulator

1. Connect Android device or start emulator
2. Enable USB debugging (for physical device)
3. Run the app:
   ```bash
   ./gradlew installDebug
   ```

### Debug Build

```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Release Build

```bash
./gradlew assembleRelease
```

## 🔧 Configuration

### App Configuration

Edit `app/src/main/java/com/maxrave/simpmusic/common/Config.kt`:

```kotlin
object Config {
    const val APP_NAME = "Echo Music"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1
    
    // API endpoints
    const val YOUTUBE_API_BASE = "https://music.youtube.com"
    const val SPOTIFY_API_BASE = "https://api.spotify.com"
    
    // Feature flags
    const val ENABLE_ANALYTICS = true
    const val ENABLE_CRASH_REPORTING = true
}
```

### Build Configuration

Edit `app/build.gradle.kts` for:
- Version numbers
- Dependencies
- Build types
- Product flavors

## 🚀 Deployment

### Debug APK

```bash
./gradlew assembleDebug
# APK location: app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

```bash
./gradlew assembleRelease
# APK location: app/build/outputs/apk/release/app-release.apk
```

### AAB (Android App Bundle)

```bash
./gradlew bundleRelease
# AAB location: app/build/outputs/bundle/release/app-release.aab
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Build Failures

**Error**: `SDK location not found`
**Solution**: Set correct SDK path in `local.properties`

**Error**: `Could not find google-services.json`
**Solution**: Add Firebase configuration or use FOSS build

**Error**: `Gradle sync failed`
**Solution**: 
- Check internet connection
- Clear Gradle cache: `./gradlew clean`
- Invalidate caches in Android Studio

#### 2. Runtime Issues

**Error**: App crashes on startup
**Solution**: 
- Check device compatibility (Android 8.0+)
- Enable USB debugging
- Check logs: `adb logcat`

**Error**: Music not playing
**Solution**:
- Check internet connection
- Verify YouTube Music access
- Check audio permissions

#### 3. Firebase Issues

**Error**: Analytics not working
**Solution**:
- Verify `google-services.json` is correct
- Check package name matches
- Enable Analytics in Firebase Console

### Debug Information

Enable debug logging:

```kotlin
// In your Application class
if (BuildConfig.DEBUG) {
    Log.d("EchoMusic", "Debug mode enabled")
}
```

### Logs

View app logs:
```bash
adb logcat | grep EchoMusic
```

## 📚 Additional Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Sentry Android Documentation](https://docs.sentry.io/platforms/android/)

## 🤝 Getting Help

If you encounter issues:

1. Check the [GitHub Issues](https://github.com/iad1tya/Echo-Music/issues)
2. Search existing discussions
3. Create a new issue with:
   - Clear description
   - Steps to reproduce
   - Device information
   - Logs (if applicable)

## 📝 Next Steps

After setup:

1. Read the [Contributing Guidelines](CONTRIBUTING.md)
2. Check out the [API Documentation](docs/API.md)
3. Explore the codebase structure
4. Start contributing!

---

<div align="center">
  <p>Happy coding! 🚀</p>
  <p>If you have questions, don't hesitate to ask! 🤝</p>
</div>
