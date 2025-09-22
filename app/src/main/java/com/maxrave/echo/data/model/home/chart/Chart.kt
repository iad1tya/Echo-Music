package iad1tya.echo.music.data.model.home.chart

import androidx.compose.runtime.Immutable

@Immutable
data class Chart(
    val artists: Artists,
    val countries: Countries?,
    val listChartItem: List<ChartItemPlaylist>,
)