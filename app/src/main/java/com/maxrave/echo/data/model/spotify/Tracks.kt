package iad1tya.echo.music.data.model.spotify

data class Tracks(
    val href: String?,
    val items: List<Item?>?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: Any?,
    val total: Int?,
)