# Echo Music - GitHub Setup Guide

## 🔧 Initial Setup

### 1. Clone the Repository
```bash
git clone https://github.com/iad1tya/Echo-Music.git
cd Echo-Music
```

### 2. Setup Local Configuration
```bash
# Copy local properties template
cp local.properties.template local.properties
# Edit local.properties with your Android SDK path
```

### 3. Setup Firebase (Optional)
```bash
# Copy Firebase configuration template
cp app/google-services.json.template app/google-services.json
# Replace with your actual Firebase configuration
# See app/FIREBASE_SETUP.md for detailed instructions
```

### 4. Setup Release Signing (For Production)
```bash
# Copy keystore template
cp keystore.properties.template keystore.properties
# Generate keystore and update passwords
# See keystore.properties.template for instructions
```

### 5. Open in Android Studio
- Launch Android Studio
- Select "Open an existing project"
- Navigate to the Echo-Music directory
- Click "OK"

### 6. Sync Project
- Android Studio will automatically sync the project
- Wait for the sync to complete
- Resolve any dependency issues if prompted

## 🏗️ Building the App

### Debug Build
```bash
# Build debug APK
./gradlew assembleFossDebug

# Install on connected device/emulator
./gradlew installFossDebug
```

### Release Build
```bash
# Build release APK (requires keystore setup)
./gradlew assembleFossRelease

# Build release AAB (for Play Store)
./gradlew bundleFossRelease
```

## 🧪 Testing

### Unit Tests
```bash
# Run unit tests
./gradlew testFossDebugUnitTest
```

### Integration Tests
```bash
# Run integration tests
./gradlew connectedAndroidTest
```

## 📁 Project Structure

```
Echo-Music/
├── app/                    # Main application module
├── kotlinYtmusicScraper/   # YouTube Music integration
├── spotify/               # Spotify integration
├── aiService/             # AI-powered features
├── ffmpeg-kit/            # Audio/video processing
├── assets/                # App assets and screenshots
├── search/                # Search category images
└── docs/                  # Documentation files
```

## 🔒 Security Notes

- **Never commit sensitive files**: `google-services.json`, `keystore.properties`, `local.properties`
- **Use template files**: All sensitive configuration has template files
- **Follow setup guides**: Each template file contains detailed setup instructions

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/iad1tya/Echo-Music/issues)
- **Discussions**: [GitHub Discussions](https://github.com/iad1tya/Echo-Music/discussions)
- **Discord**: [Discord Community](https://discord.gg/eNFNHaWN97)
