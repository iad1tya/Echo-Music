package com.alexmercerind.audire.mappers

import com.alexmercerind.audire.api.shazam.models.ShazamResponse
import com.alexmercerind.audire.models.Music

fun ShazamResponse.toMusic() = track?.let {
    Music(
        track.title!!,
        track.subtitle!!,
        track.images?.coverarthq!!,
        track.sections
            ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
            ?.metadata
            ?.firstOrNull { metadata -> metadata.title?.uppercase() == "ALBUM" }
            ?.text,
        track.sections
            ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
            ?.metadata
            ?.firstOrNull { metadata -> metadata.title?.uppercase() == "LABEL" }
            ?.text,
        track.sections
            ?.firstOrNull { section -> section.type?.uppercase() == "SONG" }
            ?.metadata
            ?.firstOrNull { metadata -> metadata.title?.uppercase() == "RELEASED" }
            ?.text,
        track.sections
            ?.firstOrNull { section -> section.type?.uppercase() == "LYRICS" }
            ?.text
            ?.joinToString("\n")
    )
}
