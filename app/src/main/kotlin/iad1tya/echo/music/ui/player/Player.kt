package iad1tya.echo.music.ui.player

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.os.Build
import android.widget.Toast
import androidx.core.content.ContextCompat
import iad1tya.echo.music.ui.component.PlatformBackdrop
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.mediarouter.media.MediaRouter
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaControlIntent
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import iad1tya.echo.music.ui.component.DefaultDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.palette.graphics.Palette
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import iad1tya.echo.music.LocalDownloadUtil
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.DarkModeKey
import iad1tya.echo.music.constants.UseNewPlayerDesignKey
import iad1tya.echo.music.constants.PlayerBackgroundStyle
import iad1tya.echo.music.constants.PlayerBackgroundStyleKey
import iad1tya.echo.music.constants.PlayerButtonsStyle
import iad1tya.echo.music.constants.PlayerButtonsStyleKey
import iad1tya.echo.music.ui.theme.PlayerColorExtractor
import iad1tya.echo.music.ui.theme.PlayerSliderColors
import iad1tya.echo.music.constants.PlayerHorizontalPadding
import iad1tya.echo.music.constants.QueuePeekHeight
import iad1tya.echo.music.constants.SliderStyle
import iad1tya.echo.music.constants.SliderStyleKey
import iad1tya.echo.music.extensions.togglePlayPause
import iad1tya.echo.music.extensions.toggleRepeatMode
import iad1tya.echo.music.models.MediaMetadata
import iad1tya.echo.music.ui.component.BottomSheet
import iad1tya.echo.music.ui.component.BottomSheetState
import iad1tya.echo.music.ui.component.AnimatedGradientBackground
import iad1tya.echo.music.ui.component.Lyrics
import iad1tya.echo.music.ui.component.QueueContent
import iad1tya.echo.music.ui.component.LocalBottomSheetPageState
import iad1tya.echo.music.ui.component.LocalMenuState
import iad1tya.echo.music.ui.component.PlayerSliderTrack
import iad1tya.echo.music.ui.component.ResizableIconButton
import iad1tya.echo.music.ui.component.rememberBottomSheetState
import iad1tya.echo.music.ui.menu.PlayerMenu
import iad1tya.echo.music.ui.screens.settings.DarkMode
import iad1tya.echo.music.ui.utils.ShowMediaInfo
import iad1tya.echo.music.utils.makeTimeString
import iad1tya.echo.music.utils.rememberEnumPreference
import iad1tya.echo.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.squiggles.SquigglySlider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    modifier: Modifier = Modifier,
    pureBlack: Boolean,
    backdrop: iad1tya.echo.music.ui.component.PlatformBackdrop? = null,
    layer: androidx.compose.ui.graphics.layer.GraphicsLayer? = null,
    luminance: Float = 0f,
) {

    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current
    val bottomSheetPageState = LocalBottomSheetPageState.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val (useNewPlayerDesign, onUseNewPlayerDesignChange) = rememberPreference(
        UseNewPlayerDesignKey,
        defaultValue = false
    )
    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT
    )
    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    val onBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.secondary
        else ->
            if (useDarkTheme)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onPrimary
    }
    val useBlackBackground =
        remember(isSystemInDarkTheme, darkTheme, pureBlack) {
            val useDarkTheme =
                if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
            useDarkTheme && pureBlack
        }

    val playbackState by playerConnection.playbackState.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val automix by playerConnection.service.automixItems.collectAsState()
    val repeatMode by playerConnection.repeatMode.collectAsState()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()
    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.DEFAULT)

    var position by rememberSaveable(playbackState) {
        mutableLongStateOf(playerConnection.player.currentPosition)
    }
    var duration by rememberSaveable(playbackState) {
        mutableLongStateOf(playerConnection.player.duration)
    }
    var sliderPosition by remember {
        mutableStateOf<Long?>(null)
    }
    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }
    var showAudioRoutingDialog by remember { mutableStateOf(false) }
    
    val audioRoutingSheetState = rememberBottomSheetState(
        dismissedBound = 0.dp,
        expandedBound = state.expandedBound,
        collapsedBound = 0.dp,
        initialAnchor = 1
    )

    if (!canSkipNext && automix.isNotEmpty()) {
        playerConnection.service.addToQueueAutomix(automix[0], 0)
    }

    // Detect if thumbnail is rectangular (video) or square (audio-only)
    var isRectangularThumbnail by remember { mutableStateOf(false) }
    
    LaunchedEffect(mediaMetadata?.thumbnailUrl) {
        mediaMetadata?.thumbnailUrl?.let { thumbnailUrl ->
            try {
                val imageLoader = coil3.ImageLoader.Builder(context).build()
                val request = ImageRequest.Builder(context)
                    .data(thumbnailUrl)
                    .build()
                
                val result = imageLoader.execute(request)
                if (result is coil3.request.SuccessResult) {
                    val image = result.image
                    val width = image.width
                    val height = image.height
                    
                    // Check if aspect ratio is closer to 16:9 (1.77) than 1:1 (square)
                    // Consider it rectangular if aspect ratio > 1.3
                    val aspectRatio = width.toFloat() / height.toFloat()
                    isRectangularThumbnail = aspectRatio > 1.3f
                }
            } catch (e: Exception) {
                // If we can't load, default to false
                isRectangularThumbnail = false
            }
        }
    }

    val defaultGradientColors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()

    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT) {
            val currentMetadata = mediaMetadata
            if (currentMetadata != null && currentMetadata.thumbnailUrl != null) {
                val cachedColors = gradientColorsCache[currentMetadata.id]
                if (cachedColors != null) {
                    gradientColors = cachedColors
                    return@LaunchedEffect
                }
                withContext(Dispatchers.IO) {
                    val request = ImageRequest.Builder(context)
                        .data(currentMetadata.thumbnailUrl)
                        .size(100, 100)
                        .allowHardware(false)
                        .memoryCacheKey("gradient_${currentMetadata.id}")
                        .build()

                    val result = runCatching { context.imageLoader.execute(request) }.getOrNull()
                    if (result != null) {
                        val bitmap = result.image?.toBitmap()
                        if (bitmap != null) {
                            val palette = withContext(Dispatchers.Default) {
                                Palette.from(bitmap)
                                    .maximumColorCount(8)
                                    .resizeBitmapArea(100 * 100)
                                    .generate()
                            }
                            val extractedColors = PlayerColorExtractor.extractRichGradientColors(
                                palette = palette,
                                fallbackColor = fallbackColor
                            )
                            gradientColorsCache[currentMetadata.id] = extractedColors
                            withContext(Dispatchers.Main) { gradientColors = extractedColors }
                        }
                    }
                }
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val TextBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
            PlayerBackgroundStyle.GRADIENT -> Color.White
        }

    val icBackgroundColor =
        when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.surface
            PlayerBackgroundStyle.GRADIENT -> Color.Black
        }

    val (textButtonColor, iconButtonColor) = when (playerButtonsStyle) {
        PlayerButtonsStyle.DEFAULT -> Pair(TextBackgroundColor, icBackgroundColor)
        PlayerButtonsStyle.SECONDARY -> Pair(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
    }

    val download by LocalDownloadUtil.current.getDownload(mediaMetadata?.id ?: "")
        .collectAsState(initial = null)

    val sleepTimerEnabled =
        remember(
            playerConnection.service.sleepTimer.triggerTime,
            playerConnection.service.sleepTimer.pauseWhenSongEnd
        ) {
            playerConnection.service.sleepTimer.isActive
        }

    var sleepTimerTimeLeft by remember {
        mutableLongStateOf(0L)
    }

    LaunchedEffect(sleepTimerEnabled) {
        if (sleepTimerEnabled) {
            while (isActive) {
                sleepTimerTimeLeft =
                    if (playerConnection.service.sleepTimer.pauseWhenSongEnd) {
                        playerConnection.player.duration - playerConnection.player.currentPosition
                    } else {
                        playerConnection.service.sleepTimer.triggerTime - System.currentTimeMillis()
                    }
                delay(1000L)
            }
        }
    }

    var showSleepTimerDialog by remember {
        mutableStateOf(false)
    }

    var sleepTimerValue by remember {
        mutableFloatStateOf(30f)
    }
    if (showSleepTimerDialog) {
        iad1tya.echo.music.ui.component.SleepTimerDialog(
            onDismiss = { showSleepTimerDialog = false }
        )
    }

    var showChoosePlaylistDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(playbackState) {
        if (playbackState == STATE_READY) {
            while (isActive) {
                delay(500)
                position = playerConnection.player.currentPosition
                duration = playerConnection.player.duration
            }
        }
    }

    val dismissedBound = QueuePeekHeight + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    val queueSheetState = rememberBottomSheetState(
        dismissedBound = dismissedBound,
        expandedBound = state.expandedBound,
        collapsedBound = dismissedBound + 1.dp,
        initialAnchor = 1
    )

    var showLyrics by rememberSaveable { mutableStateOf(false) }
    var showQueue by rememberSaveable { mutableStateOf(false) }

    // Collapse the old queue bottom sheet when showing queue overlay
    LaunchedEffect(showQueue) {
        if (showQueue) {
            queueSheetState.collapseSoft()
        }
    }

    val bottomSheetBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.GRADIENT -> 
            MaterialTheme.colorScheme.surfaceContainer
        else -> 
            if (useBlackBackground) Color.Black 
            else MaterialTheme.colorScheme.surfaceContainer
    }

    val backgroundAlpha = state.progress.coerceIn(0f, 1f)

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bottomSheetBackgroundColor)
            ) {
                when (playerBackground) {
                    PlayerBackgroundStyle.GRADIENT -> {
                        AnimatedContent(
                            targetState = gradientColors,
                            transitionSpec = {
                                fadeIn(tween(800)).togetherWith(fadeOut(tween(800)))
                            },
                            label = "gradientBackground"
                        ) { colors ->
                            if (colors.isNotEmpty()) {
                                AnimatedGradientBackground(
                                    colors = colors,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(backgroundAlpha)
                                )
                            }
                        }
                    }
                    else -> {
                        PlayerBackgroundStyle.DEFAULT
                    }
                }
            }
        },
        onDismiss = {
            playerConnection.service.clearAutomix()
            playerConnection.player.stop()
            playerConnection.player.clearMediaItems()
        },
        collapsedContent = {
            MiniPlayer(
                position = position,
                duration = duration,
                pureBlack = pureBlack,
                backdrop = backdrop,
                layer = layer,
                luminance = luminance,
            )
        },
    ) {
        val controlsContent: @Composable ColumnScope.(MediaMetadata) -> Unit = { mediaMetadata ->
            @Composable
            fun AudioOutputWidget() {
                val audioManager = try {
                    context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                } catch (e: Exception) {
                    null
                }
                val devices = try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioManager != null) {
                        audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }
                val hasBluetoothDevice = devices.any { 
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || 
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO 
                }
                val hasWiredHeadset = devices.any { 
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || 
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES 
                }
                
                val audioIcon = when {
                    hasBluetoothDevice -> R.drawable.audio_bluetooth
                    hasWiredHeadset -> R.drawable.audio_earphone
                    else -> R.drawable.audio_device
                }

                val audioText = when {
                    hasBluetoothDevice -> "Bluetooth Device"
                    hasWiredHeadset -> "Wired Headset"
                    else -> "Phone Speaker"
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { audioRoutingSheetState.expandSoft() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(audioIcon),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = audioText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            val playPauseRoundness by animateDpAsState(
                targetValue = 36.dp,
                animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                label = "playPauseRoundness",
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlayerHorizontalPadding),
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {



                    @Composable
                    fun AudioOutputWidget() {
                        val audioManager = try {
                            context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                        } catch (e: Exception) {
                            null
                        }
                        val devices = try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioManager != null) {
                                audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
                            } else {
                                emptyList()
                            }
                        } catch (e: Exception) {
                            emptyList()
                        }
                        val hasBluetoothDevice = devices.any { 
                            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || 
                            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO 
                        }
                        val hasWiredHeadset = devices.any { 
                            it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || 
                            it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES 
                        }
                        
                        val audioIcon = when {
                            hasBluetoothDevice -> R.drawable.audio_bluetooth
                            hasWiredHeadset -> R.drawable.audio_earphone
                            else -> R.drawable.audio_device
                        }

                        val audioText = when {
                            hasBluetoothDevice -> "Bluetooth Device"
                            hasWiredHeadset -> "Wired Headset"
                            else -> "Phone Speaker"
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .clip(RoundedCornerShape(32.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .clickable { audioRoutingSheetState.expandSoft() }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(audioIcon),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = audioText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    // Audio Output Label (Above video/song name)
                    if (!showQueue && !showLyrics) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            AudioOutputWidget()
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    
                    if (!showQueue && !showLyrics) {
                        AnimatedContent(
                            targetState = mediaMetadata.title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "",
                    ) { title ->
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = TextBackgroundColor,
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    enabled = true,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (mediaMetadata.album != null) {
                                            navController.navigate("album/${mediaMetadata.album.id}")
                                            state.collapseSoft()
                                        }
                                    },
                                    onLongClick = {
                                        val clip = ClipData.newPlainText("Copied Title", title)
                                        clipboardManager.setPrimaryClip(clip)
                                        Toast
                                            .makeText(context, "Copied Title", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                )
                            ,
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    if (mediaMetadata.artists.any { it.name.isNotBlank() }) {
                        val annotatedString = buildAnnotatedString {
                            mediaMetadata.artists.forEachIndexed { index, artist ->
                                val tag = "artist_${artist.id.orEmpty()}"
                                pushStringAnnotation(tag = tag, annotation = artist.id.orEmpty())
                                withStyle(SpanStyle(color = TextBackgroundColor, fontSize = 16.sp)) {
                                    append(artist.name)
                                }
                                pop()
                                if (index != mediaMetadata.artists.lastIndex) append(", ")
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxWidth()
                                .padding(end = 12.dp)
                        ) {
                            var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                            var clickOffset by remember { mutableStateOf<Offset?>(null) }
                            Text(
                                text = annotatedString,
                                style = MaterialTheme.typography.titleMedium.copy(color = TextBackgroundColor),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                onTextLayout = { layoutResult = it },
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        awaitPointerEventScope {
                                            while (true) {
                                                val event = awaitPointerEvent()
                                                val tapPosition = event.changes.firstOrNull()?.position
                                                if (tapPosition != null) {
                                                    clickOffset = tapPosition
                                                }
                                            }
                                        }
                                    }
                                    .combinedClickable(
                                        enabled = true,
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            val tapPosition = clickOffset
                                            val layout = layoutResult
                                            if (tapPosition != null && layout != null) {
                                                val offset = layout.getOffsetForPosition(tapPosition)
                                                annotatedString
                                                    .getStringAnnotations(offset, offset)
                                                    .firstOrNull()
                                                    ?.let { ann ->
                                                        val artistId = ann.item
                                                        if (artistId.isNotBlank()) {
                                                            navController.navigate("artist/$artistId")
                                                            state.collapseSoft()
                                                        }
                                                    }
                                            }
                                        },
                                        onLongClick = {
                                            val clip =
                                                ClipData.newPlainText("Copied Artist", annotatedString)
                                            clipboardManager.setPrimaryClip(clip)
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Copied Artist",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
                                    )
                            )
                        }
                    }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (useNewPlayerDesign) {
                    // Audio button moved to top. Just Favorite button here.
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = if (mediaMetadata.id.isNotEmpty()) 48.dp else 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(textButtonColor)
                                .clickable {
                                    playerConnection.toggleLike()
                                }
                        ) {
                            Image(
                                painter = painterResource(
                                    if (currentSong?.song?.liked == true)
                                        R.drawable.favorite
                                    else R.drawable.favorite_border
                                ),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(iconButtonColor),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(24.dp)
                            )
                        }
                    }
                } else {
                     // Audio button moved to top. Just Menu button here.
                     
                    if (showQueue || showLyrics) {
                         Row(
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .padding(top = 8.dp),
                             horizontalArrangement = Arrangement.SpaceBetween,
                             verticalAlignment = Alignment.CenterVertically
                         ) {
                             AudioOutputWidget()

                             Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .clickable {
                                        menuState.show {
                                            PlayerMenu(
                                                mediaMetadata = mediaMetadata,
                                                navController = navController,
                                                playerBottomSheetState = state,
                                                onShowDetailsDialog = {
                                                    mediaMetadata.id.let {
                                                        bottomSheetPageState.show {
                                                            ShowMediaInfo(it)
                                                        }
                                                    }
                                                },
                                                onDismiss = menuState::dismiss,
                                            )
                                        }
                                    },
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.more_vert),
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(Color.White),
                                )
                            }
                         }
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier =
                            Modifier
                                .padding(top = if (mediaMetadata.id.isNotEmpty()) 48.dp else 8.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .clickable {
                                    menuState.show {
                                        PlayerMenu(
                                            mediaMetadata = mediaMetadata,
                                            navController = navController,
                                            playerBottomSheetState = state,
                                            onShowDetailsDialog = {
                                                mediaMetadata.id.let {
                                                    bottomSheetPageState.show {
                                                        ShowMediaInfo(it)
                                                    }
                                                }
                                            },
                                            onDismiss = menuState::dismiss,
                                        )
                                    }
                                },
                        ) {
                            Image(
                                painter = painterResource(R.drawable.more_vert),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            when (sliderStyle) {
                SliderStyle.DEFAULT -> {
                    Slider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = {
                            sliderPosition = it.toLong()
                        },
                        onValueChangeFinished = {
                            sliderPosition?.let {
                                playerConnection.player.seekTo(it)
                                position = it
                            }
                            sliderPosition = null
                        },
                        colors = PlayerSliderColors.defaultSliderColors(textButtonColor, playerBackground, useDarkTheme),
                        modifier = Modifier.padding(horizontal = PlayerHorizontalPadding),
                    )
                }

                SliderStyle.SQUIGGLY -> {
                    SquigglySlider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = {
                            sliderPosition = it.toLong()
                        },
                        onValueChangeFinished = {
                            sliderPosition?.let {
                                playerConnection.player.seekTo(it)
                                position = it
                            }
                            sliderPosition = null
                        },
                        colors = PlayerSliderColors.squigglySliderColors(textButtonColor, playerBackground, useDarkTheme),
                        modifier = Modifier.padding(horizontal = PlayerHorizontalPadding),
                        squigglesSpec =
                        SquigglySlider.SquigglesSpec(
                            amplitude = if (isPlaying) (2.dp).coerceAtLeast(2.dp) else 0.dp,
                            strokeWidth = 3.dp,
                        ),
                    )
                }

                SliderStyle.SLIM -> {
                    Slider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = {
                            sliderPosition = it.toLong()
                        },
                        onValueChangeFinished = {
                            sliderPosition?.let {
                                playerConnection.player.seekTo(it)
                                position = it
                            }
                            sliderPosition = null
                        },
                        thumb = { Spacer(modifier = Modifier.size(0.dp)) },
                        track = { sliderState ->
                            PlayerSliderTrack(
                                sliderState = sliderState,
                                colors = PlayerSliderColors.slimSliderColors(textButtonColor, playerBackground, useDarkTheme)
                            )
                        },
                        modifier = Modifier.padding(horizontal = PlayerHorizontalPadding)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PlayerHorizontalPadding + 4.dp),
            ) {
                Text(
                    text = makeTimeString(sliderPosition ?: position),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextBackgroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = if (duration != C.TIME_UNSET) makeTimeString(duration) else "",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextBackgroundColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (useNewPlayerDesign) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val maxW = maxWidth
                    val playButtonHeight = maxW / 6f
                    val playButtonWidth = playButtonHeight * 1.6f
                    val sideButtonHeight = playButtonHeight * 0.8f
                    val sideButtonWidth = sideButtonHeight * 1.3f

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        FilledTonalIconButton(
                            onClick = playerConnection::seekToPrevious,
                            enabled = canSkipPrevious,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = textButtonColor,
                                contentColor = iconButtonColor
                            ),
                            modifier = Modifier
                                .size(width = sideButtonWidth, height = sideButtonHeight)
                                .clip(RoundedCornerShape(32.dp))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_previous),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        FilledIconButton(
                            onClick = {
                                if (playbackState == STATE_ENDED) {
                                    playerConnection.player.seekTo(0, 0)
                                    playerConnection.player.playWhenReady = true
                                } else {
                                    playerConnection.player.togglePlayPause()
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = textButtonColor,
                                contentColor = iconButtonColor
                            ),
                            modifier = Modifier
                                .size(width = playButtonWidth, height = playButtonHeight)
                                .clip(RoundedCornerShape(32.dp))
                        ) {
                            Icon(
                                painter = painterResource(
                                    when {
                                        playbackState == STATE_ENDED -> R.drawable.replay
                                        isPlaying -> R.drawable.pause
                                        else -> R.drawable.play
                                    }
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        FilledTonalIconButton(
                            onClick = playerConnection::seekToNext,
                            enabled = canSkipNext,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = textButtonColor,
                                contentColor = iconButtonColor
                            ),
                            modifier = Modifier
                                .size(width = sideButtonWidth, height = sideButtonHeight)
                                .clip(RoundedCornerShape(32.dp))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_next),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = PlayerHorizontalPadding),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ResizableIconButton(
                            icon = when (repeatMode) {
                                Player.REPEAT_MODE_OFF, Player.REPEAT_MODE_ALL -> R.drawable.repeat
                                Player.REPEAT_MODE_ONE -> R.drawable.repeat_one
                                else -> throw IllegalStateException()
                            },
                            color = TextBackgroundColor,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .align(Alignment.Center)
                                .alpha(if (repeatMode == Player.REPEAT_MODE_OFF) 0.5f else 1f),
                            onClick = {
                                playerConnection.player.toggleRepeatMode()
                            },
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ResizableIconButton(
                            icon = R.drawable.skip_previous,
                            enabled = canSkipPrevious,
                            color = TextBackgroundColor,
                            modifier =
                            Modifier
                                .size(32.dp)
                                .align(Alignment.Center),
                            onClick = playerConnection::seekToPrevious,
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier =
                        Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(playPauseRoundness))
                            .background(textButtonColor)
                            .clickable {
                                if (playbackState == STATE_ENDED) {
                                    playerConnection.player.seekTo(0, 0)
                                    playerConnection.player.playWhenReady = true
                                } else {
                                    playerConnection.player.togglePlayPause()
                                }
                            },
                    ) {
                        Image(
                            painter =
                            painterResource(
                                if (playbackState ==
                                    STATE_ENDED
                                ) {
                                    R.drawable.replay
                                } else if (isPlaying) {
                                    R.drawable.pause
                                } else {
                                    R.drawable.play
                                },
                            ),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(iconButtonColor),
                            modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(36.dp),
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        ResizableIconButton(
                            icon = R.drawable.skip_next,
                            enabled = canSkipNext,
                            color = TextBackgroundColor,
                            modifier =
                            Modifier
                                .size(32.dp)
                                .align(Alignment.Center),
                            onClick = playerConnection::seekToNext,
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ResizableIconButton(
                            icon = if (currentSong?.song?.liked == true) R.drawable.favorite else R.drawable.favorite_border,
                            color = if (currentSong?.song?.liked == true) MaterialTheme.colorScheme.error else TextBackgroundColor,
                            modifier =
                            Modifier
                                .size(32.dp)
                                .padding(4.dp)
                                .align(Alignment.Center),
                            onClick = playerConnection::toggleLike,
                        )
                    }
                }
            }
        }

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back button at top left
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Close player",
                        tint = textButtonColor,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Start))
                            .padding(16.dp)
                            .size(24.dp)
                            .clickable { state.collapseSoft() }
                    )
                    
                    // Share button at top right
                    Icon(
                        painter = painterResource(R.drawable.share),
                        contentDescription = "Share",
                        tint = textButtonColor,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.End))
                            .padding(16.dp)
                            .size(24.dp)
                            .clickable {
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "https://music.youtube.com/watch?v=${mediaMetadata?.id}"
                                    )
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                    )
                    
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                    Modifier
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                        .padding(bottom = queueSheetState.collapsedBound),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.weight(1f),
                    ) {
                        Thumbnail(
                            sliderPositionProvider = { sliderPosition },
                            modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                            isPlayerExpanded = state.isExpanded,
                            onToggleLyrics = {
                                showLyrics = !showLyrics
                            },
                            overlayContent = {
                                if (mediaMetadata?.id?.isNotEmpty() == true && isRectangularThumbnail) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .clickable {
                                                playerConnection.player.pause()
                                                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                                    putExtra("VIDEO_ID", mediaMetadata?.id)
                                                    putExtra("START_POSITION", playerConnection.player.currentPosition)
                                                }
                                                context.startActivity(intent)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.play),
                                            contentDescription = "Switch to Video",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                        )
                    }

                    mediaMetadata?.let {
                        controlsContent(it)
                    }

                    Spacer(Modifier.height(30.dp))
                }
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Blurred Background Layer
                    mediaMetadata?.let { metadata ->
                        AsyncImage(
                            model = coil3.request.ImageRequest.Builder(LocalContext.current)
                                .data(metadata.thumbnailUrl)
                                .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                                .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                                .networkCachePolicy(coil3.request.CachePolicy.ENABLED)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(100.dp) // Strong blur for background
                        )
                        
                        // Dark overlay to ensure text readability against the blurred image
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )
                    }

                    // 2. Foreground Content (Thumbnail + Controls)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                            .padding(bottom = queueSheetState.collapsedBound),
                    ) {
                        // Global Drag Handle (Apple Music style)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                             Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.4f))
                             )
                        }

                        
                        // Main Thumbnail
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier.weight(1f),
                        ) {
                            AnimatedContent(
                                targetState = when {
                                    showQueue -> "queue"
                                    showLyrics -> "lyrics"
                                    else -> "thumbnail"
                                },
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(400)).togetherWith(fadeOut(animationSpec = tween(400)))
                                },
                                label = "PlayerViewTransition"
                            ) { viewMode ->
                                when (viewMode) {
                                    "lyrics" -> {
                                        Lyrics(
                                            sliderPositionProvider = { sliderPosition },
                                            isVisible = true,
                                            palette = gradientColors,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    "queue" -> {
                                        QueueContent(
                                            navController = navController,
                                            modifier = Modifier.fillMaxSize(),
                                            background = bottomSheetBackgroundColor,
                                            onBackgroundColor = onBackgroundColor
                                        )
                                    }
                                    else -> {
                                        Thumbnail(
                                            sliderPositionProvider = { sliderPosition },
                                            modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                                            isPlayerExpanded = state.isExpanded,
                                            onToggleLyrics = {
                                                showLyrics = !showLyrics
                                                showQueue = false
                                            },
                                            overlayContent = {
                                                if (mediaMetadata?.id?.isNotEmpty() == true && isRectangularThumbnail) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.Center)
                                                            .size(64.dp)
                                                            .clip(RoundedCornerShape(50))
                                                            .background(Color.Black.copy(alpha = 0.5f))
                                                            .clickable {
                                                                playerConnection.player.pause()
                                                                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                                                    putExtra("VIDEO_ID", mediaMetadata?.id)
                                                                    putExtra("START_POSITION", playerConnection.player.currentPosition)
                                                                }
                                                                context.startActivity(intent)
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.play),
                                                            contentDescription = "Switch to Video",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(32.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        mediaMetadata?.let {
                            controlsContent(it)
                        }

                        Spacer(Modifier.height(30.dp))
                    }


                }
            }
        }


        // Old Queue bottom sheet removed - now using QueueContent overlay instead
        /*
        Queue(
            state = queueSheetState,
            playerBottomSheetState = state,
            navController = navController,
            background =
            if (useBlackBackground) {
                Color.Black
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            onBackgroundColor = onBackgroundColor,
            TextBackgroundColor = TextBackgroundColor,
            textButtonColor = textButtonColor,
            iconButtonColor = iconButtonColor,
            onShowLyrics = { 
                showLyrics = !showLyrics
                showQueue = false
            },
            onShowQueue = {
                showQueue = !showQueue
                showLyrics = false
            },
            pureBlack = pureBlack,
        )
        */


        // Lyrics BottomSheet removed

        
        // Audio Routing Bottom Sheet
        BottomSheet(
            state = audioRoutingSheetState,
            background = { 
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            if (useBlackBackground) Color.Black 
                            else MaterialTheme.colorScheme.surfaceContainer
                        )
                ) 
            },
            onDismiss = { },
            collapsedContent = {}
        ) {
            val audioManager = try {
                context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            } catch (e: Exception) {
                null
            }
            
            val bluetoothAdapter = try {
                val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                bluetoothManager?.adapter
            } catch (e: Exception) {
                null
            }
            
            val wifiManager = try {
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            } catch (e: Exception) {
                null
            }
            
            // Get available audio devices
            val devices = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioManager != null) {
                    audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).toList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
            
            // Get currently active audio device
            val activeDevices = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioManager != null) {
                    audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).filter { device ->
                        // Check if this device is currently being used for music playback
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                                    .any { it.id == device.id }
                            } else {
                                true
                            }
                        } catch (e: Exception) {
                            false
                        }
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }
            
            // Determine current active device type
            val isPlayingOnBluetooth = activeDevices.any { 
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || 
                it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO 
            }
            val isPlayingOnWiredHeadset = activeDevices.any { 
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || 
                it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES 
            }
            val isPlayingOnUsb = activeDevices.any { 
                it.type == AudioDeviceInfo.TYPE_USB_DEVICE || 
                it.type == AudioDeviceInfo.TYPE_USB_HEADSET 
            }
            val isPlayingOnSpeaker = !isPlayingOnBluetooth && !isPlayingOnWiredHeadset && !isPlayingOnUsb
            
            val hasBluetoothDevice = try {
                devices.any { 
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || 
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO 
                }
            } catch (e: Exception) {
                false
            }
            
            val hasWiredHeadset = try {
                devices.any { 
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET || 
                    it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES 
                }
            } catch (e: Exception) {
                false
            }
            
            val hasUsbDevice = try {
                devices.any { 
                    it.type == AudioDeviceInfo.TYPE_USB_DEVICE || 
                    it.type == AudioDeviceInfo.TYPE_USB_HEADSET 
                }
            } catch (e: Exception) {
                false
            }
            
            val isBluetoothOn = try {
                bluetoothAdapter?.isEnabled == true
            } catch (e: Exception) {
                false
            }
            
            val isWifiOn = try {
                wifiManager?.isWifiEnabled == true
            } catch (e: Exception) {
                false
            }
            
            val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
            val sheetContentColor = MaterialTheme.colorScheme.onSurface
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(sheetBackgroundColor)
                    .padding(24.dp)
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Audio Output",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = sheetContentColor
                    )
                    FilledTonalIconButton(
                        onClick = { audioRoutingSheetState.collapseSoft() },
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
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                     // Connected Devices Header
                    Text(
                        "CONNECTED DEVICES",
                        style = MaterialTheme.typography.labelLarge,
                        color = sheetContentColor.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        
                        // 1. "This Device" Option (Always Visible)
                        val isThisDeviceActive = !isPlayingOnBluetooth && !isPlayingOnWiredHeadset && !isPlayingOnUsb
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Logic to switch to speaker
                                    // Note: Android API doesn't easily allow "forcing" to speaker if BT is connected without disconnecting
                                    // But we can try using the media router or simple Toast for now if complex.
                                    // For now, let's keep the existing logic if present, or just UI.
                                    audioRoutingSheetState.collapseSoft()
                                }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.audio_device),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = sheetContentColor
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "This Device",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sheetContentColor
                                )
                                Text(
                                    if (isThisDeviceActive) "Playing now" else "Phone Speaker",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f)
                                )
                            }
                            if (isThisDeviceActive) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Active",
                                    tint = sheetContentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        // Separator if needed, but background grouping handles it nicely usually. 
                        // Just use Column spacing.
                        
                        // 2. Bluetooth Devices Section
                        // Show "Bluetooth Devices" generic item
                        val bluetoothIcon = R.drawable.audio_bluetooth 
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Open Bluetooth Settings
                                    try {
                                        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open Bluetooth settings", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(bluetoothIcon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = sheetContentColor
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Bluetooth Devices",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sheetContentColor
                                )
                                Text(
                                    if (hasBluetoothDevice) "Manage devices" else "No device found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f)
                                )
                            }
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = null,
                                tint = sheetContentColor.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    // Spacer for Cast/WiFi devices if we wanted to add them, but user prompt focused on Speaker/BT.
                    // Adding specific placeholder for Wifi as per screenshot "WiFi, Cast & DLNA" if relevant
                    
                     Text(
                        "WIFI, CAST & DLNA DEVICES",
                        style = MaterialTheme.typography.labelLarge,
                        color = sheetContentColor.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 0.dp)
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                         Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.cast),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = sheetContentColor
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "WiFi, Cast & DLNA Devices",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = sheetContentColor
                                )
                                Text(
                                    "Scanning...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = sheetContentColor.copy(alpha = 0.6f)
                                )
                            }
                    }
                }
            }
        }
        }
        
        // Bottom Navigation Bar - fixed at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Transparent)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Queue Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showQueue = !showQueue
                            showLyrics = false
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.queue_music),
                        contentDescription = "Queue",
                        modifier = Modifier.size(24.dp),
                        tint = if (showQueue) MaterialTheme.colorScheme.primary
                        else onBackgroundColor.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Queue",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (showQueue) MaterialTheme.colorScheme.primary
                        else onBackgroundColor.copy(alpha = 0.7f)
                    )
                }
                
                // Sleep Timer Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showSleepTimerDialog = true
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bedtime),
                        contentDescription = "Sleep timer",
                        modifier = Modifier.size(24.dp),
                        tint = onBackgroundColor.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Sleep timer",
                        style = MaterialTheme.typography.labelSmall,
                        color = onBackgroundColor.copy(alpha = 0.7f)
                    )
                }
                
                // Lyrics Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            showLyrics = !showLyrics
                            showQueue = false
                        }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lyrics),
                        contentDescription = "Lyrics",
                        modifier = Modifier.size(24.dp),
                        tint = if (showLyrics) MaterialTheme.colorScheme.primary
                        else onBackgroundColor.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Lyrics",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (showLyrics) MaterialTheme.colorScheme.primary
                        else onBackgroundColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
