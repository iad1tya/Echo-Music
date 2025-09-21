package iad1tya.echo.kotlinytmusicscraper.pages

import iad1tya.echo.kotlinytmusicscraper.models.YTItem

data class BrowseResult(
    val title: String?,
    val items: List<Item>,
) {
    data class Item(
        val title: String?,
        val items: List<YTItem>,
    )
}