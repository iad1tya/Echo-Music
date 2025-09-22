package iad1tya.echo.music.data.model.mediaService

import android.graphics.Bitmap
import iad1tya.echo.music.data.model.searchResult.songs.Album
import iad1tya.echo.music.data.model.searchResult.songs.Artist
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail

data class Song(
    val title: String?,
    val artists: List<Artist>?,
    val duration: Long,
    val lyrics: Any,
    val album: Album,
    val videoId: String,
    val thumbnail: Thumbnail?,
    val thumbnailBitmap: Bitmap?,
    val isLocal: Boolean,
)