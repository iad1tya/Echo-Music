# Echo Music - Development Setup

## 🚀 Quick Start

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or later
- Android SDK API level 26+ (Android 8.0+)
- Kotlin 2.2.10+
- JDK 17+
- Git

### Setup Steps

1. **Clone Repository**
   ```bash
   git clone https://github.com/iad1tya/Echo-Music.git
   cd Echo-Music
   ```

2. **Configure Local Environment**
   ```bash
   cp local.properties.template local.properties
   # Edit local.properties with your Android SDK path
   ```

3. **Setup Firebase (Optional)**
   ```bash
   cp app/google-services.json.template app/google-services.json
   # Replace with your Firebase configuration
   ```

4. **Open in Android Studio**
   - File → Open → Select Echo-Music directory
   - Wait for sync to complete

5. **Build & Run**
   ```bash
   ./gradlew assembleFossDebug
   ./gradlew installFossDebug
   ```

## 🔧 Configuration Files

| File | Purpose | Required |
|------|---------|----------|
| `local.properties` | Android SDK path | Yes |
| `google-services.json` | Firebase configuration | Optional |
| `keystore.properties` | Release signing | For production |

## 🏗️ Build Variants

- **fossDebug**: Open source debug build
- **fossRelease**: Open source release build
- **fullDebug**: Full featured debug build
- **fullRelease**: Full featured release build

## 🧪 Testing

```bash
# Unit tests
./gradlew testFossDebugUnitTest

# Integration tests
./gradlew connectedAndroidTest

# Lint checks
./gradlew lintFossDebug
```

## 📱 Features

- 🎵 YouTube Music integration
- 🎧 Spotify integration
- 🤖 AI-powered song suggestions
- 📝 Synced lyrics
- 🎛️ Bit-perfect USB DAC support
- 📱 Android Auto support
- 💾 Offline playback
- 🎨 Material Design 3 UI

## 🔒 Security

- All sensitive files are templated
- Never commit actual credentials
- Follow template setup instructions
- Use environment variables for CI/CD

## 🤝 Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## 📄 License

MIT License - see [LICENSE](LICENSE) for details.
