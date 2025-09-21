package iad1tya.echo.kotlinytmusicscraper.pages

import iad1tya.echo.kotlinytmusicscraper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)