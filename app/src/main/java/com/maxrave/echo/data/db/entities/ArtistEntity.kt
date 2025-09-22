package iad1tya.echo.music.data.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import iad1tya.echo.music.data.type.ArtistType
import iad1tya.echo.music.data.type.RecentlyType
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Entity(tableName = "artist")
@Parcelize
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    val channelId: String,
    val name: String,
    val thumbnails: String?,
    val followed: Boolean = false,
    val inLibrary: LocalDateTime = LocalDateTime.now(),
) : RecentlyType,
    ArtistType,
    Parcelable {
    override fun objectType() = RecentlyType.Type.ARTIST
}