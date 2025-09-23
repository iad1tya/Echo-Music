# Echo Music - Setup Guide

This comprehensive guide will help you set up Echo Music for development, testing, and building.

## Prerequisites

### Required Software

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **Android SDK**: API level 26 (Android 8.0) or later
- **Java Development Kit**: JDK 17 or later
- **Git**: Latest version
- **Gradle**: 8.0 or later (included with Android Studio)

### Recommended Tools

- **Android Studio**: Latest stable version
- **Android SDK Build Tools**: Latest version
- **Android Emulator**: API 30+ for testing
- **Physical Device**: For testing real-world performance

### System Requirements

- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 10GB free space for Android SDK and project files
- **OS**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 18.04+)

## Setup Instructions

### 1. Clone the Repository

```bash
# Clone the repository
git clone https://github.com/iad1tya/Echo-Music.git
cd Echo-Music

# Verify the clone was successful
ls -la
```

### 2. Configure Local Properties

Copy the template and configure your local settings:

```bash
cp local.properties.template local.properties
```

Edit `local.properties` and set your Android SDK path:

```properties
# Android SDK path (required)
sdk.dir=/path/to/your/android/sdk

# Optional: Firebase configuration (uncomment and fill if needed)
# SENTRY_DSN=your_sentry_dsn_here
# SENTRY_AUTH_TOKEN=your_sentry_auth_token_here

# Optional: Custom build configurations
# org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
# org.gradle.parallel=true
# org.gradle.caching=true
```

**Note**: The SDK path varies by operating system:
- **Windows**: `C:\Users\YourUsername\AppData\Local\Android\Sdk`
- **macOS**: `/Users/YourUsername/Library/Android/sdk`
- **Linux**: `/home/YourUsername/Android/Sdk`

### 3. Firebase Configuration (Optional)

Firebase configuration is optional for basic functionality. The app will work without it, but some features like analytics and crash reporting will be disabled.

#### Setting up Firebase

1. **Create a Firebase project**:
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Create a new project or use an existing one

2. **Add Android apps**:
   - Add two Android apps with these package names:
     - `iad1tya.echo.music` (for release builds)
     - `iad1tya.echo.music.dev` (for debug builds)

3. **Download configuration files**:
   ```bash
   # Copy the template
   cp app/google-services.json.template app/google-services.json
   ```

4. **Edit the configuration**:
   Replace the placeholder values in `app/google-services.json`:
   - `YOUR_PROJECT_NUMBER` → Your Firebase project number
   - `your-firebase-project-id` → Your Firebase project ID
   - `YOUR_MOBILE_SDK_APP_ID` → Your Firebase app ID
   - `YOUR_API_KEY` → Your Firebase API key

#### Firebase Features

When configured, Firebase provides:
- **Analytics**: App usage statistics and user behavior
- **Crashlytics**: Crash reporting and stability monitoring
- **Performance**: App performance monitoring
- **Remote Config**: Dynamic configuration management

### 4. Build the Project

#### First Build

```bash
# Clean and build the project
./gradlew clean
./gradlew assembleFossDebug
```

#### Build Variants

Echo Music supports multiple build variants:

```bash
# FOSS (Free and Open Source Software) builds
./gradlew assembleFossDebug      # Debug FOSS build
./gradlew assembleFossRelease    # Release FOSS build

# Full builds (with all features)
./gradlew assembleFullDebug      # Debug full build
./gradlew assembleFullRelease    # Release full build

# Install builds
./gradlew installFossDebug       # Install FOSS debug build
./gradlew installFullDebug       # Install full debug build
```

### 5. Run on Device/Emulator

#### Using Gradle

```bash
# Install and run FOSS debug build
./gradlew installFossDebug

# Install and run full debug build
./gradlew installFullDebug
```

#### Using Android Studio

1. Open the project in Android Studio
2. Select the desired build variant from the Build Variants panel
3. Click the Run button or press `Shift + F10`

#### Using ADB

```bash
# Install APK manually
adb install app/build/outputs/apk/foss/debug/app-foss-debug.apk

# Launch the app
adb shell am start -n iad1tya.echo.music.dev/.ui.MainActivity
```

## Project Structure

```
Echo-Music/
├── app/                          # Main application module
│   ├── src/main/java/           # Main source code
│   ├── src/main/res/            # Resources (layouts, strings, etc.)
│   ├── build.gradle.kts         # App module build configuration
│   └── google-services.json     # Firebase configuration
├── kotlinYtmusicScraper/        # YouTube Music scraping module
├── spotify/                     # Spotify integration module
├── aiService/                   # AI service module
├── ffmpeg-kit/                  # FFmpeg integration (currently disabled)
├── gradle/                      # Gradle wrapper and configuration
├── fastlane/                    # Fastlane configuration for releases
└── docs/                        # Documentation files
```

## Build Variants

| Variant | Description | Features |
|---------|-------------|----------|
| `fossDebug` | FOSS version (debug) | Core features, debug symbols |
| `fossRelease` | FOSS version (release) | Core features, optimized |
| `fullDebug` | Full version (debug) | All features, debug symbols |
| `fullRelease` | Full version (release) | All features, optimized |

## Development Workflow

### 1. Daily Development

```bash
# Start development session
git pull origin main
./gradlew clean
./gradlew assembleFossDebug

# Make changes and test
./gradlew installFossDebug

# Run tests
./gradlew test
```

### 2. Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run lint checks
./gradlew lint

# Generate test coverage report
./gradlew testDebugUnitTestCoverage
```

### 3. Debugging

```bash
# Enable debug logging
./gradlew assembleFossDebug -PdebugLogging=true

# Generate debug APK with symbols
./gradlew assembleFossDebug --info
```

## Troubleshooting

### Common Issues

#### Build Failures

1. **SDK not found**:
   ```bash
   # Check SDK path in local.properties
   cat local.properties
   ```

2. **Gradle sync issues**:
   ```bash
   # Clean and rebuild
   ./gradlew clean
   ./gradlew build
   ```

3. **Memory issues**:
   ```bash
   # Increase heap size in gradle.properties
   echo "org.gradle.jvmargs=-Xmx4g" >> gradle.properties
   ```

#### Runtime Issues

1. **App crashes on startup**:
   - Check device compatibility (Android 8.0+)
   - Verify all permissions are granted
   - Check logcat for error messages

2. **Music not playing**:
   - Verify internet connection
   - Check YouTube Music/Spotify login status
   - Clear app cache and data

### Getting Help

- Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for detailed solutions
- Create an issue on [GitHub](https://github.com/iad1tya/Echo-Music/issues)

## Additional Resources

- [Contributing Guide](CONTRIBUTING.md) - How to contribute to the project
- [Architecture Documentation](ARCHITECTURE.md) - Technical architecture details
- [API Documentation](API.md) - API integration details
- [Features Guide](FEATURES.md) - Detailed feature descriptions
- [Privacy Policy](PRIVACY_POLICY.md) - Data collection and usage
- [Security Policy](SECURITY.md) - Security guidelines and reporting

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0) - see the [LICENSE](LICENSE) file for details.