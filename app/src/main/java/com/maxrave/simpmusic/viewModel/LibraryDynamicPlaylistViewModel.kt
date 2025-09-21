package iad1tya.echo.music.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import iad1tya.echo.music.R
import iad1tya.echo.music.common.Config
import iad1tya.echo.music.data.db.entities.ArtistEntity
import iad1tya.echo.music.data.db.entities.SongEntity
import iad1tya.echo.music.extension.toArrayListTrack
import iad1tya.echo.music.extension.toTrack
import iad1tya.echo.music.service.PlaylistType
import iad1tya.echo.music.service.QueueData
import iad1tya.echo.music.ui.screen.library.LibraryDynamicPlaylistType
import iad1tya.echo.music.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@UnstableApi
class LibraryDynamicPlaylistViewModel(
    application: Application,
) : BaseViewModel(application) {
    private val _listFavoriteSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listFavoriteSong: StateFlow<List<SongEntity>> get() = _listFavoriteSong

    private val _listFollowedArtist: MutableStateFlow<List<ArtistEntity>> = MutableStateFlow(emptyList())
    val listFollowedArtist: StateFlow<List<ArtistEntity>> get() = _listFollowedArtist

    private val _listMostPlayedSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listMostPlayedSong: StateFlow<List<SongEntity>> get() = _listMostPlayedSong

    private val _listDownloadedSong: MutableStateFlow<List<SongEntity>> = MutableStateFlow(emptyList())
    val listDownloadedSong: StateFlow<List<SongEntity>> get() = _listDownloadedSong

    init {
        getFavoriteSong()
        getFollowedArtist()
        getMostPlayedSong()
        getDownloadedSong()
    }

    private fun getFavoriteSong() {
        viewModelScope.launch {
            mainRepository.getLikedSongs().collectLatest { likedSong ->
                _listFavoriteSong.value = likedSong.reversed()
            }
        }
    }

    private fun getFollowedArtist() {
        viewModelScope.launch {
            mainRepository.getFollowedArtists().collectLatest { followedArtist ->
                _listFollowedArtist.value = followedArtist.reversed()
            }
        }
    }

    private fun getMostPlayedSong() {
        viewModelScope.launch {
            mainRepository.getMostPlayedSongs().collectLatest { mostPlayedSong ->
                _listMostPlayedSong.value = mostPlayedSong.sortedByDescending { it.totalPlayTime }
            }
        }
    }

    private fun getDownloadedSong() {
        viewModelScope.launch {
            mainRepository.getDownloadedSongs().collectLatest { downloadedSong ->
                _listDownloadedSong.value = downloadedSong?.reversed() ?: emptyList()
            }
        }
    }

    fun playSong(
        videoId: String,
        type: LibraryDynamicPlaylistType,
    ) {
        val (targetList, playTrack) =
            when (type) {
                LibraryDynamicPlaylistType.Favorite -> listFavoriteSong.value to listFavoriteSong.value.find { it.videoId == videoId }
                LibraryDynamicPlaylistType.Downloaded -> listDownloadedSong.value to listDownloadedSong.value.find { it.videoId == videoId }
                LibraryDynamicPlaylistType.Followed -> return
                LibraryDynamicPlaylistType.MostPlayed -> listMostPlayedSong.value to listMostPlayedSong.value.find { it.videoId == videoId }
            }
        if (playTrack == null) return
        setQueueData(
            QueueData(
                listTracks = targetList.toArrayListTrack(),
                firstPlayedTrack = playTrack.toTrack(),
                playlistId = null,
                playlistName = "${
                    getString(
                        R.string.playlist,
                    )
                } ${getString(type.name())}",
                playlistType = PlaylistType.RADIO,
                continuation = null,
            ),
        )
        loadMediaItem(
            playTrack.toTrack(),
            Config.PLAYLIST_CLICK,
            0,
        )
    }
}