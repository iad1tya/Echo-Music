package iad1tya.echo.music.data.model.home

import androidx.compose.runtime.Immutable
import iad1tya.echo.music.data.model.explore.mood.Mood
import iad1tya.echo.music.data.model.home.chart.Chart
import iad1tya.echo.music.utils.Resource

@Immutable
data class HomeResponse(
    val homeItem: Resource<ArrayList<HomeItem>>,
    val exploreMood: Resource<Mood>,
    val exploreChart: Resource<Chart>,
)