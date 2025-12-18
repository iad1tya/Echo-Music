package com.alexmercerind.audire.api.shazam.models


import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("genres")
    val genres: Genres?,
    @SerializedName("images")
    val images: Images?,
    @SerializedName("sections")
    val sections: List<Section>?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("title")
    val title: String?,
)
