package iad1tya.echo.music.data.model.browse.artist

import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail
import iad1tya.echo.music.data.type.HomeContentType

data class ResultPlaylist(
    val id: String,
    val author: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
) : HomeContentType