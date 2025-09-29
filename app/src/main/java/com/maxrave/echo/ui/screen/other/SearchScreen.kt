package iad1tya.echo.music.ui.screen.other

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import coil3.request.crossfade
import iad1tya.echo.kotlinytmusicscraper.models.AlbumItem
import iad1tya.echo.kotlinytmusicscraper.models.ArtistItem
import iad1tya.echo.kotlinytmusicscraper.models.PlaylistItem
import iad1tya.echo.kotlinytmusicscraper.models.SongItem
import iad1tya.echo.kotlinytmusicscraper.models.VideoItem
import iad1tya.echo.kotlinytmusicscraper.models.YTItem
import iad1tya.echo.music.R
import iad1tya.echo.music.common.Config
import iad1tya.echo.music.data.db.entities.SongEntity
import iad1tya.echo.music.data.model.browse.album.Track
import iad1tya.echo.music.data.model.searchResult.albums.AlbumsResult
import iad1tya.echo.music.data.model.searchResult.artists.ArtistsResult
import iad1tya.echo.music.data.model.searchResult.playlists.PlaylistsResult
import iad1tya.echo.music.data.model.searchResult.songs.SongsResult
import iad1tya.echo.music.data.model.searchResult.songs.Thumbnail
import iad1tya.echo.music.data.model.searchResult.videos.VideosResult
import iad1tya.echo.music.extension.connectArtists
import iad1tya.echo.music.extension.toAlbumsResult
import iad1tya.echo.music.extension.toSongEntity
import iad1tya.echo.music.extension.toTrack
import iad1tya.echo.music.service.PlaylistType
import iad1tya.echo.music.service.QueueData
import iad1tya.echo.music.ui.component.ArtistFullWidthItems
import iad1tya.echo.music.ui.component.Chip
import iad1tya.echo.music.ui.component.EndOfPage
import iad1tya.echo.music.ui.component.NowPlayingBottomSheet
import iad1tya.echo.music.ui.component.PlaylistFullWidthItems
import iad1tya.echo.music.ui.component.ShimmerSearchItem
import iad1tya.echo.music.ui.component.SongFullWidthItems
import iad1tya.echo.music.ui.navigation.destination.list.AlbumDestination
import iad1tya.echo.music.ui.navigation.destination.list.ArtistDestination
import iad1tya.echo.music.ui.navigation.destination.list.PlaylistDestination
import iad1tya.echo.music.ui.navigation.destination.list.PodcastDestination
import iad1tya.echo.music.ui.theme.typo
import iad1tya.echo.music.viewModel.SearchScreenUIState
import iad1tya.echo.music.viewModel.SearchType
import iad1tya.echo.music.viewModel.SearchViewModel
import iad1tya.echo.music.viewModel.SharedViewModel
import iad1tya.echo.music.viewModel.toStringRes
import org.koin.compose.koinInject

data class AlbumData(
    val title: String,
    val artist: String,
    val genre: String,
    val imageUrl: String
)

