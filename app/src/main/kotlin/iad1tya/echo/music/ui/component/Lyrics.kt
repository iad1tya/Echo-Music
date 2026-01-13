package iad1tya.echo.music.ui.component

import android.annotation.SuppressLint
import iad1tya.echo.music.models.MediaMetadata
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Surface
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.zIndex
import iad1tya.echo.music.constants.PreferredLyricsProviderKey
import iad1tya.echo.music.constants.PreferredLyricsProvider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.CircularProgressIndicator
import iad1tya.echo.music.lyrics.LyricsTranslationHelper
import iad1tya.echo.music.ui.component.LyricsShareDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.palette.graphics.Palette
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import coil3.request.crossfade
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.DarkModeKey
import iad1tya.echo.music.constants.LyricsClickKey
import iad1tya.echo.music.constants.LyricsRomanizeBelarusianKey
import iad1tya.echo.music.constants.LyricsRomanizeBulgarianKey
import iad1tya.echo.music.constants.LyricsRomanizeCyrillicByLineKey
import iad1tya.echo.music.constants.LyricsRomanizeJapaneseKey
import iad1tya.echo.music.constants.LyricsRomanizeKoreanKey
import iad1tya.echo.music.constants.LyricsRomanizeKyrgyzKey
import iad1tya.echo.music.constants.LyricsRomanizeRussianKey
import iad1tya.echo.music.constants.LyricsRomanizeSerbianKey
import iad1tya.echo.music.constants.LyricsRomanizeUkrainianKey
import iad1tya.echo.music.constants.LyricsRomanizeMacedonianKey
import iad1tya.echo.music.constants.LyricsScrollKey
import iad1tya.echo.music.constants.LyricsTextPositionKey
import iad1tya.echo.music.constants.PlayerBackgroundStyle
import iad1tya.echo.music.constants.PlayerBackgroundStyleKey
import iad1tya.echo.music.db.entities.LyricsEntity.Companion.LYRICS_NOT_FOUND
import iad1tya.echo.music.lyrics.LyricsEntry
import iad1tya.echo.music.lyrics.LyricsUtils.findCurrentLineIndex
import iad1tya.echo.music.lyrics.LyricsUtils.isBelarusian
import iad1tya.echo.music.lyrics.LyricsUtils.isChinese
import iad1tya.echo.music.lyrics.LyricsUtils.isJapanese
import iad1tya.echo.music.lyrics.LyricsUtils.isKorean
import iad1tya.echo.music.lyrics.LyricsUtils.isKyrgyz
import iad1tya.echo.music.lyrics.LyricsUtils.isRussian
import iad1tya.echo.music.lyrics.LyricsUtils.isSerbian
import iad1tya.echo.music.lyrics.LyricsUtils.isBulgarian
import iad1tya.echo.music.lyrics.LyricsUtils.isUkrainian
import iad1tya.echo.music.lyrics.LyricsUtils.isMacedonian
import iad1tya.echo.music.lyrics.LyricsUtils.parseLyrics
import iad1tya.echo.music.lyrics.LyricsUtils.romanizeCyrillic
import iad1tya.echo.music.lyrics.LyricsUtils.romanizeJapanese
import iad1tya.echo.music.lyrics.LyricsUtils.romanizeKorean
import iad1tya.echo.music.ui.component.shimmer.ShimmerHost
import iad1tya.echo.music.ui.component.shimmer.TextPlaceholder
import iad1tya.echo.music.ui.screens.settings.DarkMode
import iad1tya.echo.music.ui.screens.settings.LyricsPosition
import iad1tya.echo.music.ui.utils.fadingEdge
import iad1tya.echo.music.utils.ComposeToImage
import iad1tya.echo.music.utils.rememberEnumPreference
import iad1tya.echo.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds
import iad1tya.echo.music.constants.OpenRouterApiKey
import iad1tya.echo.music.constants.OpenRouterBaseUrlKey
import iad1tya.echo.music.constants.OpenRouterModelKey
import iad1tya.echo.music.constants.AutoTranslateLyricsKey
import iad1tya.echo.music.constants.AutoTranslateLyricsMismatchKey
import iad1tya.echo.music.constants.TranslateLanguageKey
import iad1tya.echo.music.lyrics.LanguageDetectionHelper
import androidx.hilt.navigation.compose.hiltViewModel
import iad1tya.echo.music.viewmodels.LyricsMenuViewModel
import iad1tya.echo.music.ui.component.DefaultDialog
import iad1tya.echo.music.ui.component.ListDialog
import android.app.SearchManager
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import iad1tya.echo.music.LocalDatabase
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.room.util.copy
import iad1tya.echo.music.db.entities.LyricsEntity
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.M)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedBoxWithConstraintsScope", "StringFormatInvalid")
@Composable
fun Lyrics(
    sliderPositionProvider: () -> Long?,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    palette: List<Color> = emptyList(),
    viewModel: LyricsMenuViewModel = hiltViewModel(),
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val menuState = LocalMenuState.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val database = LocalDatabase.current
    val configuration = LocalConfiguration.current // Get configuration

    val landscapeOffset =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val lyricsTextPosition by rememberEnumPreference(LyricsTextPositionKey, LyricsPosition.CENTER)
    val changeLyrics by rememberPreference(LyricsClickKey, true)
    val scrollLyrics by rememberPreference(LyricsScrollKey, true)
    val romanizeJapaneseLyrics by rememberPreference(LyricsRomanizeJapaneseKey, true)
    val romanizeKoreanLyrics by rememberPreference(LyricsRomanizeKoreanKey, true)
    val romanizeRussianLyrics by rememberPreference(LyricsRomanizeRussianKey, true)
    val romanizeUkrainianLyrics by rememberPreference(LyricsRomanizeUkrainianKey, true)
    val romanizeSerbianLyrics by rememberPreference(LyricsRomanizeSerbianKey, true)
    val romanizeBulgarianLyrics by rememberPreference(LyricsRomanizeBulgarianKey, true)
    val romanizeBelarusianLyrics by rememberPreference(LyricsRomanizeBelarusianKey, true)
    val romanizeKyrgyzLyrics by rememberPreference(LyricsRomanizeKyrgyzKey, true)
    val romanizeMacedonianLyrics by rememberPreference(LyricsRomanizeMacedonianKey, true)
    val romanizeCyrillicByLine by rememberPreference(LyricsRomanizeCyrillicByLineKey, false)
    
    val openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
    val openRouterBaseUrl by rememberPreference(OpenRouterBaseUrlKey, "https://openrouter.ai/api/v1/chat/completions")
    val openRouterModel by rememberPreference(OpenRouterModelKey, "mistralai/mistral-small-3.1-24b-instruct:free")
    val autoTranslateLyrics by rememberPreference(AutoTranslateLyricsKey, false)
    val autoTranslateLyricsMismatch by rememberPreference(AutoTranslateLyricsMismatchKey, false)
    val translateLanguage by rememberPreference(TranslateLanguageKey, "en")
    val translateMode by rememberPreference(iad1tya.echo.music.constants.TranslateModeKey, "Literal")
    
    val preferredLyricsProvider by rememberEnumPreference(PreferredLyricsProviderKey, PreferredLyricsProvider.LRCLIB)
    var showMenu by remember { mutableStateOf(false) }
    
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    
    val scope = rememberCoroutineScope()

    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val lyricsEntity by playerConnection.currentLyrics.collectAsState(initial = null)
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val lyrics = remember(lyricsEntity) { lyricsEntity?.lyrics?.trim() }

    // Search Dialog States (Moved here to access mediaMetadata)
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }
    var showSearchResultDialog by rememberSaveable { mutableStateOf(false) }
    
    val searchMediaMetadata = mediaMetadata ?: MediaMetadata(id = "", title = "", artists = emptyList(), duration = 0) 
    
    val (titleField, onTitleFieldChange) = rememberSaveable(showSearchDialog, stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(text = searchMediaMetadata.title))
    }
    val (artistField, onArtistFieldChange) = rememberSaveable(showSearchDialog, stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(text = searchMediaMetadata.artists.joinToString { it.name }))
    }

    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT
    )

    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }

    val lines = remember(lyrics, scope) {
        if (lyrics == null || lyrics == LYRICS_NOT_FOUND) {
            emptyList()
        } else if (lyrics.startsWith("[")) {
            val parsedLines = parseLyrics(lyrics)
            
            // Romanization/Translation logic would go here
            // For now, mapping directly to ensure display works first
            parsedLines.map { entry ->
                LyricsEntry(entry.time, entry.text)
            }.let {
                listOf(LyricsEntry.HEAD_LYRICS_ENTRY) + it
            }
        } else {
            // Handle unsynced lyrics (plain text)
             lyrics.lines().mapIndexed { index, line ->
                 LyricsEntry(index * 2000L, line) // Approximate timing for scrolling
             }.let {
                 listOf(LyricsEntry.HEAD_LYRICS_ENTRY) + it
             }
        }
    }
    val translationStatus by LyricsTranslationHelper.status.collectAsState()

    
    // Status UI
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
            .padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val status = translationStatus) {
            is LyricsTranslationHelper.TranslationStatus.Translating -> {
                Card(
                     colors = CardDefaults.cardColors(
                         containerColor = MaterialTheme.colorScheme.primaryContainer
                     ),
                     shape = RoundedCornerShape(16.dp),
                     elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Translating lyrics...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            is LyricsTranslationHelper.TranslationStatus.Error -> {
                Card(
                     colors = CardDefaults.cardColors(
                         containerColor = MaterialTheme.colorScheme.errorContainer
                     ),
                     shape = RoundedCornerShape(16.dp),
                     elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = status.message,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            is LyricsTranslationHelper.TranslationStatus.Success -> {
                 Card(
                     colors = CardDefaults.cardColors(
                         containerColor = MaterialTheme.colorScheme.tertiaryContainer
                     ),
                     shape = RoundedCornerShape(16.dp),
                     elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Translated",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
            else -> {}
        }
    }

    val isSynced =
        remember(lyrics) {
            !lyrics.isNullOrEmpty() && lyrics.startsWith("[")
        }

    val textColor = if (playerBackground == PlayerBackgroundStyle.GRADIENT) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    var currentLineIndex by remember {
        mutableIntStateOf(-1)
    }
    // Because LaunchedEffect has delay, which leads to inconsistent with current line color and scroll animation,
    // we use deferredCurrentLineIndex when user is scrolling
    var deferredCurrentLineIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    var previousLineIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    var lastPreviewTime by rememberSaveable {
        mutableLongStateOf(0L)
    }
    var isSeeking by remember {
        mutableStateOf(false)
    }

    var initialScrollDone by rememberSaveable {
        mutableStateOf(false)
    }

    var shouldScrollToFirstLine by rememberSaveable {
        mutableStateOf(true)
    }

    var isAppMinimized by rememberSaveable {
        mutableStateOf(false)
    }

    var showProgressDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showImageCustomizationDialog by remember { mutableStateOf(false) }
    var shareDialogData by remember { mutableStateOf<Triple<String, String, String>?>(null) }

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var previewBackgroundColor by remember { mutableStateOf(Color(0xFF242424)) }
    var previewTextColor by remember { mutableStateOf(Color.White) }
    var previewSecondaryTextColor by remember { mutableStateOf(Color.White.copy(alpha = 0.7f)) }

    // State for multi-selection
    var isSelectionModeActive by rememberSaveable { mutableStateOf(false) }
    val selectedIndices = remember { mutableStateListOf<Int>() }
    var showMaxSelectionToast by remember { mutableStateOf(false) } // State for showing max selection toast

    val lazyListState = rememberLazyListState()
    
    var isAnimating by remember { mutableStateOf(false) }

    // Handle back button press - close selection mode instead of exiting screen
    BackHandler(enabled = isSelectionModeActive) {
        isSelectionModeActive = false
        selectedIndices.clear()
    }

    // Define max selection limit
    val maxSelectionLimit = 5

    // Show toast when max selection is reached
    LaunchedEffect(showMaxSelectionToast) {
        if (showMaxSelectionToast) {
            Toast.makeText(
                context,
                context.getString(R.string.max_selection_limit, maxSelectionLimit),
                Toast.LENGTH_SHORT
            ).show()
            showMaxSelectionToast = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val visibleItemsInfo = lazyListState.layoutInfo.visibleItemsInfo
                val isCurrentLineVisible = visibleItemsInfo.any { it.index == currentLineIndex }
                if (isCurrentLineVisible) {
                    initialScrollDone = false
                }
                isAppMinimized = true
            } else if(event == Lifecycle.Event.ON_START) {
                isAppMinimized = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Reset selection mode if lyrics change
    LaunchedEffect(lines) {
        isSelectionModeActive = false
        selectedIndices.clear()
    }

    LaunchedEffect(lyrics) {
        if (lyrics.isNullOrEmpty() || !lyrics.startsWith("[")) {
            currentLineIndex = -1
            return@LaunchedEffect
        }
        while (isActive) {
            delay(50)
            val sliderPosition = sliderPositionProvider()
            isSeeking = sliderPosition != null
            currentLineIndex = findCurrentLineIndex(
                lines,
                sliderPosition ?: playerConnection.player.currentPosition
            )
        }
    }



    LaunchedEffect(isSeeking, lastPreviewTime) {
        if (isSeeking) {
            lastPreviewTime = 0L
        } else if (lastPreviewTime != 0L) {
            delay(LyricsPreviewTime)
            lastPreviewTime = 0L
        }
    }

    LaunchedEffect(currentLineIndex, lastPreviewTime, initialScrollDone) {

        /**
         * Calculate the lyric offset Based on how many lines (\n chars)
         */
        fun calculateOffset() = with(density) {
            if (currentLineIndex < 0 || currentLineIndex >= lines.size) return@with 0
            val currentItem = lines[currentLineIndex]
            val totalNewLines = currentItem.text.count { it == '\n' }

            val dpValue = if (landscapeOffset) 16.dp else 20.dp
            dpValue.toPx().toInt() * totalNewLines
        }

        if (!isSynced) return@LaunchedEffect
        
        // Smooth page animation without sudden jumps - direct animation to center
        suspend fun performSmoothPageScroll(targetIndex: Int, duration: Int = 1500) {
            if (isAnimating) return // Prevent multiple animations
            
            isAnimating = true
            
            try {
                val itemInfo = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == targetIndex }
                if (itemInfo != null) {
                    // Item is visible, animate directly to center without sudden jumps
                    val viewportHeight = lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
                    val center = lazyListState.layoutInfo.viewportStartOffset + (viewportHeight / 2)
                    val itemCenter = itemInfo.offset + itemInfo.size / 2
                    val offset = itemCenter - center

                    if (kotlin.math.abs(offset) > 10) {
                        lazyListState.animateScrollBy(
                            value = offset.toFloat(),
                            animationSpec = tween(durationMillis = duration)
                        )
                    }
                } else {
                    // Item is not visible, scroll to it first without animation, then it will be handled in next cycle
                    lazyListState.scrollToItem(targetIndex)
                }
            } finally {
                isAnimating = false
            }
        }
        
        if((currentLineIndex == 0 && shouldScrollToFirstLine) || !initialScrollDone) {
            shouldScrollToFirstLine = false
            // Initial scroll to center the first line with medium animation (600ms)
            val initialCenterIndex = kotlin.math.max(0, currentLineIndex)
            performSmoothPageScroll(initialCenterIndex, 800) // Initial scroll duration
            if(!isAppMinimized) {
                initialScrollDone = true
            }
        } else if (currentLineIndex != -1) {
            deferredCurrentLineIndex = currentLineIndex
            if (isSeeking) {
                // Fast scroll for seeking to center the target line (300ms)
                val seekCenterIndex = kotlin.math.max(0, currentLineIndex - 1)
                performSmoothPageScroll(seekCenterIndex, 500) // Fast seek duration
            } else if ((lastPreviewTime == 0L || currentLineIndex != previousLineIndex) && scrollLyrics) {
                // Auto-scroll when lyrics settings allow it
                if (currentLineIndex != previousLineIndex) {
                    // Calculate which line should be at the top to center the active group
                    val centerTargetIndex = currentLineIndex
                    performSmoothPageScroll(centerTargetIndex, 1500) // Auto scroll duration
                }
            }
        }
        if(currentLineIndex > 0) {
            shouldScrollToFirstLine = true
        }
        previousLineIndex = currentLineIndex
    }

    BoxWithConstraints(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 12.dp)
    ) {
        val containerHeight = maxHeight // Capture height for use in inner scopes

        if (lyrics == LYRICS_NOT_FOUND) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.lyrics),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).alpha(0.3f),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.lyrics_not_found),
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { 
                             showSearchDialog = true
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("Search Lyrics")
                    }
                }
            }
        } else {
            val displayedCurrentLineIndex =
                if (isSeeking || isSelectionModeActive) deferredCurrentLineIndex else currentLineIndex
            
            Box(modifier = Modifier.fillMaxSize()) {
                // Sticky Header (Main Layout)
                Column(modifier = Modifier.fillMaxSize()) {
                 // Header
                // Header - Row Layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 72.dp, bottom = 16.dp) // Moved down
                        .zIndex(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Album Art
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                        modifier = Modifier.size(80.dp)
                    ) {
                         mediaMetadata?.thumbnailUrl?.let { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.music_note),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Provider Info (Left Aligned)
                    Column(modifier = Modifier.weight(1f)) {
                        val providerName = lyricsEntity?.provider?.takeIf { it != "Unknown" }
                            ?: when (preferredLyricsProvider) {
                                PreferredLyricsProvider.LRCLIB -> "LrcLib"
                                PreferredLyricsProvider.SIMPMUSIC -> "SimpMusic"
                                PreferredLyricsProvider.KUGOU -> "KuGou"
                            }
                        
                        Text(
                             text = "Lyrics by $providerName",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start
                        )
                    }

                    // Menu Button
                    Box {
                        FilledTonalIconButton(
                            onClick = { showMenu = true },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                             Icon(
                                painter = painterResource(R.drawable.more_vert),
                                contentDescription = "Lyrics Options",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                            tonalElevation = 8.dp
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(painterResource(R.drawable.sync), null, Modifier.size(20.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text("Refetch Lyrics", fontSize = 16.sp)
                                    }
                                },
                                onClick = { 
                                    showMenu = false
                                    mediaMetadata?.let {
                                        viewModel.refetchLyrics(it, lyricsEntity)
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(painterResource(R.drawable.search), null, Modifier.size(20.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text("Search Lyrics", fontSize = 16.sp)
                                    }
                                },
                                onClick = { 
                                    showMenu = false
                                    showSearchDialog = true
                                },
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }

                // Lyrics List
                LazyColumn(
                    state = lazyListState,
                    contentPadding = WindowInsets.systemBars
                        .only(WindowInsetsSides.Top)
                        .add(WindowInsets(bottom = containerHeight / 2))
                        .asPaddingValues(),
                    modifier = Modifier
                        .fadingEdge(vertical = 64.dp)
                        .nestedScroll(remember {
                            object : NestedScrollConnection {
                                override fun onPostScroll(
                                    consumed: Offset,
                                    available: Offset,
                                    source: NestedScrollSource
                                ): Offset {
                                    if (!isSelectionModeActive) { // Only update preview time if not selecting
                                        lastPreviewTime = System.currentTimeMillis()
                                    }
                                    return super.onPostScroll(consumed, available, source)
                                }

                                override suspend fun onPostFling(
                                    consumed: Velocity,
                                    available: Velocity
                                ): Velocity {
                                    if (!isSelectionModeActive) { // Only update preview time if not selecting
                                        lastPreviewTime = System.currentTimeMillis()
                                    }
                                    return super.onPostFling(consumed, available)
                                }
                            }
                        })
                        .weight(1f) // Take remaining space
                ) {

            if (lyrics == null) {
                item {
                    ShimmerHost {
                        repeat(10) {
                            Box(
                                contentAlignment = when (lyricsTextPosition) {
                                    LyricsPosition.LEFT -> Alignment.CenterStart
                                    LyricsPosition.CENTER -> Alignment.Center
                                    LyricsPosition.RIGHT -> Alignment.CenterEnd
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 4.dp)
                            ) {
                                TextPlaceholder()
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(
                    items = lines,
                    key = { index, item -> "$index-${item.time}" } // Add stable key
                ) { index, item ->
                    val isSelected = selectedIndices.contains(index)
                    val isActive = index == displayedCurrentLineIndex && isSynced
                    val distance = kotlin.math.abs(index - displayedCurrentLineIndex)

                    // Target values for animation
                    // Reduced active scale for subtle "bouncy" feel
                    val targetScale = when {
                        !isSynced || isActive -> 1.05f 
                        distance == 1 -> 0.95f 
                        distance >= 2 -> 0.85f  
                        else -> 1f
                    }

                    val targetAlpha = when {
                        !isSynced || (isSelectionModeActive && isSelected) -> 1f
                        isActive -> 1f
                        distance == 1 -> 0.6f
                        distance == 2 -> 0.3f
                        else -> 0.15f
                    }
                    
                    val animatedScale by animateFloatAsState(
                        targetValue = targetScale,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy, // Gentle bounce
                            stiffness = Spring.StiffnessLow // Slow and smooth
                        ),
                        label = "scale"
                    )

                    val animatedAlpha by animateFloatAsState(
                        targetValue = targetAlpha,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "alpha"
                    )

                    val itemModifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)) // Clip for background
                        .combinedClickable(
                            enabled = true,
                            onClick = {
                                if (isSelectionModeActive) {
                                    // Toggle selection
                                    if (isSelected) {
                                        selectedIndices.remove(index)
                                        if (selectedIndices.isEmpty()) {
                                            isSelectionModeActive =
                                                false // Exit mode if last item deselected
                                        }
                                    } else {
                                        if (selectedIndices.size < maxSelectionLimit) {
                                            selectedIndices.add(index)
                                        } else {
                                            showMaxSelectionToast = true
                                        }
                                    }
                                } else if (isSynced && changeLyrics) {
                                    // Professional seek action with smooth animation
                                    playerConnection.player.seekTo(item.time)
                                    // Smooth slow scroll when clicking on lyrics (3 seconds)
                                    scope.launch {
                                        // First scroll to the clicked item without animation
                                        lazyListState.scrollToItem(index = index)

                                        // Then animate it to center position slowly
                                        val itemInfo =
                                            lazyListState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
                                        if (itemInfo != null) {
                                            val viewportHeight =
                                                lazyListState.layoutInfo.viewportEndOffset - lazyListState.layoutInfo.viewportStartOffset
                                            val center =
                                                lazyListState.layoutInfo.viewportStartOffset + (viewportHeight / 2)
                                            val itemCenter = itemInfo.offset + itemInfo.size / 2
                                            val offset = itemCenter - center

                                            if (kotlin.math.abs(offset) > 10) { // Only animate if not already centered
                                                lazyListState.animateScrollBy(
                                                    value = offset.toFloat(),
                                                    animationSpec = tween(durationMillis = 800) // Fast smooth scroll
                                                )
                                            }
                                        }
                                    }
                                    lastPreviewTime = 0L
                                }
                            },
                            onLongClick = {
                                if (!isSelectionModeActive) {
                                    isSelectionModeActive = true
                                    selectedIndices.add(index)
                                } else if (!isSelected && selectedIndices.size < maxSelectionLimit) {
                                    // If already in selection mode and item not selected, add it if below limit
                                    selectedIndices.add(index)
                                } else if (!isSelected) {
                                    // If already at limit, show toast
                                    showMaxSelectionToast = true
                                }
                            }
                        )
                        .background(
                            if (isSelected && isSelectionModeActive) MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.3f
                            )
                            else Color.Transparent
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                            alpha = animatedAlpha
                        }

                    Column(
                        modifier = itemModifier,
                        horizontalAlignment = when (lyricsTextPosition) {
                            LyricsPosition.LEFT -> Alignment.Start
                            LyricsPosition.CENTER -> Alignment.CenterHorizontally
                            LyricsPosition.RIGHT -> Alignment.End
                        }
                    ) {
                        // Collect translated/romanized text
                        val translatedText by item.translatedTextFlow.collectAsState()
                        val romanizedText by item.romanizedTextFlow.collectAsState()
                        
                        // Determine what text to display based on mode and availability
                        val displayText: String?
                        val isTranslationError: Boolean
                        
                        when {
                            // Translation mode: Show translated text if available, original as fallback
                            translateMode == "Translated" -> {
                                isTranslationError = translatedText?.startsWith("⚠️") == true || translatedText?.startsWith("Error:") == true
                                displayText = if (translatedText != null && !isTranslationError) {
                                    translatedText
                                } else {
                                    item.text // Fallback to original
                                }
                            }
                            // Romanized mode: Show AI romanization if available, local romanization if available, original as fallback
                            translateMode == "Romanized" -> {
                                isTranslationError = translatedText?.startsWith("⚠️") == true || translatedText?.startsWith("Error:") == true
                                displayText = when {
                                    translatedText != null && !isTranslationError -> translatedText // AI romanization
                                    romanizedText != null -> romanizedText // Local romanization
                                    else -> item.text // Fallback to original
                                }
                            }
                            // Literal mode or no translation: Show local romanization if available, otherwise original
                            currentSong?.romanizeLyrics == true && romanizedText != null -> {
                                isTranslationError = false
                                displayText = romanizedText
                            }
                            // Default: Show original text
                            else -> {
                                isTranslationError = false
                                displayText = item.text
                            }
                        }
                        
                        // Display the selected text
                        // Active color is always White for the glow effect
                        val currentTextColor = if (isActive) Color.White else textColor
                        
                        // Smoother glow animation
                        val glowBlur by animateFloatAsState(
                            targetValue = if (isActive) 60f else 0f,
                            animationSpec = tween(durationMillis = 800)
                        )
                        val glowAlpha by animateFloatAsState(
                            targetValue = if (isActive) 0.9f else 0f,
                            animationSpec = tween(durationMillis = 800)
                        )

                        Text(
                            text = displayText ?: item.text,
                            fontSize = if (isActive) 34.sp else 26.sp,
                            lineHeight = if (isActive) 40.sp else 32.sp,
                            color = if (isActive) {
                                currentTextColor
                            } else {
                                textColor.copy(alpha = 0.5f)
                            },
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.White.copy(alpha = glowAlpha),
                                    blurRadius = glowBlur,
                                    offset = Offset(0f, 0f)
                                )
                            ),
                            textAlign = when (lyricsTextPosition) {
                                LyricsPosition.LEFT -> TextAlign.Left
                                LyricsPosition.CENTER -> TextAlign.Center
                                LyricsPosition.RIGHT -> TextAlign.Right
                            },
                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold
                        )
                    }
                }
            }
        }



        } // Close Sticky Column
        
        // Action buttons: Close and Share buttons grouped together
        if (isSelectionModeActive) {
            mediaMetadata?.let { metadata ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp), // Just above player slider
                    contentAlignment = Alignment.Center
                ) {
                    // Row containing both close and share buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Close button (circular, right side of share)
                        Box(
                            modifier = Modifier
                                .size(48.dp) // Larger for better touch target
                                .background(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable {
                                    isSelectionModeActive = false
                                    selectedIndices.clear()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = stringResource(R.string.cancel),
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Share button (rectangular with text)
                        Row(
                            modifier = Modifier
                                .background(
                                    color = if (selectedIndices.isNotEmpty())
                                        Color.White.copy(alpha = 0.9f) // White background when active
                                    else
                                        Color.White.copy(alpha = 0.5f), // Lighter white when inactive
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable(enabled = selectedIndices.isNotEmpty()) {
                                    if (selectedIndices.isNotEmpty()) {
                                        val sortedIndices = selectedIndices.sorted()
                                        val selectedLyricsText = sortedIndices
                                            .mapNotNull { lines.getOrNull(it)?.text }
                                            .joinToString("\n")

                                        if (selectedLyricsText.isNotBlank()) {
                                            shareDialogData = Triple(
                                                selectedLyricsText,
                                                metadata.title,
                                                metadata.artists.joinToString { it.name }
                                            )
                                            showShareDialog = true
                                        }
                                        isSelectionModeActive = false
                                        selectedIndices.clear()
                                    }
                                }
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.share),
                                contentDescription = stringResource(R.string.share_selected),
                                tint = Color.Black, // Black icon on white background
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = stringResource(R.string.share),
                                color = Color.Black, // Black text on white background
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
        // Removed the more button from bottom - it's now in the top header
      }

    }
    if (showSearchDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showSearchDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showSearchDialog = false } // Click outside to dismiss
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f), // Match Menu Color
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth() // Width limit
                        .clickable(interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null) {} // Intercept clicks
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "Search Lyrics",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        OutlinedTextField(
                            value = titleField,
                            onValueChange = onTitleFieldChange,
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = artistField,
                            onValueChange = onArtistFieldChange,
                            label = { Text("Artist") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showSearchDialog = false },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel", style = MaterialTheme.typography.bodyLarge)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    showSearchDialog = false
                                    showSearchResultDialog = true
                                    viewModel.search(
                                        searchMediaMetadata.id,
                                        titleField.text,
                                        artistField.text,
                                        searchMediaMetadata.duration
                                    )
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Search", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSearchResultDialog) {
        val results by viewModel.results.collectAsState()
        val isLoading by viewModel.isLoading.collectAsState()

        ListDialog(
            onDismiss = { showSearchResultDialog = false },
        ) {
            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(results) { result ->
                     ListItem(
                         headlineContent = { 
                             Text(
                                 text = result.providerName,
                                 style = MaterialTheme.typography.titleMedium,
                                 fontWeight = FontWeight.Bold,
                                 color = MaterialTheme.colorScheme.primary
                             ) 
                         },
                         supportingContent = { 
                             Text(
                                 text = result.lyrics, 
                                 maxLines = 3,
                                 overflow = TextOverflow.Ellipsis,
                                 style = MaterialTheme.typography.bodyMedium,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant
                             ) 
                         },
                         leadingContent = {
                             Icon(
                                 painter = painterResource(R.drawable.lyrics),
                                 contentDescription = null,
                                 tint = MaterialTheme.colorScheme.secondary
                             )
                         },
                         modifier = Modifier
                            .clickable {
                                 showSearchResultDialog = false
                                 viewModel.cancelSearch()
                                 scope.launch(Dispatchers.IO) {
                                     database.query {
                                         upsert(iad1tya.echo.music.db.entities.LyricsEntity(currentSong?.id ?: "", result.lyrics, result.providerName))
                                     }
                                 }
                             }
                             .padding(vertical = 4.dp)
                     )
                }
            }
        }
    }
}
}



private const val METROLIST_AUTO_SCROLL_DURATION = 1500L // Much slower auto-scroll for smooth transitions
private const val METROLIST_INITIAL_SCROLL_DURATION = 1000L // Slower initial positioning
private const val METROLIST_SEEK_DURATION = 800L // Slower user interaction
private const val METROLIST_FAST_SEEK_DURATION = 600L // Less aggressive seeking

// Lyrics constants
val LyricsPreviewTime = 2.seconds
