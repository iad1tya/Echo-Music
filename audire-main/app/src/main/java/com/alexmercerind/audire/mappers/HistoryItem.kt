package com.alexmercerind.audire.mappers

import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.models.Music

fun HistoryItem.toMusic() = Music(
    title,
    artists,
    cover,
    album,
    label,
    year,
    lyrics
)
