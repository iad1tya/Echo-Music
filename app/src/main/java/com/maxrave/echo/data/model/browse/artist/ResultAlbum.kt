package iad1tya.echo.music.data.model.browse.artist

import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail
import iad1tya.echo.music.data.type.HomeContentType

data class ResultAlbum(
    val browseId: String,
    val isExplicit: Boolean,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType