package iad1tya.echo.music.ui.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Build
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.echo.innertube.YouTube
import com.echo.innertube.models.MediaInfo
import iad1tya.echo.music.LocalDatabase
import iad1tya.echo.music.R
import iad1tya.echo.music.db.entities.FormatEntity
import iad1tya.echo.music.db.entities.Song
import iad1tya.echo.music.models.MediaMetadata
import iad1tya.echo.music.models.toMediaMetadata
import iad1tya.echo.music.ui.component.LocalBottomSheetPageState
import kotlinx.coroutines.flow.collect

@Composable
fun DetailsDialog(
    mediaMetadata: MediaMetadata,
    onDismiss: () -> Unit
) {
    val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    val sheetContentColor = MaterialTheme.colorScheme.onSurface

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
            DetailsContent(mediaMetadata, onDismiss)
        }
    }
}

@Composable
fun ShowMediaInfo(videoId: String) {
    val database = LocalDatabase.current
    var song by remember { mutableStateOf<Song?>(null) }
    val bottomSheetState = LocalBottomSheetPageState.current

    LaunchedEffect(videoId) {
        database.song(videoId).collect { song = it }
    }

    if (song != null) {
        DetailsContent(
            mediaMetadata = song!!.toMediaMetadata(),
            onDismiss = { bottomSheetState.dismiss() }
        )
    }
}

@Composable
fun DetailsContent(
    mediaMetadata: MediaMetadata,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    
    var info by remember { mutableStateOf<MediaInfo?>(null) }
    var currentFormat by remember { mutableStateOf<FormatEntity?>(null) }
    
    LaunchedEffect(mediaMetadata.id) {
        YouTube.getMediaInfo(mediaMetadata.id).onSuccess { info = it }
    }
    LaunchedEffect(mediaMetadata.id) {
        database.format(mediaMetadata.id).collect { currentFormat = it }
    }

    val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    val sheetContentColor = MaterialTheme.colorScheme.onSurface
    val scrollState = rememberLazyListState()

    val isScrolled by remember { derivedStateOf { scrollState.canScrollBackward } }
    val headerAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f, 
        label = "headerAlpha"
    )

    var isControlsVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
                if (available.y < -5f) { 
                    isControlsVisible = false
                } else if (available.y > 5f) { 
                    isControlsVisible = true
                }
                return androidx.compose.ui.geometry.Offset.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
            // Background Image with Blur
            if (!mediaMetadata.thumbnailUrl.isNullOrEmpty()) {
            AsyncImage(
                model = mediaMetadata.thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.2f)
                    .then(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            Modifier.graphicsLayer {
                                renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                    100f,
                                    100f,
                                    android.graphics.Shader.TileMode.CLAMP
                                ).asComposeRenderEffect()
                            }
                        } else {
                            Modifier
                        }
                    )
            )
        }

        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 80.dp,
                bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- HEADER: Album Art & Title ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.size(180.dp)
                    ) {
                        AsyncImage(
                            model = mediaMetadata.thumbnailUrl,
                            contentDescription = "Album Art",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = mediaMetadata.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().basicMarquee(),
                        color = sheetContentColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mediaMetadata.artists.joinToString { it.name },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // --- SECTION 1: TECHNICAL INFO ---
            if (currentFormat != null) {
                item {
                    InfoContainer(title = stringResource(R.string.details)) {
                        InfoRow(
                            painter = painterResource(R.drawable.music_note),
                            label = stringResource(R.string.mime_type),
                            value = currentFormat?.mimeType
                        )
                        InfoRow(
                            painter = painterResource(R.drawable.integration),
                            label = stringResource(R.string.codecs),
                            value = currentFormat?.codecs
                        )
                        InfoRow(
                            painter = painterResource(R.drawable.speed),
                            label = stringResource(R.string.bitrate),
                            value = currentFormat?.bitrate?.let { "${it / 1000} Kbps" }
                        )
                        InfoRow(
                            painter = painterResource(R.drawable.graphic_eq),
                            label = stringResource(R.string.sample_rate),
                            value = currentFormat?.sampleRate?.let { "$it Hz" }
                        )
                        InfoRow(
                            painter = painterResource(R.drawable.volume_up),
                            label = stringResource(R.string.loudness),
                            value = currentFormat?.loudnessDb?.let { "$it dB" }
                        )
                        InfoRow(
                            painter = painterResource(R.drawable.storage),
                            label = stringResource(R.string.file_size),
                            value = currentFormat?.contentLength?.let {
                                Formatter.formatShortFileSize(context, it)
                            }
                        )
                    }
                }
            }

            // --- SECTION 2: STATISTICS ---
            if (info != null) {
                item {
                    InfoContainer(title = stringResource(R.string.numbers)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                painter = painterResource(R.drawable.stats),
                                label = stringResource(R.string.views),
                                value = info?.viewCount?.toInt()?.let { compactNumberFormatter(it) }
                            )
                            StatItem(
                                painter = painterResource(R.drawable.favorite_border),
                                label = stringResource(R.string.likes),
                                value = info?.like?.toInt()?.let { compactNumberFormatter(it) }
                            )
                        }
                    }
                }

                // --- SECTION 3: DESCRIPTION ---
                if (!info?.description.isNullOrBlank()) {
                    item {
                            InfoContainer(title = stringResource(R.string.description)) {
                                Text(
                                    text = info?.description ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                    }
                }
            }

            // --- SECTION 4: METADATA ---
            item {
                InfoContainer(title = "Metadata") {
                        InfoRow(
                        painter = painterResource(R.drawable.key),
                        label = stringResource(R.string.media_id),
                        value = mediaMetadata.id
                    )
                        if (currentFormat?.itag != null) {
                            InfoRow(
                                painter = painterResource(R.drawable.bookmark),
                                label = "Itag",
                                value = currentFormat?.itag.toString()
                            )
                        }
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
                    "Details",
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
    }
}

@Composable
fun InfoContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(24.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
fun InfoRow(
    painter: Painter,
    label: String,
    value: String?
) {
    val context = LocalContext.current
    if (value.isNullOrBlank()) return

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, value)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
            }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatItem(
    painter: Painter,
    label: String,
    value: String?
) {
    if (value == null) return
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun compactNumberFormatter(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 1000000 -> String.format("%.1fK", count / 1000.0)
        else -> String.format("%.1fM", count / 1000000.0)
    }
}

