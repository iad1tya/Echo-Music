package iad1tya.echo.music.data.model.home.chart

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail

@Immutable
data class ItemArtist(
    val browseId: String,
    val rank: String,
    val subscribers: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val trend: String,
)