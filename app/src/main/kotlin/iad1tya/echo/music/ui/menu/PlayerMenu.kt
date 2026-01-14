package iad1tya.echo.music.ui.menu

import android.content.Intent
import android.content.res.Configuration
import android.media.audiofx.AudioEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.border
import iad1tya.echo.music.ui.utils.DetailsDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import android.widget.Toast
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import com.echo.innertube.YouTube
import com.echo.innertube.models.WatchEndpoint
import iad1tya.echo.music.LocalDatabase
import iad1tya.echo.music.LocalDownloadUtil
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.ListItemHeight
import iad1tya.echo.music.models.MediaMetadata
import iad1tya.echo.music.playback.ExoDownloadService
import iad1tya.echo.music.playback.queues.YouTubeQueue
import iad1tya.echo.music.ui.component.BigSeekBar
import iad1tya.echo.music.ui.component.BottomSheetState
import iad1tya.echo.music.ui.component.ListDialog
import iad1tya.echo.music.ui.component.AdvancedDownloadDialog
import iad1tya.echo.music.ui.component.NewAction
import iad1tya.echo.music.ui.component.NewActionGrid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.round
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import android.os.Build
import androidx.compose.foundation.layout.fillMaxHeight

@Composable
fun PlayerMenu(
    mediaMetadata: MediaMetadata?,
    navController: NavController,
    playerBottomSheetState: BottomSheetState,
    isQueueTrigger: Boolean? = false,
    onShowDetailsDialog: () -> Unit,
    onDismiss: () -> Unit,
) {
    mediaMetadata ?: return
    val context = LocalContext.current
    val database = LocalDatabase.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val playerVolume = playerConnection.service.playerVolume.collectAsState()
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    val librarySong by database.song(mediaMetadata.id).collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()

    val download by LocalDownloadUtil.current.getDownload(mediaMetadata.id)
        .collectAsState(initial = null)

    val artists =
        remember(mediaMetadata.artists) {
            mediaMetadata.artists.filter { it.id != null }
        }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    AddToPlaylistDialog(
        isVisible = showChoosePlaylistDialog,
        onGetSong = { playlist ->
            database.transaction {
                insert(mediaMetadata)
            }
            coroutineScope.launch(Dispatchers.IO) {
                playlist.playlist.browseId?.let { YouTube.addToPlaylist(it, mediaMetadata.id) }
            }
            listOf(mediaMetadata.id)
        },
        onDismiss = {
            showChoosePlaylistDialog = false
        }
    )

    var showSelectArtistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showSelectArtistDialog) {
        val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
        val sheetContentColor = MaterialTheme.colorScheme.onSurface
        val scrollState = androidx.compose.foundation.lazy.rememberLazyListState()

        val isScrolled by remember { androidx.compose.runtime.derivedStateOf { scrollState.canScrollBackward } }
        val headerAlpha by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isScrolled) 1f else 0f, 
            label = "headerAlpha"
        )

        var isControlsVisible by remember { mutableStateOf(true) }
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    if (available.y < -5f) { 
                        isControlsVisible = false
                    } else if (available.y > 5f) { 
                        isControlsVisible = true
                    }
                    return Offset.Zero
                }
            }
        }

        Dialog(
            onDismissRequest = { showSelectArtistDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = sheetBackgroundColor,
                contentColor = sheetContentColor
            ) {
                 Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                         contentPadding = PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 80.dp,
                            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 24.dp,
                            start = 24.dp,
                            end = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(artists) { artist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer) // Use container for card look without actual Card elevation issues in Dialog
                                    .clickable {
                                        navController.navigate("artist/${artist.id}")
                                        showSelectArtistDialog = false
                                        playerBottomSheetState.collapseSoft()
                                        onDismiss()
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.artist),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = artist.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sheetContentColor,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.navigate_next),
                                    contentDescription = null,
                                    tint = sheetContentColor.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Header Background (Foggy Blur)
                    AnimatedVisibility(
                        visible = isControlsVisible,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                    ) {
                        if (headerAlpha > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp) // Adjusted height
                                    .alpha(headerAlpha)
                                    .then(
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                            Modifier.graphicsLayer {
                                                renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                                    25f,
                                                    25f,
                                                    android.graphics.Shader.TileMode.CLAMP
                                                ).asComposeRenderEffect()
                                            }
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            colors = listOf(
                                                sheetBackgroundColor.copy(alpha = 0.98f),
                                                sheetBackgroundColor.copy(alpha = 0.95f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    // Header
                    AnimatedVisibility(
                        visible = isControlsVisible,
                        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                        modifier = Modifier.align(Alignment.TopCenter).zIndex(2f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(WindowInsets.systemBars.asPaddingValues())
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Select Artist",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = sheetContentColor
                            )
                            FilledTonalIconButton(
                                onClick = { showSelectArtistDialog = false },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    contentColor = sheetContentColor
                                )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close),
                                    contentDescription = "Close",
                                )
                            }
                        }
                    }
                 }
            }
        }
    }

    var showPitchTempoDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showPitchTempoDialog) {
        TempoPitchDialog(
            onDismiss = { showPitchTempoDialog = false },
        )
    }

    var showAdvancedDownloadDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showAdvancedDownloadDialog) {
        AdvancedDownloadDialog(
            mediaMetadata = mediaMetadata,
            onDismiss = { showAdvancedDownloadDialog = false }
        )
    }

    var showDetailsDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showDetailsDialog) {
        DetailsDialog(
            mediaMetadata = mediaMetadata,
            onDismiss = { showDetailsDialog = false }
        )
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentPadding = PaddingValues(
            bottom = 89.dp + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
        ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        if (isQueueTrigger != true) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                ) {
                    // Volume Control Container
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .border(BorderStroke(1.dp, Color.White), CircleShape)
                                .padding(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.volume_up),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        BigSeekBar(
                            progressProvider = playerVolume::value,
                            onProgressChange = { playerConnection.service.playerVolume.value = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                        )
                    }
                }
            }
        }
        
        item {
             // Quick Actions Container
             // Quick Actions Container
             NewActionGrid(
                actions = listOf(
                    NewAction(
                        icon = {
                            when (download?.state) {
                                Download.STATE_COMPLETED -> Icon(
                                    painter = painterResource(R.drawable.offline),
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> CircularProgressIndicator(
                                    modifier = Modifier.size(26.dp),
                                    strokeWidth = 2.dp
                                )
                                else -> Icon(
                                    painter = painterResource(R.drawable.download),
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        text = when (download?.state) {
                            Download.STATE_COMPLETED -> stringResource(R.string.remove_download)
                            Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> stringResource(R.string.downloading)
                            else -> stringResource(R.string.action_download)
                        },
                        onClick = {
                            when (download?.state) {
                                Download.STATE_COMPLETED, Download.STATE_QUEUED, Download.STATE_DOWNLOADING -> {
                                    DownloadService.sendRemoveDownload(
                                        context,
                                        ExoDownloadService::class.java,
                                        mediaMetadata.id,
                                        false,
                                    )
                                }
                                else -> {
                                    database.transaction {
                                        insert(mediaMetadata)
                                    }
                                    val downloadRequest = DownloadRequest
                                        .Builder(mediaMetadata.id, mediaMetadata.id.toUri())
                                        .setCustomCacheKey(mediaMetadata.id)
                                        .setData(mediaMetadata.title.toByteArray())
                                        .build()
                                    DownloadService.sendAddDownload(
                                        context,
                                        ExoDownloadService::class.java,
                                        downloadRequest,
                                        false,
                                    )
                                }
                            }
                        }
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.playlist_add),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        text = stringResource(R.string.add_to_playlist),
                        onClick = { showChoosePlaylistDialog = true }
                    ),
                    NewAction(
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.share),
                                contentDescription = null,
                                modifier = Modifier.size(26.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        text = "Share",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://music.youtube.com/watch?v=${mediaMetadata.id}")
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                            onDismiss()
                        }
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        if (artists.isNotEmpty()) {
            item {
                MenuGroup {
                     MenuEntry(
                        icon = R.drawable.bedtime,
                        text = "Ambient Mode",
                        onClick = {
                            navController.navigate("ambient_mode")
                            onDismiss()
                        }
                    )
                    MenuDivider()
                     MenuEntry(
                        icon = R.drawable.radio,
                        text = stringResource(R.string.start_radio),
                        onClick = {
                            playerConnection.playQueue(YouTubeQueue.radio(mediaMetadata))
                            onDismiss()
                        }
                    )
                    MenuDivider()
                     MenuEntry(
                        icon = R.drawable.artist,
                        text = stringResource(R.string.view_artist),
                        onClick = {
                            if (mediaMetadata.artists.size == 1) {
                                navController.navigate("artist/${mediaMetadata.artists[0].id}")
                                playerBottomSheetState.collapseSoft()
                                onDismiss()
                            } else {
                                showSelectArtistDialog = true
                            }
                        }
                    )
                     if (mediaMetadata.album != null) {
                         MenuDivider()
                        MenuEntry(
                            icon = R.drawable.album,
                            text = stringResource(R.string.view_album),
                            onClick = {
                                navController.navigate("album/${mediaMetadata.album.id}")
                                playerBottomSheetState.collapseSoft()
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
        
        item {
            MenuGroup {
                MenuEntry(
                    icon = R.drawable.info,
                    text = stringResource(R.string.details),
                    onClick = {
                        showDetailsDialog = true
                    }
                )
                MenuDivider()
                 MenuEntry(
                    icon = R.drawable.download,
                    text = "Advance Download",
                    onClick = {
                        showAdvancedDownloadDialog = true
                    }
                )
            }
        }

        if (isQueueTrigger != true) {
             item {
                MenuGroup {
                    MenuEntry(
                        icon = R.drawable.equalizer,
                        text = stringResource(R.string.equalizer),
                        onClick = {
                            val intent =
                                Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                                    putExtra(
                                        AudioEffect.EXTRA_AUDIO_SESSION,
                                        playerConnection.player.audioSessionId,
                                    )
                                    putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                                    putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                                }
                            if (intent.resolveActivity(context.packageManager) != null) {
                                activityResultLauncher.launch(intent)
                            }
                            onDismiss()
                        }
                    )
                    MenuDivider()
                    MenuEntry(
                        icon = R.drawable.tune,
                        text = stringResource(R.string.advanced),
                        onClick = {
                            showPitchTempoDialog = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuGroup(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        content()
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
        modifier = Modifier.padding(horizontal = 20.dp)
    )
}

@Composable
private fun MenuEntry(
    @DrawableRes icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun TempoPitchDialog(onDismiss: () -> Unit) {
    val playerConnection = LocalPlayerConnection.current ?: return
    var tempo by remember {
        mutableFloatStateOf(playerConnection.player.playbackParameters.speed)
    }
    var transposeValue by remember {
        mutableIntStateOf(round(12 * log2(playerConnection.player.playbackParameters.pitch)).toInt())
    }
    val updatePlaybackParameters = {
        playerConnection.player.playbackParameters =
            PlaybackParameters(tempo, 2f.pow(transposeValue.toFloat() / 12))
    }

    val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    val sheetContentColor = MaterialTheme.colorScheme.onSurface
    val scrollState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Header Background Animation
    val isScrolled by remember { androidx.compose.runtime.derivedStateOf { scrollState.canScrollBackward } }
    val headerAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f, 
        label = "headerAlpha"
    )

    var isControlsVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -5f) { 
                    isControlsVisible = false
                } else if (available.y > 5f) { 
                    isControlsVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = sheetBackgroundColor,
            contentColor = sheetContentColor
        ) {
            Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
                
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 80.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 80.dp, // Space for bottom buttons
                        start = 24.dp,
                        end = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "TEMPO",
                                style = MaterialTheme.typography.labelMedium,
                                color = sheetContentColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            ValueAdjuster(
                                icon = R.drawable.speed,
                                title = "Tempo",
                                currentValue = tempo,
                                values = (0..35).map { round((0.25f + it * 0.05f) * 100) / 100 },
                                onValueUpdate = {
                                    tempo = it
                                    updatePlaybackParameters()
                                },
                                valueText = { "x$it" }
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(24.dp)
                        ) {
                            Text(
                                text = "PITCH",
                                style = MaterialTheme.typography.labelMedium,
                                color = sheetContentColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            ValueAdjuster(
                                icon = R.drawable.discover_tune,
                                title = "Pitch",
                                currentValue = transposeValue,
                                values = (-12..12).toList(),
                                onValueUpdate = {
                                    transposeValue = it
                                    updatePlaybackParameters()
                                },
                                valueText = { "${if (it > 0) "+" else ""}$it" },
                            )
                        }
                    }

                    
                    item {
                        Spacer(Modifier.height(100.dp))
                    }
                }

                // Header Background (Foggy Blur)
                AnimatedVisibility(
                    visible = isControlsVisible,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(1f)
                ) {
                    if (headerAlpha > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .alpha(headerAlpha)
                                .then(
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        Modifier.graphicsLayer {
                                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                                25f,
                                                25f,
                                                android.graphics.Shader.TileMode.CLAMP
                                            ).asComposeRenderEffect()
                                        }
                                    } else {
                                        Modifier
                                    }
                                )
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            sheetBackgroundColor.copy(alpha = 0.98f),
                                            sheetBackgroundColor.copy(alpha = 0.95f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                }

                // Header
                AnimatedVisibility(
                    visible = isControlsVisible,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(2f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(WindowInsets.systemBars.asPaddingValues())
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.tempo_and_pitch),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = sheetContentColor
                        )
                        FilledTonalIconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = sheetContentColor
                            )
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = "Close",
                            )
                        }
                    }
                }
                
                // Bottom Buttons
                AnimatedVisibility(
                    visible = isControlsVisible,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 24.dp)
                        .padding(horizontal = 24.dp)
                ) {
                     Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                tempo = 1f
                                transposeValue = 0
                                updatePlaybackParameters()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = sheetContentColor
                            )
                        ) {
                            Text(
                                stringResource(R.string.reset),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                "Done",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> ValueAdjuster(
    @DrawableRes icon: Int,
    title: String,
    currentValue: T,
    values: List<T>,
    onValueUpdate: (T) -> Unit,
    valueText: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                     text = valueText(currentValue),
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHigh, CircleShape).padding(4.dp),
        ) {
            IconButton(
                enabled = currentValue != values.first(),
                onClick = {
                    onValueUpdate(values[values.indexOf(currentValue) - 1])
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove),
                    contentDescription = null,
                )
            }

            Text(
                text = valueText(currentValue),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(80.dp),
                fontWeight = FontWeight.Bold
            )

            IconButton(
                enabled = currentValue != values.last(),
                onClick = {
                    onValueUpdate(values[values.indexOf(currentValue) + 1])
                },
                 modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = null,
                )
            }
        }
    }
}
