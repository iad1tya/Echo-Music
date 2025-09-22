package iad1tya.echo.music.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import iad1tya.echo.music.pagination.RecentPagingSource
import iad1tya.echo.music.viewModel.base.BaseViewModel

@UnstableApi
class RecentlySongsViewModel(
    application: Application,
) : BaseViewModel(application) {
    val recentlySongs =
        Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
            ),
        ) {
            RecentPagingSource(mainRepository)
        }.flow.cachedIn(viewModelScope)
}