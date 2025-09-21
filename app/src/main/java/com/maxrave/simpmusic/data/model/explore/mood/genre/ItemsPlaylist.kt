package iad1tya.echo.music.data.model.explore.mood.genre

import androidx.compose.runtime.Immutable

@Immutable
data class ItemsPlaylist(
    val contents: List<Content>,
    val header: String,
    val type: String,
)