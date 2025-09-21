package iad1tya.echo.music.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import iad1tya.echo.music.data.model.metadata.Line

@Entity(tableName = "translated_lyrics")
data class TranslatedLyricsEntity(
    @PrimaryKey(autoGenerate = false) val videoId: String,
    val language: String = "en",
    val error: Boolean,
    val lines: List<Line>?,
    val syncType: String?,
)