package iad1tya.echo.music.pagination.localPlaylistPaging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import iad1tya.echo.music.data.db.LocalDataSource
import iad1tya.echo.music.data.db.entities.PairSongLocalPlaylist
import iad1tya.echo.music.data.db.entities.SongEntity
import iad1tya.echo.music.viewModel.FilterState

class LocalPlaylistPagingSource(
    private val playlistId: Long,
    private val totalCount: Int,
    private val filter: FilterState,
    private val localDataSource: LocalDataSource,
) : PagingSource<Int, SongEntity>() {
    override fun getRefreshKey(state: PagingState<Int, SongEntity>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SongEntity> {
        return try {
            val currentPage = params.key ?: 0
            val pairs =
                localDataSource.getPlaylistPairSongByOffset(
                    playlistId = playlistId,
                    filterState = filter,
                    offset = currentPage,
                    totalCount = totalCount,
                )
            Log.d("LocalPlaylistPagingSource", "load: $pairs")
            val songs =
                localDataSource
                    .getSongByListVideoIdFull(
                        pairs?.map { it.songId } ?: emptyList(),
                    )
            val idValue = songs.associateBy { it.videoId }
            val sorted =
                (pairs ?: mutableListOf<PairSongLocalPlaylist>()).mapNotNull {
                    idValue[it.songId]
                }
            Log.d("LocalPlaylistPagingSource", "load: $songs")
            return LoadResult.Page(
                data = sorted,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = if (songs.isEmpty()) null else currentPage + 1,
            )
        } catch (e: Exception) {
            Log.e("LocalPlaylistPagingSource", "load: ${e.printStackTrace()}")
            LoadResult.Error(e)
        }
    }
}