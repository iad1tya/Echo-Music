package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Geolocation(
    @SerializedName("altitude")
    val altitude: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
