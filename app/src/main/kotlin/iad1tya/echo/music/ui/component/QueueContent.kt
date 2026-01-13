package iad1tya.echo.music.ui.component

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.source.ShuffleOrder.DefaultShuffleOrder
import androidx.navigation.NavController
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.ListItemHeight
import iad1tya.echo.music.constants.QueueEditLockKey
import iad1tya.echo.music.extensions.metadata
import iad1tya.echo.music.extensions.move
import iad1tya.echo.music.extensions.togglePlayPause
import iad1tya.echo.music.extensions.toggleRepeatMode
import iad1tya.echo.music.utils.rememberPreference
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QueueContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.background,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onBackground,
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current

    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val repeatMode by playerConnection.repeatMode.collectAsState()
    val shuffleModeEnabled by playerConnection.shuffleModeEnabled.collectAsState()
    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsState()

    var locked by rememberPreference(QueueEditLockKey, defaultValue = false)
    val selectedSongs = remember { mutableStateListOf<iad1tya.echo.music.models.MediaMetadata>() }
    val selectedItems = remember { mutableStateListOf<Timeline.Window>() }
    var selection by remember { mutableStateOf(false) }

    if (selection) {
        BackHandler {
            selection = false
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var dismissJob: Job? by remember { mutableStateOf(null) }

    val queueWindows by playerConnection.queueWindows.collectAsState()
    val mutableQueueWindows = remember { mutableStateListOf<Timeline.Window>() }

    val coroutineScope = rememberCoroutineScope()

    val headerItems = 1
    val lazyListState = rememberLazyListState()
    var dragInfo by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    val reorderableState = rememberReorderableLazyListState(
        lazyListState = lazyListState,
        scrollThresholdPadding = WindowInsets.systemBars.add(
            WindowInsets(
                top = ListItemHeight,
                bottom = ListItemHeight
            )
        ).asPaddingValues()
    ) { from, to ->
        val currentDragInfo = dragInfo
        dragInfo = if (currentDragInfo == null) {
            from.index to to.index
        } else {
            currentDragInfo.first to to.index
        }

        val safeFrom = (from.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)
        val safeTo = (to.index - headerItems).coerceIn(0, mutableQueueWindows.lastIndex)

        mutableQueueWindows.move(safeFrom, safeTo)
    }

    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging) {
            dragInfo?.let { (from, to) ->
                val safeFrom = (from - headerItems).coerceIn(0, queueWindows.lastIndex)
                val safeTo = (to - headerItems).coerceIn(0, queueWindows.lastIndex)

                if (!playerConnection.player.shuffleModeEnabled) {
                    playerConnection.player.moveMediaItem(safeFrom, safeTo)
                } else {
                    playerConnection.player.setShuffleOrder(
                        DefaultShuffleOrder(
                            queueWindows.map { it.firstPeriodIndex }
                                .toMutableList()
                                .move(safeFrom, safeTo)
                                .toIntArray(),
                            System.currentTimeMillis()
                        )
                    )
                }
                dragInfo = null
            }
        }
    }

    LaunchedEffect(queueWindows) {
        mutableQueueWindows.apply {
            clear()
            addAll(queueWindows)
        }
    }

    LaunchedEffect(mutableQueueWindows) {
        if (currentWindowIndex != -1) {
            lazyListState.scrollToItem(currentWindowIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Transparent),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Queue Title at top left
            Text(
                text = "Queue",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 60.dp, bottom = 16.dp),
                color = onBackgroundColor
            )
            
            // Header with controls - individual pills for each button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle button with individual pill
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .clickable {
                            playerConnection.player.shuffleModeEnabled =
                                !playerConnection.player.shuffleModeEnabled
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = "Shuffle",
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(if (shuffleModeEnabled) 1f else 0.5f),
                        tint = if (shuffleModeEnabled) MaterialTheme.colorScheme.primary
                        else onBackgroundColor
                    )
                }

                // Repeat button with individual pill
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .clickable {
                            playerConnection.player.toggleRepeatMode()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            when (repeatMode) {
                                Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                else -> R.drawable.repeat
                            }
                        ),
                        contentDescription = "Repeat",
                        modifier = Modifier
                            .size(24.dp)
                            .alpha(if (repeatMode == Player.REPEAT_MODE_OFF) 0.5f else 1f),
                        tint = if (repeatMode != Player.REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary
                        else onBackgroundColor
                    )
                }

                // Lock button with individual pill
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .clickable {
                            locked = !locked
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            if (locked) R.drawable.lock else R.drawable.lock_open
                        ),
                        contentDescription = "Lock Queue",
                        modifier = Modifier.size(24.dp),
                        tint = if (locked) MaterialTheme.colorScheme.primary
                        else onBackgroundColor
                    )
                }
            }

            // "Continue Playing" text WITHOUT background
            Text(
                text = "Continue Playing",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, top = 20.dp, bottom = 12.dp),
                color = onBackgroundColor
            )

            // Queue list
            LazyColumn(
                state = lazyListState,
                contentPadding = WindowInsets.systemBars.add(
                    WindowInsets(
                        bottom = ListItemHeight + 8.dp,
                    ),
                ).asPaddingValues(),
            ) {
                itemsIndexed(
                    items = mutableQueueWindows,
                    key = { _, item -> item.uid.hashCode() },
                ) { index, window ->
                    ReorderableItem(
                        state = reorderableState,
                        key = window.uid.hashCode(),
                    ) {
                        val currentItem by rememberUpdatedState(window)
                        val dismissBoxState =
                            rememberSwipeToDismissBoxState(
                                positionalThreshold = { totalDistance -> totalDistance }
                            )

                        var processedDismiss by remember { mutableStateOf(false) }
                        LaunchedEffect(dismissBoxState.currentValue) {
                            val dv = dismissBoxState.currentValue
                            if (!processedDismiss && (
                                    dv == SwipeToDismissBoxValue.StartToEnd ||
                                    dv == SwipeToDismissBoxValue.EndToStart
                                )
                            ) {
                                processedDismiss = true
                                playerConnection.player.removeMediaItem(currentItem.firstPeriodIndex)
                                dismissJob?.cancel()
                                dismissJob = coroutineScope.launch {
                                    val snackbarResult = snackbarHostState.showSnackbar(
                                        message = context.getString(
                                            R.string.removed_song_from_playlist,
                                            currentItem.mediaItem.metadata?.title,
                                        ),
                                        actionLabel = context.getString(R.string.undo),
                                        duration = SnackbarDuration.Short,
                                    )
                                    if (snackbarResult == SnackbarResult.ActionPerformed) {
                                        playerConnection.player.addMediaItem(currentItem.mediaItem)
                                        playerConnection.player.moveMediaItem(
                                            mutableQueueWindows.size,
                                            currentItem.firstPeriodIndex,
                                        )
                                    }
                                }
                            }
                            if (dv == SwipeToDismissBoxValue.Settled) {
                                processedDismiss = false
                            }
                        }

                        val content: @Composable () -> Unit = {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.animateItem(),
                            ) {
                                MediaMetadataListItem(
                                    mediaMetadata = window.mediaItem.metadata!!,
                                    isSelected = selection && window.mediaItem.metadata!! in selectedSongs,
                                    isActive = index == currentWindowIndex,
                                    isPlaying = isPlaying,
                                    trailingContent = {
                                        if (!locked) {
                                            IconButton(
                                                onClick = { },
                                                modifier = Modifier.draggableHandle()
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.drag_handle),
                                                    contentDescription = null,
                                                )
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (index == currentWindowIndex)
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            else Color.Transparent
                                        )
                                        .combinedClickable(
                                            onClick = {
                                                if (selection) {
                                                    if (window.mediaItem.metadata!! in selectedSongs) {
                                                        selectedSongs.remove(window.mediaItem.metadata!!)
                                                        selectedItems.remove(currentItem)
                                                    } else {
                                                        selectedSongs.add(window.mediaItem.metadata!!)
                                                        selectedItems.add(currentItem)
                                                    }
                                                } else {
                                                    if (index == currentWindowIndex) {
                                                        playerConnection.player.togglePlayPause()
                                                    } else {
                                                        playerConnection.player.seekToDefaultPosition(
                                                            window.firstPeriodIndex,
                                                        )
                                                        playerConnection.player.playWhenReady = true
                                                    }
                                                }
                                            },
                                            onLongClick = {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                if (!selection) {
                                                    selection = true
                                                }
                                                selectedSongs.clear()
                                                selectedSongs.add(window.mediaItem.metadata!!)
                                            },
                                        ),
                                )
                            }
                        }

                        if (locked) {
                            content()
                        } else {
                            SwipeToDismissBox(
                                state = dismissBoxState,
                                backgroundContent = {},
                            ) {
                                content()
                            }
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter),
        )
        
        // Top fade edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.TopCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Bottom fade edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f)
                        )
                    )
                )
        )
    }
}
