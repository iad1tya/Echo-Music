package iad1tya.echo.music.data.model.explore.mood.genre

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail
import iad1tya.echo.music.data.type.HomeContentType

@Immutable
data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType