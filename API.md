# Echo Music - API Documentation

This document provides comprehensive information about the APIs and services integrated into Echo Music.

## Table of Contents

- [Overview](#overview)
- [YouTube Music API](#youtube-music-api)
- [Spotify Web API](#spotify-web-api)
- [AI Services](#ai-services)
- [Third-Party Services](#third-party-services)
- [Authentication](#authentication)
- [Rate Limiting](#rate-limiting)
- [Error Handling](#error-handling)
- [API Security](#api-security)

## Overview

Echo Music integrates with multiple APIs and services to provide a comprehensive music streaming experience:

- **YouTube Music**: Primary music streaming source
- **Spotify**: Secondary music source with enhanced features
- **AI Services**: For song recommendations and lyrics
- **Third-Party Services**: For lyrics, translations, and other features

## YouTube Music API

### Overview

Echo Music uses YouTube Music's internal API through reverse engineering and scraping techniques. This provides access to YouTube Music's vast catalog without official API access.

### Key Endpoints

#### Search
```kotlin
// Search for songs, artists, albums, playlists
GET /youtubei/v1/search
```

**Parameters:**
- `query`: Search query string
- `type`: Content type (song, artist, album, playlist)
- `limit`: Number of results to return

**Response:**
```json
{
  "contents": {
    "sectionListRenderer": {
      "contents": [
        {
          "musicShelfRenderer": {
            "contents": [
              {
                "musicResponsiveListItemRenderer": {
                  "flexColumns": [
                    {
                      "musicResponsiveListItemFlexColumnRenderer": {
                        "text": {
                          "runs": [
                            {
                              "text": "Song Title"
                            }
                          ]
                        }
                      }
                    }
                  ]
                }
              }
            ]
          }
        }
      ]
    }
  }
}
```

#### Get Song Details
```kotlin
// Get detailed information about a song
GET /youtubei/v1/player
```

**Parameters:**
- `videoId`: YouTube video ID
- `playbackContext`: Playback context information

#### Get Playlist
```kotlin
// Get playlist contents
GET /youtubei/v1/browse
```

**Parameters:**
- `browseId`: Playlist ID
- `params`: Additional parameters

### Authentication

YouTube Music requires authentication for full access:

```kotlin
class YouTubeAuthManager {
    suspend fun authenticate(email: String, password: String): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun logout()
}
```

### Rate Limiting

- **Search**: 100 requests per minute
- **Streaming**: No specific limits
- **Playlist Operations**: 50 requests per minute

## Spotify Web API

### Overview

Echo Music integrates with Spotify's official Web API for enhanced features like Canvas, lyrics, and high-quality audio.

### Key Endpoints

#### Search
```kotlin
// Search for tracks, artists, albums
GET https://api.spotify.com/v1/search
```

**Parameters:**
- `q`: Search query
- `type`: Content type (track, artist, album)
- `limit`: Number of results (1-50)
- `offset`: Pagination offset

**Response:**
```json
{
  "tracks": {
    "items": [
      {
        "id": "track_id",
        "name": "Track Name",
        "artists": [
          {
            "id": "artist_id",
            "name": "Artist Name"
          }
        ],
        "album": {
          "id": "album_id",
          "name": "Album Name",
          "images": [
            {
              "url": "image_url",
              "height": 640,
              "width": 640
            }
          ]
        },
        "preview_url": "preview_url",
        "external_urls": {
          "spotify": "spotify_url"
        }
      }
    ]
  }
}
```

#### Get Track Details
```kotlin
// Get detailed track information
GET https://api.spotify.com/v1/tracks/{id}
```

#### Get Audio Features
```kotlin
// Get audio analysis features
GET https://api.spotify.com/v1/audio-features/{id}
```

#### Get Canvas
```kotlin
// Get Spotify Canvas for track
GET https://spclient.wg.spotify.com/canvas/v1/canvases/{track_id}
```

### Authentication

Spotify uses OAuth 2.0 with PKCE:

```kotlin
class SpotifyAuthManager {
    suspend fun authenticate(): AuthResult
    suspend fun refreshToken(): AuthResult
    suspend fun getAccessToken(): String?
}
```

### Rate Limiting

- **General API**: 10,000 requests per hour
- **Search**: 1,000 requests per hour
- **Audio Features**: 100 requests per hour

## AI Services

### Overview

Echo Music integrates with various AI services for enhanced user experience:

- **OpenAI GPT**: Song recommendations and descriptions
- **Google Gemini**: Alternative AI provider
- **OpenRouter**: Multi-provider AI access

### OpenAI Integration

```kotlin
class OpenAIService {
    suspend fun getSongRecommendations(
        playlist: List<Song>,
        mood: String
    ): List<SongRecommendation>
    
    suspend fun generatePlaylistDescription(
        songs: List<Song>
    ): String
}
```

**API Endpoint:**
```
POST https://api.openai.com/v1/chat/completions
```

**Request:**
```json
{
  "model": "gpt-4",
  "messages": [
    {
      "role": "system",
      "content": "You are a music recommendation AI."
    },
    {
      "role": "user",
      "content": "Recommend songs similar to: [song list]"
    }
  ],
  "max_tokens": 500,
  "temperature": 0.7
}
```

### Google Gemini Integration

```kotlin
class GeminiService {
    suspend fun analyzeMusicMood(songs: List<Song>): MoodAnalysis
    suspend fun generateLyrics(songInfo: SongInfo): String
}
```

## Third-Party Services

### LRCLIB (Lyrics)

```kotlin
class LRCLibService {
    suspend fun getLyrics(
        trackName: String,
        artistName: String
    ): LyricsResult
}
```

**API Endpoint:**
```
GET https://lrclib.net/api/get
```

**Parameters:**
- `track_name`: Song title
- `artist_name`: Artist name
- `album_name`: Album name (optional)
- `duration`: Song duration (optional)

### SponsorBlock

```kotlin
class SponsorBlockService {
    suspend fun getSegments(videoId: String): List<SponsorSegment>
}
```

**API Endpoint:**
```
GET https://sponsor.ajay.app/api/skipSegments
```

**Parameters:**
- `videoID`: YouTube video ID
- `categories`: Array of segment categories

### Translation Services

```kotlin
class TranslationService {
    suspend fun translateLyrics(
        lyrics: String,
        targetLanguage: String
    ): String
}
```

## Authentication

### YouTube Music Authentication

```kotlin
data class YouTubeAuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val tokenType: String
)

class YouTubeAuthManager {
    suspend fun authenticate(
        email: String,
        password: String
    ): YouTubeAuthResult
    
    suspend fun refreshToken(): YouTubeAuthResult
    
    fun isAuthenticated(): Boolean
    
    suspend fun logout()
}
```

### Spotify Authentication

```kotlin
data class SpotifyAuthResult(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String
)

class SpotifyAuthManager {
    suspend fun authenticate(): SpotifyAuthResult
    
    suspend fun refreshToken(): SpotifyAuthResult
    
    fun isAuthenticated(): Boolean
    
    suspend fun logout()
}
```

## Rate Limiting

### Implementation

```kotlin
class RateLimiter(
    private val maxRequests: Int,
    private val timeWindow: Duration
) {
    private val requests = mutableListOf<Instant>()
    
    suspend fun acquire(): Boolean {
        val now = Instant.now()
        val windowStart = now.minus(timeWindow)
        
        // Remove old requests
        requests.removeAll { it.isBefore(windowStart) }
        
        return if (requests.size < maxRequests) {
            requests.add(now)
            true
        } else {
            false
        }
    }
}
```

### Rate Limits by Service

| Service | Limit | Window |
|---------|-------|--------|
| YouTube Music | 100 requests | 1 minute |
| Spotify | 10,000 requests | 1 hour |
| OpenAI | 3,500 requests | 1 minute |
| LRCLIB | 1,000 requests | 1 hour |
| SponsorBlock | 10,000 requests | 1 hour |

## Error Handling

### Error Types

```kotlin
sealed class ApiError : Exception() {
    object NetworkError : ApiError()
    object AuthenticationError : ApiError()
    object RateLimitError : ApiError()
    object ServerError : ApiError()
    data class ClientError(val code: Int, val message: String) : ApiError()
}
```

### Error Handling Strategy

```kotlin
class ApiErrorHandler {
    suspend fun <T> handleApiCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.failure(ApiError.AuthenticationError)
                429 -> Result.failure(ApiError.RateLimitError)
                500..599 -> Result.failure(ApiError.ServerError)
                else -> Result.failure(ApiError.ClientError(e.code(), e.message()))
            }
        } catch (e: IOException) {
            Result.failure(ApiError.NetworkError)
        }
    }
}
```

### Retry Logic

```kotlin
class RetryManager {
    suspend fun <T> retryWithBackoff(
        maxRetries: Int = 3,
        initialDelay: Duration = Duration.ofSeconds(1),
        apiCall: suspend () -> T
    ): T {
        var delay = initialDelay
        
        repeat(maxRetries) { attempt ->
            try {
                return apiCall()
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) throw e
                
                delay(delay.toMillis())
                delay = delay.multipliedBy(2) // Exponential backoff
            }
        }
        
        throw IllegalStateException("Should not reach here")
    }
}
```

## API Security

### API Key Management

```kotlin
class ApiKeyManager {
    private val encryptedPrefs: EncryptedSharedPreferences
    
    fun storeApiKey(service: String, key: String) {
        encryptedPrefs.edit()
            .putString("api_key_$service", key)
            .apply()
    }
    
    fun getApiKey(service: String): String? {
        return encryptedPrefs.getString("api_key_$service", null)
    }
}
```

### Request Signing

```kotlin
class RequestSigner {
    fun signRequest(
        url: String,
        method: String,
        headers: Map<String, String>,
        body: String?
    ): Map<String, String> {
        // Implement request signing logic
        return headers + mapOf("Authorization" to generateSignature())
    }
}
```

### Certificate Pinning

```kotlin
class CertificatePinner {
    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .certificatePinner(
                CertificatePinner.Builder()
                    .add("api.spotify.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                    .add("api.openai.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
                    .build()
            )
            .build()
    }
}
```

## API Monitoring

### Metrics Collection

```kotlin
class ApiMetricsCollector {
    fun recordApiCall(
        service: String,
        endpoint: String,
        duration: Duration,
        success: Boolean
    ) {
        // Record metrics for monitoring
    }
    
    fun recordRateLimit(service: String, endpoint: String) {
        // Record rate limit events
    }
}
```

### Health Checks

```kotlin
class ApiHealthChecker {
    suspend fun checkServiceHealth(service: String): HealthStatus {
        return try {
            when (service) {
                "youtube" -> checkYouTubeHealth()
                "spotify" -> checkSpotifyHealth()
                "openai" -> checkOpenAIHealth()
                else -> HealthStatus.Unknown
            }
        } catch (e: Exception) {
            HealthStatus.Unhealthy(e.message ?: "Unknown error")
        }
    }
}
```

---

This API documentation provides a comprehensive overview of all the APIs and services integrated into Echo Music. For implementation details, refer to the source code in the respective modules.
