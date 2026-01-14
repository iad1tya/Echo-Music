package iad1tya.echo.music.ui.component

import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.documentfile.provider.DocumentFile
import com.echo.innertube.YouTube
import com.echo.innertube.models.YouTubeClient
import com.echo.innertube.utils.completed
import iad1tya.echo.music.R
import iad1tya.echo.music.models.MediaMetadata
import iad1tya.echo.music.models.toMediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdvancedPlaylistDownloadDialog(
    songs: List<MediaMetadata>,
    playlistId: String? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var songsToDownload by remember { mutableStateOf(songs) }
    var isFetchingSongs by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (songsToDownload.isEmpty() && playlistId != null) {
            isFetchingSongs = true
            withContext(Dispatchers.IO) {
                try {
                    val result = YouTube.playlist(playlistId).completed().getOrNull()
                    val fetchedSongs = result?.songs?.map { it.toMediaMetadata() }
                    withContext(Dispatchers.Main) {
                        if (fetchedSongs != null) {
                            songsToDownload = fetchedSongs
                        } else {
                             Toast.makeText(context, "Failed to fetch playlist songs", Toast.LENGTH_SHORT).show()
                             onDismiss()
                        }
                        isFetchingSongs = false
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Error fetching playlist: ${e.message}", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                }
            }
        }
    }
    
    var downloadLocation by remember { mutableStateOf<Uri?>(null) }
    var defaultDownloadPath by remember { mutableStateOf("Downloads/EchoMusic") }
    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var progressText by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(DownloadType.Audio) }

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

    Dialog(
        onDismissRequest = {
             if (!isDownloading && !isFetchingSongs) onDismiss()
        },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Playlist Download",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = sheetContentColor
                    )
                    FilledTonalIconButton(
                        onClick = { 
                            if (!isDownloading) onDismiss() 
                        },
                        enabled = !isDownloading,
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

                Spacer(Modifier.height(24.dp))

                if (isFetchingSongs) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                             CircularProgressIndicator(
                                 modifier = Modifier.size(48.dp),
                                 color = MaterialTheme.colorScheme.primary
                             )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text(
                                 "Fetching playlist info...",
                                 style = MaterialTheme.typography.bodyLarge,
                                 color = sheetContentColor.copy(alpha = 0.7f)
                             )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Info Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .padding(20.dp)
                        ) {
                             Text(
                                text = "SUMMARY",
                                style = MaterialTheme.typography.labelMedium,
                                color = sheetContentColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.library_music),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${songsToDownload.size} songs selected",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (isDownloading) {
                             Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = "DOWNLOADING",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = progressText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor
                                )
                            }
                        } else {
                            // Settings Section
                             Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                // Location Item
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
                                
                                // Divider could go here if needed, but spacing is usually cleaner
                                
                                // Format Selection
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 20.dp)
                                ) {
                                     Text(
                                        text = "FORMAT",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = sheetContentColor.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        FilterChip(
                                            selected = selectedFormat == DownloadType.Audio,
                                            onClick = { selectedFormat = DownloadType.Audio },
                                            label = "Audio",
                                            icon = R.drawable.music_note,
                                            modifier = Modifier.weight(1f)
                                        )
                                        FilterChip(
                                            selected = selectedFormat == DownloadType.Video,
                                            onClick = { selectedFormat = DownloadType.Video },
                                            label = "Video",
                                            icon = R.drawable.video,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Start Button
                     if (!isDownloading && songsToDownload.isNotEmpty()) {
                        Button(
                            onClick = {
                                isDownloading = true
                                coroutineScope.launch(Dispatchers.IO) {
                                    var successCount = 0
                                    songsToDownload.forEachIndexed { index, song ->
                                        progress = (index.toFloat() / songsToDownload.size.toFloat())
                                        withContext(Dispatchers.Main) {
                                            progressText = "Fetching ${index + 1}/${songsToDownload.size}: ${song.title}"
                                        }

                                        try {
                                            val result = YouTube.player(song.id, client = YouTubeClient.MOBILE)
                                            val playerResponse = result.getOrNull()
                                            val formats = playerResponse?.streamingData?.adaptiveFormats

                                            val url = if (selectedFormat == DownloadType.Audio) {
                                                formats?.filter { it.mimeType.startsWith("audio/mp4") }
                                                    ?.maxByOrNull { it.bitrate ?: 0 }?.url
                                            } else {
                                                formats?.filter { it.mimeType.startsWith("video/mp4") }
                                                    ?.maxByOrNull { it.height ?: 0 }?.url
                                            }

                                            if (!url.isNullOrEmpty()) {
                                                val extension = if (selectedFormat == DownloadType.Audio) "m4a" else "mp4"
                                                val fileName = "${song.title.replace("/", "_")}.$extension"
                                                
                                                withContext(Dispatchers.Main) {
                                                    downloadFile(context, url, fileName, downloadLocation)
                                                }
                                                successCount++
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    
                                    withContext(Dispatchers.Main) {
                                        progress = 1f
                                        progressText = "Done! queued $successCount downloads."
                                        Toast.makeText(context, "Queued $successCount downloads", Toast.LENGTH_LONG).show()
                                        onDismiss()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(32.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                "Start Download",
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
private fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(50.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class DownloadType {
    Audio,
    Video
}
