package iad1tya.echo.music.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object AnalyticsHelper {
    
    private var firebaseAnalytics: FirebaseAnalytics? = null
    
    fun initialize(context: Context) {
        firebaseAnalytics = Firebase.analytics
    }
    
    fun logEvent(eventName: String, parameters: Map<String, Any>? = null) {
        val bundle = Bundle().apply {
            parameters?.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Long -> putLong(key, value)
                    is Int -> putLong(key, value.toLong())
                    is Double -> putDouble(key, value)
                    is Boolean -> putLong(key, if (value) 1L else 0L)
                }
            }
        }
        firebaseAnalytics?.logEvent(eventName, bundle)
    }
    
    // Music-related events
    fun logSongPlayed(songTitle: String, artistName: String, source: String = "unknown") {
        val params = mapOf(
            "song_title" to songTitle,
            "artist_name" to artistName,
            "source" to source
        )
        logEvent("song_played", params)
    }
    
    fun logSongPaused(songTitle: String, artistName: String) {
        val params = mapOf(
            "song_title" to songTitle,
            "artist_name" to artistName
        )
        logEvent("song_paused", params)
    }
    
    fun logSongSkipped(songTitle: String, artistName: String) {
        val params = mapOf(
            "song_title" to songTitle,
            "artist_name" to artistName
        )
        logEvent("song_skipped", params)
    }
    
    fun logPlaylistCreated(playlistName: String) {
        val params = mapOf("playlist_name" to playlistName)
        logEvent("playlist_created", params)
    }
    
    fun logPlaylistDeleted(playlistName: String) {
        val params = mapOf("playlist_name" to playlistName)
        logEvent("playlist_deleted", params)
    }
    
    fun logSongAddedToPlaylist(songTitle: String, playlistName: String) {
        val params = mapOf(
            "song_title" to songTitle,
            "playlist_name" to playlistName
        )
        logEvent("song_added_to_playlist", params)
    }
    
    fun logSongRemovedFromPlaylist(songTitle: String, playlistName: String) {
        val params = mapOf(
            "song_title" to songTitle,
            "playlist_name" to playlistName
        )
        logEvent("song_removed_from_playlist", params)
    }
    
    // Search events
    fun logSearchPerformed(query: String, resultCount: Int) {
        val params = mapOf(
            "search_query" to query,
            "result_count" to resultCount.toLong()
        )
        logEvent("search_performed", params)
    }
    
    // Settings events
    fun logSettingChanged(settingName: String, newValue: String) {
        val params = mapOf(
            "setting_name" to settingName,
            "new_value" to newValue
        )
        logEvent("setting_changed", params)
    }
    
    // App lifecycle events
    fun logAppOpened() {
        logEvent("app_opened")
    }
    
    fun logAppBackgrounded() {
        logEvent("app_backgrounded")
    }
    
    // Error events
    fun logError(errorType: String, errorMessage: String) {
        val params = mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage
        )
        logEvent("error_occurred", params)
    }
    
    // User engagement events
    fun logScreenViewed(screenName: String) {
        val params = mapOf("screen_name" to screenName)
        logEvent("screen_viewed", params)
    }
    
    fun logButtonClicked(buttonName: String, screenName: String) {
        val params = mapOf(
            "button_name" to buttonName,
            "screen_name" to screenName
        )
        logEvent("button_clicked", params)
    }
}