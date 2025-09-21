package iad1tya.echo.music.data.model.explore.mood.genre

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.searchResult.songs.Artist

@Immutable
data class ItemsSong(
    val title: String,
    val artist: List<Artist>?,
    val videoId: String,
)