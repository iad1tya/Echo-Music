package iad1tya.echo.music.data.model.browse.playlist

import androidx.compose.runtime.Immutable
import iad1tya.echo.kotlinytmusicscraper.models.WatchEndpoint
import iad1tya.echo.music.data.model.browse.album.Track
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail

@Immutable
data class PlaylistBrowse(
    val author: Author,
    val description: String?,
    val duration: String,
    val durationSeconds: Int,
    val id: String,
    val privacy: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val trackCount: Int,
    val tracks: List<Track>,
    val year: String,
    val shuffleEndpoint: WatchEndpoint? = null,
    val radioEndpoint: WatchEndpoint? = null,
)