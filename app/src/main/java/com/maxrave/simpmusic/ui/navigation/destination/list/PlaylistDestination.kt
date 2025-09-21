package iad1tya.echo.music.ui.navigation.destination.list

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDestination(
    val playlistId: String,
    val isYourYouTubePlaylist: Boolean = false,
)