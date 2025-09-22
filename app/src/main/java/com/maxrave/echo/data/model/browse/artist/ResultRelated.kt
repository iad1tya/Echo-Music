package iad1tya.echo.music.data.model.browse.artist

import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail

data class ResultRelated(
    val browseId: String,
    val subscribers: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
)