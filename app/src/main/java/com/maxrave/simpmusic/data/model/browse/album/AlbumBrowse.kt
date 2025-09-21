package iad1tya.echo.music.data.model.browse.album

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.browse.artist.ResultAlbum
import iad1tya.echo.music.data.model.searchResult.songs.Artist
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail

@Immutable
data class AlbumBrowse(
    val artists: List<Artist>,
    val audioPlaylistId: String,
    val description: String?,
    val duration: String?,
    val durationSeconds: Int,
    val thumbnails: List<Thumbnail>?,
    val title: String,
    val trackCount: Int,
    val tracks: List<Track>,
    val type: String,
    val year: String?,
    val otherVersion: List<ResultAlbum> = emptyList(),
)