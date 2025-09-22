package iad1tya.echo.music.data.model.home.chart

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.browse.artist.ResultPlaylist

@Immutable
data class ChartItemPlaylist(
    val title: String,
    val playlists: List<ResultPlaylist>,
)