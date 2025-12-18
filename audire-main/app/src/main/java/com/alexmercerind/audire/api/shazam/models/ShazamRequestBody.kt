package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class ShazamRequestBody(
    @SerializedName("geolocation")
    val geolocation: Geolocation,
    @SerializedName("signature")
    val signature: Signature,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("timezone")
    val timezone: String
)
