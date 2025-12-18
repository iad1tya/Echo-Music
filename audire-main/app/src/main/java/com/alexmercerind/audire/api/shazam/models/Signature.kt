package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Signature(
    @SerializedName("samplems")
    val samplems: Int,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("uri")
    val uri: String
)