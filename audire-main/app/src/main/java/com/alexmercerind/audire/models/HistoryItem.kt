package com.alexmercerind.audire.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "history_item")
data class HistoryItem(
    @SerializedName("id") @PrimaryKey(autoGenerate = true) val id: Int?,
    @SerializedName("timestamp") @ColumnInfo(name = "timestamp") val timestamp: Long,
    @SerializedName("title") @ColumnInfo(name = "title") val title: String,
    @SerializedName("artists") @ColumnInfo(name = "artists") val artists: String,
    @SerializedName("cover") @ColumnInfo(name = "cover") val cover: String,
    @SerializedName("album") @ColumnInfo(name = "album") val album: String?,
    @SerializedName("label") @ColumnInfo(name = "label") val label: String?,
    @SerializedName("year") @ColumnInfo(name = "year") val year: String?,
    @SerializedName("lyrics") @ColumnInfo(name = "lyrics") val lyrics: String?,
    @SerializedName("liked") @ColumnInfo(name = "liked", defaultValue = "0") val liked: Boolean
) : Serializable
