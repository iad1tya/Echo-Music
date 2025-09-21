package iad1tya.echo.music.data.model.searchResult.songs

import kotlinx.serialization.Serializable

@Serializable
data class Thumbnail(
    val height: Int,
    val url: String,
    val width: Int,
)