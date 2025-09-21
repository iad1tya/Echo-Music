package iad1tya.echo.music.data.model.explore.mood

import androidx.compose.runtime.Immutable

@Immutable
data class Mood(
    val genres: ArrayList<Genre>,
    val moodsMoments: ArrayList<MoodsMoment>,
)