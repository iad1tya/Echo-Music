package iad1tya.echo.music.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import iad1tya.echo.music.common.DownloadState
import iad1tya.echo.music.data.type.PlaylistType
import iad1tya.echo.music.data.type.RecentlyType
import java.time.LocalDateTime

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",
    val author: String? = "",
    val description: String = "",
    val duration: String = "",
    val durationSeconds: Int = 0,
    val privacy: String = "PRIVATE",
    val thumbnails: String = "",
    val title: String,
    val trackCount: Int = 0,
    val tracks: List<String>? = null,
    val year: String? = null,
    val liked: Boolean = false,
    val inLibrary: LocalDateTime = LocalDateTime.now(),
    val downloadState: Int = DownloadState.STATE_NOT_DOWNLOADED,
) : PlaylistType,
    RecentlyType {
    override fun playlistType(): PlaylistType.Type =
        if (id.startsWith("RDEM") || id.startsWith("RDAMVM") || id.startsWith("RDAT")) {
            PlaylistType.Type.RADIO
        } else {
            PlaylistType.Type.YOUTUBE_PLAYLIST
        }

    override fun objectType(): RecentlyType.Type = RecentlyType.Type.PLAYLIST
}