package iad1tya.echo.kotlinytmusicscraper.pages

import iad1tya.echo.kotlinytmusicscraper.models.PlaylistItem
import iad1tya.echo.kotlinytmusicscraper.models.VideoItem

data class ExplorePage(
    val released: List<PlaylistItem>,
    val musicVideo: List<VideoItem>,
)