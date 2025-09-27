# GitHub Setup Guide for Echo Music

This guide will help you set up Echo Music for development and deployment on GitHub.

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/Echo-Music.git
cd Echo-Music
```

### 2. Firebase Setup

#### Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project"
3. Follow the setup wizard
4. Enable Analytics and Crashlytics

#### Configure Android App
1. Click "Add app" → Android
2. Package name: `iad1tya.echo.music`
3. Download `google-services.json`
4. Place it in `app/` directory

#### For Debug Builds (FOSS variant)
1. Add another Android app with package name: `iad1tya.echo.music.dev`
2. Download the debug `google-services.json`
3. Place it in `app/src/foss/debug/` directory

### 3. Android SDK Setup

#### Create local.properties
```bash
cp local.properties.template local.properties
```

Edit `local.properties` and set your Android SDK path:
```properties
sdk.dir=/path/to/your/android/sdk
```

### 4. Build the Project

#### Debug Build (FOSS)
```bash
./gradlew assembleFossDebug
```

#### Release Build (Full)
```bash
./gradlew assembleFullRelease
```

## 📁 Project Structure

```
Echo-Music/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/                 # Main source code
│   │   ├── foss/                 # FOSS variant specific code
│   │   └── full/                 # Full variant specific code
│   ├── google-services.json      # Firebase config (add your own)
│   └── build.gradle.kts         # App build configuration
├── kotlinYtmusicScraper/        # YouTube Music scraper module
├── spotify/                     # Spotify integration module
├── aiService/                    # AI service module
├── ffmpeg-kit/                   # FFmpeg integration
├── gradle/                       # Gradle configuration
│   └── libs.versions.toml        # Dependency versions
├── fastlane/                     # Fastlane deployment config
├── assets/                       # App assets and screenshots
└── docs/                         # Documentation files
```

## 🔧 Configuration Files

### Required Files (Add These)
- `local.properties` - Android SDK path
- `app/google-services.json` - Firebase configuration
- `app/src/foss/debug/google-services.json` - Debug Firebase config

### Template Files (Already Included)
- `local.properties.template` - Template for local.properties
- `app/google-services.json.template` - Template for Firebase config

## 🏗️ Build Variants

### FOSS Variant
- **Package**: `iad1tya.echo.music.dev`
- **Features**: Core music functionality without premium features
- **Build**: `./gradlew assembleFossDebug`

### Full Variant
- **Package**: `iad1tya.echo.music`
- **Features**: All features including premium integrations
- **Build**: `./gradlew assembleFullRelease`

## 🚀 Deployment

### Using Fastlane
```bash
cd fastlane
fastlane android deploy
```

### Manual Deployment
1. Build release APK: `./gradlew assembleFullRelease`
2. Sign the APK with your keystore
3. Upload to Google Play Console

## 🔒 Security Notes

### Never Commit These Files
- `google-services.json` (contains API keys)
- `local.properties` (contains SDK paths)
- `*.keystore` (signing keys)
- `secrets.properties` (API secrets)

### Environment Variables
Set up environment variables for sensitive data:
```bash
export FIREBASE_API_KEY="your_api_key"
export YOUTUBE_API_KEY="your_youtube_key"
```

## 🐛 Troubleshooting

### Common Issues

1. **Build Failures**
   - Check Android SDK version (API 26+)
   - Verify Kotlin version compatibility
   - Clean and rebuild: `./gradlew clean build`

2. **Firebase Issues**
   - Verify `google-services.json` is in correct location
   - Check package name matches Firebase project
   - Ensure Firebase services are enabled

3. **Dependency Issues**
   - Sync project: `./gradlew --refresh-dependencies`
   - Check internet connection
   - Clear Gradle cache: `./gradlew clean`

## 📚 Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Happy coding! 🎵**
