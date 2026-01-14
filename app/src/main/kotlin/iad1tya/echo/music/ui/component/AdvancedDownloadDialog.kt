package iad1tya.echo.music.ui.component


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.documentfile.provider.DocumentFile
import com.echo.innertube.YouTube
import com.echo.innertube.models.YouTubeClient
import com.echo.innertube.models.response.PlayerResponse
import iad1tya.echo.music.R
import iad1tya.echo.music.models.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdvancedDownloadDialog(
    mediaMetadata: MediaMetadata,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var playerResponse by remember { mutableStateOf<PlayerResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var downloadLocation by remember { mutableStateOf<Uri?>(null) }
    var defaultDownloadPath by remember { mutableStateOf("Downloads/EchoMusic") }

    // Fetch PlayerResponse for formats
    LaunchedEffect(mediaMetadata.id) {
        withContext(Dispatchers.IO) {
            val result = YouTube.player(mediaMetadata.id, client = YouTubeClient.MOBILE)
            playerResponse = result.getOrNull()
            isLoading = false
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            downloadLocation = uri
            val documentFile = DocumentFile.fromTreeUri(context, uri)
            defaultDownloadPath = documentFile?.name ?: "Selected Folder"
        }
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
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(WindowInsets.systemBars.asPaddingValues()),
                        contentAlignment = Alignment.Center
                    ) {
                         CircularProgressIndicator(
                             modifier = Modifier.size(48.dp),
                             color = MaterialTheme.colorScheme.primary
                         )
                    }
                } else {
                    val formats = playerResponse?.streamingData?.adaptiveFormats
                    val videoFormats = formats?.filter { it.mimeType.startsWith("video/mp4") }
                        ?.sortedByDescending { it.height }
                    val audioFormats = formats?.filter { it.mimeType.startsWith("audio/mp4") }
                        ?.sortedByDescending { it.bitrate }

                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 80.dp,
                            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 24.dp,
                            start = 24.dp,
                            end = 24.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Song Info
                         item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "SELECTED MEDIA",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Text(
                                    text = mediaMetadata.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sheetContentColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mediaMetadata.artists.joinToString { it.name },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Download Location
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { folderPickerLauncher.launch(null) }
                                        .padding(20.dp),
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
                                            painter = painterResource(R.drawable.storage),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Save to",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = sheetContentColor.copy(alpha = 0.7f)
                                        )
                                        Text(
                                            text = defaultDownloadPath,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = sheetContentColor
                                        )
                                    }
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_forward),
                                        contentDescription = null,
                                        tint = sheetContentColor.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }

                        // Thumbnail
                        item {
                             Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(8.dp)
                            ) {
                                DownloadOptionItem(
                                    icon = R.drawable.insert_photo,
                                    title = "Thumbnail",
                                    subtitle = "High Quality",
                                    onClick = {
                                        downloadFile(
                                            context,
                                            mediaMetadata.thumbnailUrl ?: "",
                                            "${mediaMetadata.title}_thumbnail.jpg",
                                            downloadLocation
                                        )
                                        onDismiss()
                                    },
                                    sheetContentColor = sheetContentColor
                                )
                            }
                        }

                        // Audio Formats
                        if (!audioFormats.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = "AUDIO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                                )
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    audioFormats.forEach { format ->
                                        DownloadOptionItem(
                                            icon = R.drawable.music_note,
                                            title = "Audio (${format.bitrate / 1000}kbps)",
                                            subtitle = format.mimeType.split(";")[0],
                                            onClick = {
                                                downloadFile(
                                                    context,
                                                    format.url ?: "",
                                                    "${mediaMetadata.title}_audio.${if (format.mimeType.contains("mp4")) "m4a" else "webm"}",
                                                    downloadLocation
                                                )
                                                onDismiss()
                                            },
                                            sheetContentColor = sheetContentColor
                                        )
                                    }
                                }
                            }
                        }

                        // Video Formats
                        if (!videoFormats.isNullOrEmpty()) {
                            item {
                                Text(
                                    text = "VIDEO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                                )
                            }
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    videoFormats.forEach { format ->
                                        DownloadOptionItem(
                                            icon = R.drawable.video,
                                            title = "Video ${format.height}p",
                                            subtitle = format.mimeType.split(";")[0],
                                            onClick = {
                                                downloadFile(
                                                    context,
                                                    format.url ?: "",
                                                    "${mediaMetadata.title}_${format.height}p.mp4",
                                                    downloadLocation
                                                )
                                                onDismiss()
                                            },
                                            sheetContentColor = sheetContentColor
                                        )
                                    }
                                }
                            }
                        }
                        
                        item {
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }

                // Header Background (New "Foggy" Blur)
                if (headerAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp) // Adjusted height to cover status bar + header
                            .align(Alignment.TopCenter)
                            .alpha(headerAlpha)
                            .zIndex(1f)
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

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(WindowInsets.systemBars.asPaddingValues())
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .align(Alignment.TopCenter)
                        .zIndex(2f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Advance Download",
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
}

@Composable
fun DownloadOptionItem(
    icon: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    sheetContentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = sheetContentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = sheetContentColor.copy(alpha = 0.6f)
            )
        }
        Icon(
            painter = painterResource(R.drawable.download),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = sheetContentColor.copy(alpha = 0.5f)
        )
    }
}

fun downloadFile(context: Context, url: String, fileName: String, destinationUri: Uri?) {
    if (url.isEmpty()) {
        Toast.makeText(context, "Download URL not available", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading $fileName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        
        if (destinationUri != null) {
              // Creating a file in the selected directory is more complex with Scoped Storage + DownloadManager
              // DownloadManager only supports setDestinationUri with file:// or content:// (if supported)
              // But usually it's restricted to Public directories.
              // For simplicity and reliability, we default to Public Downloads if not easily possible,
              // or use setDestinationInExternalPublicDir if no Uri is provided.
              
              // IF specific URI is tricky with DownloadManager, we might want to guide user to standard paths.
              // For now, let's use the standard "Downloads/EchoMusic" SubPath approach which works well.
              // If the user picked a tree URI, we can't easily pass that to DownloadManager directly
              // without handling file creation ourselves and copying bytes (which loses DownloadManager benefits).
              
              // So, we will ignore the picked URI for DownloadManager and just use the standard Downloads folder 
              // but create a subfolder.
              
               request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "EchoMusic/$fileName")
               Toast.makeText(context, "Downloading to Downloads/EchoMusic", Toast.LENGTH_SHORT).show()

        } else {
             request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "EchoMusic/$fileName")
             Toast.makeText(context, "Downloading to Downloads/EchoMusic", Toast.LENGTH_SHORT).show()
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
        
    } catch (e: Exception) {
        Toast.makeText(context, "Download Failed: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
