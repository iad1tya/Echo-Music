# Echo Music 🎵

A modern, feature-rich music streaming app for Android that integrates with YouTube Music and Spotify, offering ad-free music streaming with advanced features like AI song suggestions, synced lyrics, and offline playback.

![Echo Music Logo](/Echo_github.png)

## ✨ Features

### 🎶 Music Streaming
- **YouTube Music Integration**: Stream music from YouTube Music and YouTube for free, without ads
- **Spotify Integration**: Access Spotify's vast music library with Canvas support
- **Background Playback**: Continue listening while using other apps
- **High-Quality Audio**: Support for various audio formats and quality settings

### 🎨 User Experience
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Dark Theme**: Beautiful dark mode interface
- **Multi-language Support**: Available in 20+ languages
- **Customizable Interface**: Personalized themes and layouts

### 🔍 Discovery & Search
- **Smart Search**: Search everything on YouTube and Spotify
- **AI Song Suggestions**: Get personalized music recommendations
- **Browse Categories**: Explore Home, Charts, Podcasts, Moods & Genres
- **Trending Content**: Stay updated with the latest music trends

### 📱 Advanced Features
- **Synced Lyrics**: Real-time lyrics from multiple sources (Echo Lyrics, LRCLIB, Spotify, YouTube Transcript)
- **AI Lyrics Translation**: Translate lyrics to your preferred language (BETA)
- **Offline Playback**: Download music for offline listening
- **Playlist Management**: Create, edit, and sync custom playlists
- **Sleep Timer**: Set automatic sleep timer for bedtime listening
- **Android Auto Support**: Seamless integration with Android Auto

### 🛠️ Technical Features
- **SponsorBlock Integration**: Skip sponsored content automatically
- **Return YouTube Dislike**: See dislike counts on YouTube content
- **1080p Video Support**: High-quality video playback with subtitles
- **Multi-Account Support**: Support for multiple YouTube accounts
- **Data Analytics**: Track your listening habits and preferences

## 🏗️ Architecture

Echo Music is built using modern Android development practices:

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Koin
- **Database**: Room
- **Networking**: Ktor + OkHttp
- **Image Loading**: Coil
- **Navigation**: Navigation Component
- **Background Tasks**: WorkManager

### 📦 Modules

- **app**: Main application module
- **kotlinYtmusicScraper**: YouTube Music API integration
- **spotify**: Spotify API integration
- **aiService**: AI-powered features and recommendations
- **lyricsService**: Lyrics fetching and synchronization
- **ffmpeg-kit**: Audio/video processing

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17 or later
- Android SDK 26 or later
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/iad1tya/Echo-Music.git
   cd Echo-Music
   ```

2. **Set up Firebase (Optional)**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
   - Add an Android app with package name `iad1tya.echo.music`
   - Download `google-services.json` and place it in the `app/` directory
   - For debug builds, add another app with package name `iad1tya.echo.music.dev`

3. **Configure local properties**
   ```bash
   cp local.properties.template local.properties
   ```
   Edit `local.properties` and add your Android SDK path:
   ```properties
   sdk.dir=/path/to/your/Android/sdk
   ```

4. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

### 🔧 Configuration

#### Firebase Setup (Optional)
1. Follow the instructions in [FIREBASE_SETUP.md](FIREBASE_SETUP.md)
2. Replace `app/google-services.json.template` with your actual `google-services.json`

#### Sentry Setup (Optional)
1. Create a Sentry project
2. Add your Sentry DSN to `local.properties`:
   ```properties
   SENTRY_DSN=your_sentry_dsn_here
   SENTRY_AUTH_TOKEN=your_sentry_auth_token_here
   ```

## 🏃‍♂️ Running the App

### Debug Build
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### FOSS Build (No Google Services)
```bash
./gradlew assembleFossDebug
```

## 📱 Screenshots

<div align="center">
  <img src="asset/screenshot/1.jpg" width="200" alt="Home Screen"/>
  <img src="asset/screenshot/2.png" width="200" alt="Player Screen"/>
  <img src="asset/screenshot/3.png" width="200" alt="Search Screen"/>
  <img src="asset/screenshot/4.png" width="200" alt="Playlist Screen"/>
</div>

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Run tests: `./gradlew test`
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔒 Privacy

Your privacy is important to us. Please read our [Privacy Policy](PRIVACY_POLICY.md) to understand how we collect, use, and protect your information.

## 🐛 Bug Reports & Feature Requests

- **Bug Reports**: [Create an issue](https://github.com/iad1tya/Echo-Music/issues/new?template=bug_report.md)
- **Feature Requests**: [Create an issue](https://github.com/iad1tya/Echo-Music/issues/new?template=feature_request.md)
- **General Discussion**: [GitHub Discussions](https://github.com/iad1tya/Echo-Music/discussions)

## 🙏 Acknowledgments

- **YouTube Music** for providing the music streaming platform
- **Spotify** for their API and music library
- **NewPipe** for YouTube extraction capabilities
- **ExoPlayer** for media playback
- **Jetpack Compose** team for the modern UI framework
- **All contributors** who help make Echo Music better

## 📊 Project Status

![GitHub stars](https://img.shields.io/github/stars/iad1tya/Echo-Music?style=social)
![GitHub forks](https://img.shields.io/github/forks/iad1tya/Echo-Music?style=social)
![GitHub issues](https://img.shields.io/github/issues/iad1tya/Echo-Music)
![GitHub pull requests](https://img.shields.io/github/issues-pr/iad1tya/Echo-Music)
![License](https://img.shields.io/github/license/iad1tya/Echo-Music)

## 🔗 Links

- **Website**: [Coming Soon]
- **Download**: [GitHub Releases](https://github.com/iad1tya/Echo-Music/releases)
- **Documentation**: [Wiki](https://github.com/iad1tya/Echo-Music/wiki)
- **Changelog**: [CHANGELOG.md](CHANGELOG.md)

---

<div align="center">
  <p>Made with ❤️ by iad1tya</p>
  <p>⭐ Star this repository if you like it!</p>
</div>
