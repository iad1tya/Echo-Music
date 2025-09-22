package iad1tya.echo.music.data.model.home

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.searchResult.songs.Album
import iad1tya.echo.music.data.model.searchResult.songs.Artist
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail
import iad1tya.echo.music.data.type.HomeContentType

@Immutable
data class Content(
    val album: Album?,
    val artists: List<Artist>?,
    val description: String?,
    val isExplicit: Boolean?,
    val playlistId: String?,
    val browseId: String?,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val videoId: String?,
    val views: String?,
    val durationSeconds: Int? = null,
    val radio: String? = null,
) : HomeContentType