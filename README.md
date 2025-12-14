> [!CAUTION]
> Support has been ended - but still I will be fixing some of the bugs if found, to keep this app alive.

<div align="center">
  <img src="assets/Echo_github.png" alt="Echo Music Logo" width="150"/>
</div>

<div align="center">
  <h1>Echo Music</h1>
  <p><strong>A modern music streaming app with adfree experience, synced lyrics, and offline playback.</strong></p>
</div>

<div align="center">
  <a href="https://echomusic.fun/download">
    <img src="assets/download.png" alt="Direct Download" width="220"/>
  </a>
  <br>
    <a href="https://github.com/iad1tya/Echo-Music/releases" style="text-decoration: none;">
    <img src="assets/github.png" alt="Github Releases" width="170"/>
  </a>
    <br>
  <a href="https://echomusic.fun/obtainium">
    <img src="assets/obtainium.png" alt="Add to Obtainium" width="215"/>
  </a>
</div>

---

## Screenshots

<div align="center">
  <img src="assets/Screenshots/sc_1.png" alt="Home Screen" width="200"/>
  <img src="assets/Screenshots/sc_2.png" alt="Music Player" width="200"/>
  <img src="assets/Screenshots/sc_3.png" alt="Playlist Management" width="200"/>
  <img src="assets/Screenshots/sc_4.png" alt="Settings" width="200"/>
</div>

---

## Key Features

### Music Streaming

* **YouTube Music Integration:** Stream music seamlessly from YouTube Music.
* **Video/Audio Playback:** Switch effortlessly between video and audio modes.
* **Background Playback:** Continue listening while using other apps.
* **Offline Playback:** Download songs for offline listening.

### Discovery and Search

* **Smart Search:** Powerful search across YouTube Music.
* **Best Recommendations:** Personalized song suggestions based on your listening habits.
* **Browsing:** Explore curated categories such as Home, Charts, Podcasts, Moods, and Genres.
* **Recently Played:** Access your recent tracks instantly.

### Advanced Features

* **Synced Lyrics:** Real-time lyric display with translation support.
* **AI Lyrics Translation:** Translate lyrics with the help of AI, powered by OpenRouter.
* **Playlist Management:** Create, edit, sync, and organize playlists with intuitive long-press actions.
* **Sleep Timer:** Automatically stop playback after a set duration.
* **Widgets:** Quick access from your home screen.
* **DLNA/UPnP Support:** Stream music to compatible network devices (Smart TVs, speakers, etc.). [Learn more](DLNA_SETUP.md)
* **Chromecast:** Cast to Chromecast-enabled devices.

---

## Installation

### Option 1: Download Pre-Built APK

1. Visit the [Releases Page](https://github.com/iad1tya/Echo-Music/releases/latest).
2. Download the latest APK file.
3. Install it on your Android device.

### Option 2: Build from Source

1. **Clone the Repository**

   ```bash
   git clone https://github.com/iad1tya/Echo-Music.git
   cd Echo-Music
   ```

2. **Configure Android SDK**

   ```bash
   cp local.properties.example local.properties
   ```

   Update `local.properties` with your Android SDK path:

   ```properties
   sdk.dir=/path/to/your/android/sdk
   ```

3. **Configure Firebase (Required)**

   Firebase Analytics and Crashlytics are required for building the app.
   
   ```bash
   cp app/google-services.json.template app/google-services.json
   ```

   Edit `app/google-services.json` and replace placeholders with your Firebase project credentials.
   
   📖 **Detailed instructions:** See [FIREBASE_SETUP.md](FIREBASE_SETUP.md)

4. **Build the Project**

   ```bash
   ./gradlew assembleFossDebug
   ```

---

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.
See the [LICENSE](LICENSE) file for details.

---

## Privacy

Echo Music is designed with user privacy in mind:
Only analytics data is collected to help improve your experience. Crash reports may also be gathered for the same purpose. No personal information is collected.

---

## Documentation

* **Report Issues or Request Features:** [GitHub Issues](https://github.com/iad1tya/Echo-Music/issues)
* **Additional Documentation:**
  [CONTRIBUTING.md](CONTRIBUTING.md) | [PRIVACY_POLICY.md](PRIVACY_POLICY.md) | [SECURITY.md](SECURITY.md)

---

## Supporter
<div align="center">
  <p><strong>Thank you to the supporters who contributed a small amount and believed in this project — your support keeps Echo Music going.</strong></p>
  <img src="assets/Supporter/EM.png" alt="Emagik" width="90"/>
  <img src="assets/Supporter/AdamPoy.png" alt="AdamPoy" width="90"/>
  <br>
  <p><strong>Emagik</strong> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>AdamPoy</strong></p>
</div>

---

## Community
<div align="center">
  <a href="https://discord.com/invite/eNFNHaWN97">
    <img src="assets/discord.png" alt="Discord" width="150"/>
  </a>
  <a href="https://t.me/EchoMusicApp">
    <img src="assets/telegram.png" alt="UPI" width="140"/>
  </a>
</div>

---

## Support the Project
<div align="center">
  <a href="https://buymeacoffee.com/iad1tya">
    <img src="assets/bmac.png" alt="Buy Me a Coffee" width="150"/>
  </a>
  <a href="https://intradeus.github.io/http-protocol-redirector/?r=upi://pay?pa=iad1tya@upi&pn=Aditya%20Yadav&am=&tn=Thank%20You%20so%20much%20for%20this%20support">
    <img src="assets/upi.svg" alt="UPI" width="110"/>
  </a>
</div>

### Cryptocurrency
<div align="center">
  <img src="assets/Bitcoin.jpeg" alt="Bitcoin QR" width="150"/>
  <p><strong>Bitcoin:</strong> <code>bc1qcvyr7eekha8uytmffcvgzf4h7xy7shqzke35fy</code></p>
  <br>
  <img src="assets/Ethereum.jpeg" alt="Ethereum QR" width="150"/>
  <p><strong>Ethereum:</strong> <code>0x51bc91022E2dCef9974D5db2A0e22d57B360e700</code></p>
  <br>
  <img src="assets/Solana.jpeg" alt="Solana QR" width="150"/>
  <p><strong>Solana:</strong> <code>9wjca3EQnEiqzqgy7N5iqS1JGXJiknMQv6zHgL96t94S</code></p>
</div>

---

<div align="center">
    <img src="assets/LMEB.gif"/>
  </a>
</div>
