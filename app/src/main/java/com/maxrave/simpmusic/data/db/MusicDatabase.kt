package iad1tya.echo.music.data.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import iad1tya.echo.music.data.db.entities.AlbumEntity
import iad1tya.echo.music.data.db.entities.ArtistEntity
import iad1tya.echo.music.data.db.entities.EpisodeEntity
import iad1tya.echo.music.data.db.entities.FollowedArtistSingleAndAlbum
import iad1tya.echo.music.data.db.entities.GoogleAccountEntity
import iad1tya.echo.music.data.db.entities.LocalPlaylistEntity
import iad1tya.echo.music.data.db.entities.LyricsEntity
import iad1tya.echo.music.data.db.entities.NewFormatEntity
import iad1tya.echo.music.data.db.entities.NotificationEntity
import iad1tya.echo.music.data.db.entities.PairSongLocalPlaylist
import iad1tya.echo.music.data.db.entities.PlaylistEntity
import iad1tya.echo.music.data.db.entities.PodcastsEntity
import iad1tya.echo.music.data.db.entities.QueueEntity
import iad1tya.echo.music.data.db.entities.SearchHistory
import iad1tya.echo.music.data.db.entities.SetVideoIdEntity
import iad1tya.echo.music.data.db.entities.SongEntity
import iad1tya.echo.music.data.db.entities.SongInfoEntity
import iad1tya.echo.music.data.db.entities.TranslatedLyricsEntity

@Database(
    entities = [
        NewFormatEntity::class, SongInfoEntity::class, SearchHistory::class, SongEntity::class, ArtistEntity::class,
        AlbumEntity::class, PlaylistEntity::class, LocalPlaylistEntity::class, LyricsEntity::class, QueueEntity::class,
        SetVideoIdEntity::class, PairSongLocalPlaylist::class, GoogleAccountEntity::class, FollowedArtistSingleAndAlbum::class,
        NotificationEntity::class, TranslatedLyricsEntity::class, PodcastsEntity::class, EpisodeEntity::class,
    ],
    version = 16,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3), AutoMigration(
            from = 1,
            to = 3,
        ), AutoMigration(from = 3, to = 4), AutoMigration(from = 2, to = 4), AutoMigration(
            from = 3,
            to = 5,
        ), AutoMigration(4, 5), AutoMigration(6, 7), AutoMigration(
            7,
            8,
            spec = AutoMigration7_8::class,
        ), AutoMigration(8, 9),
        AutoMigration(9, 10),
        AutoMigration(from = 11, to = 12, spec = AutoMigration11_12::class),
        AutoMigration(13, 14),
        AutoMigration(14, 15),
        AutoMigration(15, 16),
    ],
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): DatabaseDao
}