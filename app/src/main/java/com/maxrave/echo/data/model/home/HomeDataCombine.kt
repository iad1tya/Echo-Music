package iad1tya.echo.music.data.model.home

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.explore.mood.Mood
import iad1tya.echo.music.data.model.home.chart.Chart
import iad1tya.echo.music.utils.Resource

@Immutable
data class HomeDataCombine(
    val home: Resource<ArrayList<HomeItem>>,
    val mood: Resource<Mood>,
    val chart: Resource<Chart>,
    val newRelease: Resource<ArrayList<HomeItem>>,
)