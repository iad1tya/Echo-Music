package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Section(
    @SerializedName("metadata")
    val metadata: List<Metadata>?,
    @SerializedName("text")
    val text: List<String>?,
    @SerializedName("type")
    val type: String?,
)
