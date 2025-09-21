package iad1tya.echo.music.data.model.home.chart

import androidx.compose.runtime.Immutable

@Immutable
data class Artists(
    val itemArtists: ArrayList<ItemArtist>,
    val playlist: Any,
)