package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Images(
    @SerializedName("background")
    val background: String?,
    @SerializedName("coverart")
    val coverart: String?,
    @SerializedName("coverarthq")
    val coverarthq: String?,
)