data class GenreData(
    val name: String,
    val color: Color,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@UnstableApi
fun SearchScreen(
    searchViewModel: SearchViewModel = koinInject(),
    sharedViewModel: SharedViewModel = koinInject(),
    navController: NavController,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchScreenState by searchViewModel.searchScreenState.collectAsStateWithLifecycle()
    val uiState by searchViewModel.searchScreenUIState.collectAsStateWithLifecycle()
    val searchHistory by searchViewModel.searchHistory.collectAsStateWithLifecycle()
    val searchTextFromViewModel by searchViewModel.searchText.collectAsStateWithLifecycle()

    var searchUIType by rememberSaveable { 
        mutableStateOf(
            if (searchTextFromViewModel.isNotEmpty()) SearchUIType.SEARCH_RESULTS else SearchUIType.EMPTY
        ) 
    }
    var searchText by rememberSaveable { mutableStateOf(searchTextFromViewModel) }
    var isSearchSubmitted by rememberSaveable { mutableStateOf(searchTextFromViewModel.isNotEmpty()) }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    var isFocused by rememberSaveable { mutableStateOf(false) }

    var sheetSong by remember { mutableStateOf<SongEntity?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val currentVideoId by searchViewModel.nowPlayingVideoId.collectAsStateWithLifecycle()
    val chipRowState = rememberScrollState()
    val pullToRefreshState = rememberPullToRefreshState()
    
    // Get mini-player state to calculate proper bottom padding
    val nowPlayingData by sharedViewModel.nowPlayingState.collectAsStateWithLifecycle()
    val isMiniPlayerActive = nowPlayingData?.mediaItem != null && nowPlayingData?.mediaItem != MediaItem.EMPTY

    // Voice search launcher
    val voiceSearchLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        searchViewModel.handleVoiceSearchResult(result.resultCode, result.data)
    }

    // Set the launcher in the view model and restore search state
    LaunchedEffect(Unit) {
        searchViewModel.setVoiceSearchLauncher(voiceSearchLauncher)
        
        // Restore search state if we have a previous search
        if (searchTextFromViewModel.isNotEmpty()) {
            searchText = searchTextFromViewModel
            isSearchSubmitted = true
            searchUIType = SearchUIType.SEARCH_RESULTS
        }
    }
    
    // Sync search text from ViewModel (for voice search)
    LaunchedEffect(searchTextFromViewModel) {
        if (searchTextFromViewModel.isNotEmpty() && searchTextFromViewModel != searchText) {
            searchText = searchTextFromViewModel
            isSearchSubmitted = true
            searchUIType = SearchUIType.SEARCH_RESULTS
        }
    }
    
    // Don't clear search state when leaving the screen to preserve search results when navigating back
    // DisposableEffect(Unit) {
    //     onDispose {
    //         searchViewModel.clearSearchState()
    //     }
    // }

    val onMoreClick: (SongEntity) -> Unit = { song ->
        sheetSong = song
        showBottomSheet = true
    }

    LaunchedEffect(searchText) {
        if (isFocused) {
            isSearchSubmitted = false
            isExpanded = true
        }
        if (searchText.isNotEmpty() && isFocused) {
            searchViewModel.suggestQuery(searchText)
        }
    }

    LaunchedEffect(isSearchSubmitted) {
        if (isSearchSubmitted) {
            isExpanded = false
        }
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            isExpanded = true
        }
    }

    LaunchedEffect(isExpanded, searchText, isFocused) {
        searchUIType =
            if (searchText.isNotEmpty() && isExpanded) {
                SearchUIType.SEARCH_SUGGESTIONS
            } else if (isFocused && isExpanded) {
                SearchUIType.SEARCH_HISTORY
            } else if (searchText.isEmpty()) {
                SearchUIType.EMPTY
            } else {
                SearchUIType.SEARCH_RESULTS
            }
    }

    if (showBottomSheet) {
        NowPlayingBottomSheet(
            onDismiss = {
                showBottomSheet = false
                sheetSong = null
            },
            navController = navController,
            song = sheetSong,
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(vertical = 10.dp),
    ) {
        // Search Bar
        // Search suggestions within search bar dropdown

        // YTItem suggestions
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = { newText ->
                        searchText = newText
                    },
                    onSearch = { query ->
                        if (query.isNotEmpty()) {
                            isSearchSubmitted = true
                            focusManager.clearFocus()
                            searchViewModel.insertSearchHistory(query)
                            when (searchScreenState.searchType) {
                                SearchType.ALL -> searchViewModel.searchAll(query)
                                SearchType.SONGS -> searchViewModel.searchSongs(query)
                                SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                            }
                        }
                    },
                    expanded = false,
                    onExpandedChange = {},
                    enabled = true,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.what_do_you_want_to_listen_to),
                            style = typo.labelMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_search_24),
                            contentDescription = "Search",
                        )
                    },
                    trailingIcon = {
                        Row {
                            // Voice search button
                            IconButton(
                                modifier = Modifier
                                    .clip(CircleShape),
                                onClick = {
                                    searchViewModel.startVoiceSearch()
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice search",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                            
                            // Clear button (only show when there's text)
                            if (searchText.isNotEmpty()) {
                                IconButton(
                                    modifier = Modifier
                                        .clip(CircleShape),
                                    onClick = {
                                        searchText = ""
                                        isSearchSubmitted = false
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_close_24),
                                        contentDescription = "Clear search",
                                    )
                                }
                            }
                        }
                    },
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    }.padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            content = {},
        )

        Crossfade(targetState = searchUIType) {
            when (it) {
                SearchUIType.SEARCH_SUGGESTIONS -> {
                    LazyColumn(
                        Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp,
                        ),
                    ) {
                        items(searchScreenState.suggestYTItems) { item ->
                            SuggestYTItemRow(
                                ytItem = item,
                                onItemClick = { ytItem ->
                                    // Hide keyboard and add to search history
                                    focusManager.clearFocus()
                                    searchViewModel.insertSearchHistory(ytItem.title)
                                    
                                    when (ytItem) {
                                        is SongItem, is VideoItem -> {
                                            val firstTrack: Track = (ytItem as? SongItem)?.toTrack() ?: (ytItem as VideoItem).toTrack()
                                            searchViewModel.setQueueData(
                                                QueueData(
                                                    listTracks = arrayListOf(firstTrack),
                                                    firstPlayedTrack = firstTrack,
                                                    playlistId = "RDAMVM${ytItem.id}",
                                                    playlistName = "\"${searchText}\" ${context.getString(R.string.in_search)}",
                                                    playlistType = PlaylistType.RADIO,
                                                    continuation = null,
                                                ),
                                            )
                                            searchViewModel.loadMediaItem(firstTrack, type = Config.SONG_CLICK)
                                        }

                                        is ArtistItem -> {
                                            navController.navigate(
                                                ArtistDestination(ytItem.id),
                                                navOptions = NavOptions.Builder()
                                                    .setLaunchSingleTop(true)
                                                    .build()
                                            )
                                        }

                                        is AlbumItem -> {
                                            navController.navigate(
                                                AlbumDestination(ytItem.browseId),
                                                navOptions = NavOptions.Builder()
                                                    .setLaunchSingleTop(true)
                                                    .build()
                                            )
                                        }

                                        is PlaylistItem -> {
                                            navController.navigate(
                                                PlaylistDestination(
                                                    ytItem.id,
                                                ),
                                                navOptions = NavOptions.Builder()
                                                    .setLaunchSingleTop(true)
                                                    .build()
                                            )
                                        }
                                    }
                                },
                            )
                        }
                        items(searchScreenState.suggestQueries) { suggestion ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple(),
                                            onClick = {
                                                searchText = suggestion
                                                focusManager.clearFocus()
                                                isSearchSubmitted = true
                                                searchViewModel.insertSearchHistory(suggestion)
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchViewModel.searchAll(suggestion)
                                                    SearchType.SONGS -> searchViewModel.searchSongs(suggestion)
                                                    SearchType.VIDEOS -> searchViewModel.searchVideos(suggestion)
                                                    SearchType.ALBUMS -> searchViewModel.searchAlbums(suggestion)
                                                    SearchType.ARTISTS -> searchViewModel.searchArtists(suggestion)
                                                    SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(suggestion)
                                                    SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(suggestion)
                                                    SearchType.PODCASTS -> searchViewModel.searchPodcast(suggestion)
                                                }
                                            },
                                        ).padding(horizontal = 12.dp, vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = suggestion,
                                    style = typo.bodyMedium,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        searchText = suggestion
                                        focusRequester.requestFocus()
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_arrow_outward_24),
                                        contentDescription = "Search suggestion",
                                        modifier = Modifier.size(24.dp),
                                    )
                                }
                            }
                        }
                        item {
                            EndOfPage(
                                withoutCredit = true,
                            )
                        }
                    }
                }

                SearchUIType.SEARCH_HISTORY -> {
                    // Search history state
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 10.dp,
                                ),
                    ) {
                        LazyColumn {
                            stickyHeader {
                                Crossfade(
                                    targetState = searchHistory.isNotEmpty(),
                                ) {
                                    if (it) {
                                        Row(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.Black),
                                        ) {
                                            TextButton(
                                                onClick = { searchViewModel.deleteSearchHistory() },
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.clear_search_history),
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            items(searchHistory) { historyItem ->
                                Row(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                searchText = historyItem
                                                focusManager.clearFocus()
                                                isSearchSubmitted = true
                                                searchViewModel.insertSearchHistory(historyItem)
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchViewModel.searchAll(historyItem)
                                                    SearchType.SONGS -> searchViewModel.searchSongs(historyItem)
                                                    SearchType.VIDEOS -> searchViewModel.searchVideos(historyItem)
                                                    SearchType.ALBUMS -> searchViewModel.searchAlbums(historyItem)
                                                    SearchType.ARTISTS -> searchViewModel.searchArtists(historyItem)
                                                    SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(historyItem)
                                                    SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(historyItem)
                                                    SearchType.PODCASTS -> searchViewModel.searchPodcast(historyItem)
                                                }
                                            }.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_history_24),
                                        contentDescription = "Search history",
                                        modifier = Modifier.size(24.dp),
                                    )
                                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                                    Text(
                                        text = historyItem,
                                        style = typo.bodyMedium,
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    IconButton(
                                        onClick = {
                                            searchText = historyItem
                                            focusRequester.requestFocus()
                                        },
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_arrow_outward_24),
                                            contentDescription = "Search suggestion",
                                            modifier = Modifier.size(24.dp),
                                        )
                                    }
                                }
                            }
                            item {
                                EndOfPage(
                                    withoutCredit = true,
                                )
                            }
                        }
                    }
                }

                SearchUIType.EMPTY -> {
                    // Modern music discovery interface without pull-to-refresh on empty state
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        state = rememberLazyListState(),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        
                        // Add 0.5cm spacing between search bar and Featured section
                        item {
                            Spacer(modifier = Modifier.height(19.dp))
                        }
                        
                        // Featured Section
                        item {
                            Text(
                                text = "Featured",
                                style = typo.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        item {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(listOf(
                                    AlbumData("Trending 2025", "Latest Hits", "Global", "https://i.scdn.co/image/ab67616d0000b273bb54dde68cd23e2a268ae0f5"),
                                    AlbumData("Trending Bollywood", "Bollywood Hits", "Hindi", "https://i.scdn.co/image/ab67616d0000b27349d694203245f241a1bcaa72"),
                                    AlbumData("Instagram Trending", "Viral Songs", "Social Media", "https://i.scdn.co/image/ab67616d0000b27370dbc63f8b5aa80c3c73b2c0")
                                )) { album ->
                                    Card(
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(280.dp)
                                            .clickable {
                                                searchText = album.title
                                                isSearchSubmitted = true
                                                focusManager.clearFocus()
                                                searchViewModel.insertSearchHistory(album.title)
                                                searchViewModel.searchAll(album.title)
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.Transparent
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            // Background gradient
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        when (album.title) {
                                                            "Trending 2025" -> Color(0xFF2D1B69)
                                                            "Trending Bollywood" -> Color(0xFFFF6B35)
                                                            "Instagram Trending" -> Color(0xFF8B5CF6)
                                                            "Trending 90s" -> Color(0xFF4A90E2)
                                                            "Trending 00s" -> Color(0xFFE91E63)
                                                            else -> Color(0xFF96CEB4)
                                                        },
                                                        RoundedCornerShape(12.dp)
                                                    )
                                            )
                                            
                                            // Album art image - use local drawable based on album title
                                            val imageRes = when (album.title) {
                                                "Trending 2025" -> R.drawable.trending
                                                "Trending Bollywood" -> R.drawable.trending_bollywood
                                                "Instagram Trending" -> R.drawable.trending_social_media
                                                "Trending 90s" -> R.drawable.trending
                                                "Trending 00s" -> R.drawable.trending
                                                else -> R.drawable.echo_nobg
                                            }
                                            
                                            // Large square tilted image positioned on the right side
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(imageRes)
                                                    .diskCachePolicy(CachePolicy.ENABLED)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                placeholder = painterResource(R.drawable.echo_nobg),
                                                error = painterResource(R.drawable.echo_nobg),
                                                modifier = Modifier
                                                    .size(140.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .align(Alignment.TopEnd)
                                                    .offset(15.dp, -20.dp)
                                                    .graphicsLayer {
                                                        rotationZ = 15f
                                                    }
                                            )
                                            
                                            // Text properly aligned to bottom-left corner
                                            Column(
                                                modifier = Modifier
                                                    .align(Alignment.BottomStart)
                                                    .padding(16.dp, 0.dp, 0.dp, 16.dp)
                                            ) {
                                                Text(
                                                    text = "#${album.genre}",
                                                    style = typo.labelSmall,
                                                    color = Color.White.copy(alpha = 0.8f),
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = album.title,
                                                    style = typo.titleMedium,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 2
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = album.artist,
                                                    style = typo.bodyMedium,
                                                    color = Color.White.copy(alpha = 0.9f),
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Mood & Genres Grid Section
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "Mood & Genres",
                                style = typo.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        
                        item {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.height(520.dp)
                            ) {
                                items(listOf(
                                    GenreData("Hip Hop", Color(0xFF74B9FF), "https://i.scdn.co/image/ab67616d0000b273c8a11e48c91f982b9a4152e0"),
                                    GenreData("LoFi", Color(0xFFA29BFE), "https://i.scdn.co/image/ab67616d0000b273a91c10fe9472d9bd89802e5a"),
                                    GenreData("Pop", Color(0xFF00B894), "https://i.scdn.co/image/ab67616d0000b273b0ca6153f052f56275a10f26"),
                                    GenreData("Party", Color(0xFFE84393), "https://i.scdn.co/image/ab67616d0000b2736e7f8a9b0c1d2e3f4a5b6c7d"),
                                    GenreData("Blues", Color(0xFF6C5CE7), "https://i.scdn.co/image/ab67616d0000b2737f8a9b0c1d2e3f4a5b6c7d8e"),
                                    GenreData("Techno", Color(0xFF74B9FF), "https://i.scdn.co/image/ab67616d0000b2738a9b0c1d2e3f4a5b6c7d8e9f"),
                                    GenreData("Gym", Color(0xFFFF6B6B), "https://i.scdn.co/image/ab67616d0000b2739b0c1d2e3f4a5b6c7d8e9f0a"),
                                    GenreData("Romance", Color(0xFFFF69B4), "https://i.scdn.co/image/ab67616d0000b2730c1d2e3f4a5b6c7d8e9f0a1b")
                                )) { genre ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                            .clickable {
                                                searchText = genre.name
                                                isSearchSubmitted = true
                                                focusManager.clearFocus()
                                                searchViewModel.insertSearchHistory(genre.name)
                                                searchViewModel.searchAll(genre.name)
                                            }
                                    ) {
                                        // Main card with rounded corners
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp)
                                                .align(Alignment.CenterStart),
                                            colors = CardDefaults.cardColors(
                                                containerColor = genre.color
                                            ),
                                            shape = RoundedCornerShape(20.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                // Genre text on the left
                                                Text(
                                                    text = genre.name,
                                                    style = typo.headlineSmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .align(Alignment.CenterStart)
                                                        .padding(start = 24.dp)
                                                )
                                            }
                                        }
                                        
                                        // Angled overlapping album art on the right
                                        val imageRes = when (genre.name) {
                                            "Hip Hop" -> R.drawable.hiphop
                                            "LoFi" -> R.drawable.lofi
                                            "Pop" -> R.drawable.pop
                                            "Party" -> R.drawable.party
                                            "Blues" -> R.drawable.blues
                                            "Techno" -> R.drawable.techno
                                            "Gym" -> R.drawable.gym
                                            "Romance" -> R.drawable.romance
                                            else -> R.drawable.echo_nobg
                                        }
                                        
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(imageRes)
                                                .diskCachePolicy(CachePolicy.ENABLED)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            placeholder = painterResource(R.drawable.echo_nobg),
                                            error = painterResource(R.drawable.echo_nobg),
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .align(Alignment.CenterEnd)
                                                .offset(20.dp, 0.dp)
                                                .graphicsLayer {
                                                    rotationZ = 15f
                                                }
                                        )
                                    }
                                }
                            }
                        }
                        
                        item {
                            // Dynamic bottom padding based on mini player state
                            val bottomPadding = if (isMiniPlayerActive) 200.dp else 140.dp
                            Spacer(modifier = Modifier.height(bottomPadding))
                        }
                    }
                }

                SearchUIType.SEARCH_RESULTS -> {
                    // Content area
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Filter chips
                        Row(
                            modifier =
                                Modifier
                                    .horizontalScroll(chipRowState)
                                    .padding(top = 10.dp)
                                    .padding(horizontal = 12.dp),
                        ) {
                            SearchType.entries.forEach { id ->
                                val isSelected = id == searchScreenState.searchType
                                Spacer(modifier = Modifier.width(4.dp))
                                Chip(
                                    isAnimated = uiState is SearchScreenUIState.Loading,
                                    isSelected = isSelected,
                                    text = stringResource(id = id.toStringRes()),
                                ) {
                                    searchViewModel.setSearchType(id)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                        PullToRefreshBox(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 10.dp),
                            state = pullToRefreshState,
                            onRefresh = {
                                val query = searchText.trim()
                                if (query.isNotEmpty()) {
                                    isSearchSubmitted = true
                                    searchViewModel.insertSearchHistory(query)
                                    when (searchScreenState.searchType) {
                                        SearchType.ALL -> searchViewModel.searchAll(query)
                                        SearchType.SONGS -> searchViewModel.searchSongs(query)
                                        SearchType.VIDEOS -> searchViewModel.searchVideos(query)
                                        SearchType.ALBUMS -> searchViewModel.searchAlbums(query)
                                        SearchType.ARTISTS -> searchViewModel.searchArtists(query)
                                        SearchType.PLAYLISTS -> searchViewModel.searchPlaylists(query)
                                        SearchType.FEATURED_PLAYLISTS -> searchViewModel.searchFeaturedPlaylist(query)
                                        SearchType.PODCASTS -> searchViewModel.searchPodcast(query)
                                    }
                                }
                            },
                            isRefreshing = uiState is SearchScreenUIState.Loading,
                            indicator = {
                                PullToRefreshDefaults.Indicator(
                                    state = pullToRefreshState,
                                    isRefreshing = uiState is SearchScreenUIState.Loading,
                                    modifier = Modifier.align(Alignment.TopCenter),
                                    containerColor = PullToRefreshDefaults.containerColor,
                                    color = PullToRefreshDefaults.indicatorColor,
                                    threshold = PullToRefreshDefaults.PositionalThreshold - 5.dp,
                                )
                            },
                        ) {
                            Crossfade(targetState = uiState) { uiState ->
                                when (uiState) {
                                    is SearchScreenUIState.Loading -> {
                                        // Loading state
                                        LazyColumn {
                                            items(10) {
                                                ShimmerSearchItem()
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Success -> {
                                        // Success state with results
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            // Search Results List
                                            val currentResults =
                                                when (searchScreenState.searchType) {
                                                    SearchType.ALL -> searchScreenState.searchAllResult
                                                    SearchType.SONGS -> searchScreenState.searchSongsResult
                                                    SearchType.VIDEOS -> searchScreenState.searchVideosResult
                                                    SearchType.ALBUMS -> searchScreenState.searchAlbumsResult
                                                    SearchType.ARTISTS -> searchScreenState.searchArtistsResult
                                                    SearchType.PLAYLISTS -> searchScreenState.searchPlaylistsResult
                                                    SearchType.FEATURED_PLAYLISTS -> searchScreenState.searchFeaturedPlaylistsResult
                                                    SearchType.PODCASTS -> searchScreenState.searchPodcastsResult
                                                }

                                            Crossfade(targetState = currentResults.isNotEmpty()) {
                                                if (it) {
                                                    LazyColumn(
                                                        contentPadding = PaddingValues(horizontal = 4.dp),
                                                        state = rememberLazyListState(),
                                                    ) {
                                                        items(currentResults) { result ->
                                                            when (result) {
                                                                is SongsResult ->
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title ?: "")
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${context.getString(R.string.in_search)}",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.SONG_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )

                                                                is VideosResult ->
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.videoId == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title ?: "")
                                                                            val firstTrack = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.videoId}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${context.getString(R.string.in_search)}",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.VIDEO_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )

                                                                is SongItem ->
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.id == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(searchText)
                                                                            val firstTrack: Track = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.id}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${context.getString(R.string.in_search)}",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.SONG_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )

                                                                is VideoItem ->
                                                                    SongFullWidthItems(
                                                                        track = result.toTrack(),
                                                                        isPlaying = result.id == currentVideoId,
                                                                        modifier = Modifier,
                                                                        onMoreClickListener = {
                                                                            onMoreClick(result.toTrack().toSongEntity())
                                                                        },
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(searchText)
                                                                            val firstTrack: Track = result.toTrack()
                                                                            searchViewModel.setQueueData(
                                                                                QueueData(
                                                                                    listTracks = arrayListOf(firstTrack),
                                                                                    firstPlayedTrack = firstTrack,
                                                                                    playlistId = "RDAMVM${result.id}",
                                                                                    playlistName =
                                                                                        "\"${searchText}\" ${context.getString(R.string.in_search)}",
                                                                                    playlistType = PlaylistType.RADIO,
                                                                                    continuation = null,
                                                                                ),
                                                                            )
                                                                            searchViewModel.loadMediaItem(firstTrack, Config.VIDEO_CLICK)
                                                                        },
                                                                        onAddToQueue = {
                                                                            sharedViewModel.addListToQueue(
                                                                                arrayListOf(result.toTrack()),
                                                                            )
                                                                        },
                                                                    )

                                                                is AlbumsResult ->
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title)
                                                                            navController.navigate(
                                                                                AlbumDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                                navOptions = NavOptions.Builder()
                                                                                    .setLaunchSingleTop(true)
                                                                                    .build()
                                                                            )
                                                                        },
                                                                    )

                                                                is ArtistsResult ->
                                                                    ArtistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.artist)
                                                                            navController.navigate(
                                                                                ArtistDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                                navOptions = NavOptions.Builder()
                                                                                    .setLaunchSingleTop(true)
                                                                                    .build()
                                                                            )
                                                                        },
                                                                    )

                                                                is PlaylistsResult ->
                                                                    PlaylistFullWidthItems(
                                                                        data = result,
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title)
                                                                            if (result.resultType == "Podcast") {
                                                                                navController.navigate(
                                                                                    PodcastDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                    navOptions = NavOptions.Builder()
                                                                                        .setLaunchSingleTop(true)
                                                                                        .build()
                                                                                )
                                                                            } else {
                                                                                navController.navigate(
                                                                                    PlaylistDestination(
                                                                                        result.browseId,
                                                                                    ),
                                                                                    navOptions = NavOptions.Builder()
                                                                                        .setLaunchSingleTop(true)
                                                                                        .build()
                                                                                )
                                                                            }
                                                                        },
                                                                    )

                                                                is AlbumItem ->
                                                                    PlaylistFullWidthItems(
                                                                        data = result.toAlbumsResult(),
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title)
                                                                            navController.navigate(
                                                                                AlbumDestination(
                                                                                    result.browseId,
                                                                                ),
                                                                                navOptions = NavOptions.Builder()
                                                                                    .setLaunchSingleTop(true)
                                                                                    .build()
                                                                            )
                                                                        },
                                                                    )

                                                                is ArtistItem ->
                                                                    ArtistFullWidthItems(
                                                                        data =
                                                                            ArtistsResult(
                                                                                artist = result.title,
                                                                                browseId = result.id,
                                                                                category = "",
                                                                                radioId = "",
                                                                                resultType = "",
                                                                                shuffleId = "",
                                                                                thumbnails =
                                                                                    listOf(
                                                                                        Thumbnail(
                                                                                            url = result.thumbnail,
                                                                                            width = 720,
                                                                                            height = 720,
                                                                                        ),
                                                                                    ),
                                                                            ),
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title)
                                                                            navController.navigate(
                                                                                ArtistDestination(
                                                                                    result.id,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )

                                                                is PlaylistItem ->
                                                                    PlaylistFullWidthItems(
                                                                        data =
                                                                            PlaylistsResult(
                                                                                author = result.author?.name ?: "YouTube Music",
                                                                                browseId = result.id,
                                                                                category = "",
                                                                                itemCount = "",
                                                                                resultType = "",
                                                                                thumbnails =
                                                                                    listOf(
                                                                                        Thumbnail(
                                                                                            url = result.thumbnail,
                                                                                            width = 720,
                                                                                            height = 720,
                                                                                        ),
                                                                                    ),
                                                                                title = result.title,
                                                                            ),
                                                                        onClickListener = {
                                                                            focusManager.clearFocus()
                                                                            searchViewModel.insertSearchHistory(result.title)
                                                                            navController.navigate(
                                                                                PlaylistDestination(
                                                                                    result.id,
                                                                                ),
                                                                            )
                                                                        },
                                                                    )
                                                            }
                                                        }
                                                        // Space at bottom to account for bottom navigation and mini player
                                                        item { Spacer(modifier = Modifier.height(150.dp)) }
                                                    }
                                                } else {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = stringResource(id = R.string.no_results_found),
                                                            style = typo.titleMedium,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is SearchScreenUIState.Error -> {
                                        Box {
                                            // Error state
                                            Column(
                                                modifier = Modifier.align(Alignment.Center),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                Text(
                                                    text = stringResource(id = R.string.error_occurred),
                                                    style = typo.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth(),
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Button(onClick = {
                                                    if (searchText.isNotEmpty()) {
                                                        searchViewModel.searchAll(searchText)
                                                    }
                                                }) {
                                                    Text(text = stringResource(id = R.string.retry))
                                                }
                                            }
                                        }
                                    }

                                    SearchScreenUIState.Empty -> {
                                        // Empty state - show simple message
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.no_results_found),
                                                style = typo.titleMedium,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestYTItemRow(
    ytItem: YTItem,
    onItemClick: (YTItem) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(ytItem) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val url =
            when (ytItem) {
                is SongItem ->
                    ytItem.thumbnails
                        ?.thumbnails
                        ?.lastOrNull()
                        ?.url
                is AlbumItem -> ytItem.thumbnail
                is ArtistItem -> ytItem.thumbnail
                is PlaylistItem -> ytItem.thumbnail
                is VideoItem ->
                    ytItem.thumbnails
                        ?.thumbnails
                        ?.lastOrNull()
                        ?.url
            }

        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(url)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .diskCacheKey(url)
                        .crossfade(true)
                        .build(),
                placeholder = painterResource(R.drawable.echo_nobg),
                error = painterResource(R.drawable.echo_nobg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(
                            if (ytItem is ArtistItem) {
                                CircleShape
                            } else {
                                RoundedCornerShape(4.dp)
                            },
                        ),
            )
        }

        Spacer(modifier = Modifier.padding(horizontal = 12.dp))

        Column(modifier = Modifier.weight(1f)) {
            val title =
                when (ytItem) {
                    is SongItem -> ytItem.title
                    is AlbumItem -> ytItem.title
                    is ArtistItem -> ytItem.title
                    is PlaylistItem -> ytItem.title
                    is VideoItem -> ytItem.title
                }

            Text(
                text = title,
                style = typo.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))

            val subtitle =
                when (ytItem) {
                    is SongItem -> ytItem.artists.map { it.name }.connectArtists()
                    is AlbumItem -> ytItem.artists?.mapNotNull { it.name }?.connectArtists()
                    is PlaylistItem -> ytItem.author?.name ?: stringResource(R.string.playlist)
                    is ArtistItem -> stringResource(R.string.artists)
                    is VideoItem -> ytItem.artists.map { it.name }.connectArtists()
                } ?: "Unknown"

            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = typo.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

enum class SearchUIType {
    EMPTY,
    SEARCH_HISTORY,
    SEARCH_SUGGESTIONS,
    SEARCH_RESULTS,
}