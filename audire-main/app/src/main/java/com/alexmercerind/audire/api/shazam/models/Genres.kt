package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("primary")
    val primary: String?
)
