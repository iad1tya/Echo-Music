package iad1tya.echo.music

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Consumer
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.echo.innertube.YouTube
import com.echo.innertube.models.SongItem
import com.echo.innertube.models.WatchEndpoint
import iad1tya.echo.music.constants.AppBarHeight
import iad1tya.echo.music.constants.AppLanguageKey
import iad1tya.echo.music.constants.CheckForUpdatesKey
import iad1tya.echo.music.constants.DefaultOpenTabKey
import iad1tya.echo.music.constants.DisableScreenshotKey
import iad1tya.echo.music.constants.DynamicThemeKey
import iad1tya.echo.music.constants.MiniPlayerHeight
import iad1tya.echo.music.constants.MiniPlayerBottomSpacing
import iad1tya.echo.music.constants.UpdateNotificationsEnabledKey
import iad1tya.echo.music.constants.UseNewMiniPlayerDesignKey
import iad1tya.echo.music.constants.NavigationBarAnimationSpec
import iad1tya.echo.music.constants.NavigationBarHeight
import iad1tya.echo.music.constants.PauseSearchHistoryKey
import iad1tya.echo.music.constants.PureBlackKey
import iad1tya.echo.music.constants.SYSTEM_DEFAULT
import iad1tya.echo.music.constants.SearchSource
import iad1tya.echo.music.constants.SearchSourceKey
import iad1tya.echo.music.constants.SlimNavBarHeight
import iad1tya.echo.music.constants.SlimNavBarKey
import iad1tya.echo.music.constants.StopMusicOnTaskClearKey
import iad1tya.echo.music.db.MusicDatabase
import iad1tya.echo.music.db.entities.SearchHistory
import iad1tya.echo.music.extensions.toEnum
import iad1tya.echo.music.models.toMediaMetadata
import iad1tya.echo.music.playback.DownloadUtil
import iad1tya.echo.music.playback.MusicService
import iad1tya.echo.music.playback.MusicService.MusicBinder
import iad1tya.echo.music.playback.PlayerConnection
import iad1tya.echo.music.playback.queues.YouTubeQueue
import iad1tya.echo.music.ui.component.AccountSettingsDialog
import iad1tya.echo.music.ui.component.BottomSheetMenu
import iad1tya.echo.music.ui.component.BottomSheetPage
import iad1tya.echo.music.ui.component.IconButton
import iad1tya.echo.music.ui.component.LocalBottomSheetPageState
import iad1tya.echo.music.ui.component.LocalMenuState
import iad1tya.echo.music.ui.component.TopSearch
import iad1tya.echo.music.ui.component.rememberBottomSheetState
import iad1tya.echo.music.ui.component.shimmer.ShimmerTheme
import iad1tya.echo.music.ui.menu.YouTubeSongMenu
import iad1tya.echo.music.ui.player.BottomSheetPlayer
import iad1tya.echo.music.ui.screens.Screens
import iad1tya.echo.music.ui.screens.SplashScreen
import iad1tya.echo.music.ui.screens.navigationBuilder
import iad1tya.echo.music.ui.screens.search.LocalSearchScreen
import iad1tya.echo.music.ui.screens.search.OnlineSearchScreen
import iad1tya.echo.music.ui.screens.settings.NavigationTab
import iad1tya.echo.music.ui.theme.ColorSaver
import iad1tya.echo.music.ui.theme.DefaultThemeColor
import iad1tya.echo.music.ui.theme.EchoTheme
import iad1tya.echo.music.ui.theme.extractThemeColor
import iad1tya.echo.music.ui.utils.appBarScrollBehavior
import iad1tya.echo.music.ui.utils.backToMain
import iad1tya.echo.music.ui.utils.resetHeightOffset
import iad1tya.echo.music.utils.SyncUtils
import iad1tya.echo.music.utils.dataStore
import iad1tya.echo.music.utils.get
import iad1tya.echo.music.utils.rememberEnumPreference
import iad1tya.echo.music.utils.rememberPreference
import iad1tya.echo.music.utils.reportException
import iad1tya.echo.music.utils.setAppLocale
import iad1tya.echo.music.viewmodels.HomeViewModel
import com.valentinilk.shimmer.LocalShimmerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

@Suppress("DEPRECATION", "ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var database: MusicDatabase

    @Inject
    lateinit var downloadUtil: DownloadUtil

    @Inject
    lateinit var syncUtils: SyncUtils

    private lateinit var navController: NavHostController
    private var pendingIntent: Intent? = null
    private var latestVersionName by mutableStateOf(BuildConfig.VERSION_NAME)

    private var playerConnection by mutableStateOf<PlayerConnection?>(null)

    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
                if (service is MusicBinder) {
                    playerConnection =
                        PlayerConnection(this@MainActivity, service, database, lifecycleScope)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                playerConnection?.dispose()
                playerConnection = null
            }
        }

    override fun onStart() {
        super.onStart()
        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1000)
            }
        }
        startService(Intent(this, MusicService::class.java))
        bindService(
            Intent(this, MusicService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dataStore.get(
                StopMusicOnTaskClearKey,
                false
            ) && playerConnection?.isPlaying?.value == true && isFinishing
        ) {
            stopService(Intent(this, MusicService::class.java))
            unbindService(serviceConnection)
            playerConnection = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (::navController.isInitialized) {
            handleDeepLinkIntent(intent, navController)
        } else {
            pendingIntent = intent
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val locale = dataStore[AppLanguageKey]
                ?.takeUnless { it == SYSTEM_DEFAULT }
                ?.let { Locale.forLanguageTag(it) }
                ?: Locale.getDefault()
            setAppLocale(this, locale)
        }

        lifecycleScope.launch {
            dataStore.data
                .map { it[DisableScreenshotKey] ?: false }
                .distinctUntilChanged()
                .collectLatest {
                    if (it) {
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_SECURE,
                            WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
        }

        setContent {
            val checkForUpdates by rememberPreference(CheckForUpdatesKey, defaultValue = true)

            LaunchedEffect(checkForUpdates) {
                if (checkForUpdates) {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            val url = java.net.URL("https://api.github.com/repos/iad1tya/Echo-Music/releases/latest")
                            val connection = url.openConnection() as java.net.HttpURLConnection
                            connection.requestMethod = "GET"
                            connection.setRequestProperty("Accept", "application/json")
                            
                            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                            connection.disconnect()
                            
                            val json = org.json.JSONObject(responseText)
                            val tagName = json.getString("tag_name")
                            if (tagName.isNotEmpty()) {
                                withContext(Dispatchers.Main) {
                                    latestVersionName = tagName.removePrefix("v")
                                }
                            }
                        }
                    }
                } else {
                    latestVersionName = BuildConfig.VERSION_NAME
                }
            }

            var showSplash by remember { mutableStateOf(true) }

            val enableDynamicTheme by rememberPreference(DynamicThemeKey, defaultValue = true)
            // Force dark mode always on
            val useDarkTheme = true

            LaunchedEffect(useDarkTheme) {
                setSystemBarAppearance(useDarkTheme)
            }

            val pureBlackEnabled by rememberPreference(PureBlackKey, defaultValue = false)
            val pureBlack = remember(pureBlackEnabled, useDarkTheme) {
                pureBlackEnabled && useDarkTheme 
            }

            var themeColor by rememberSaveable(stateSaver = ColorSaver) {
                mutableStateOf(DefaultThemeColor)
            }

            LaunchedEffect(playerConnection, enableDynamicTheme) {
                val playerConnection = playerConnection
                if (!enableDynamicTheme || playerConnection == null) {
                    themeColor = DefaultThemeColor
                    return@LaunchedEffect
                }

                playerConnection.service.currentMediaMetadata.collectLatest { song ->
                    if (song?.thumbnailUrl != null) {
                        withContext(Dispatchers.IO) {
                            try {
                                val result = imageLoader.execute(
                                    ImageRequest.Builder(this@MainActivity)
                                        .data(song.thumbnailUrl)
                                        .allowHardware(false)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .networkCachePolicy(CachePolicy.ENABLED)
                                        .crossfade(false)
                                        .build()
                                )
                                themeColor = result.image?.toBitmap()?.extractThemeColor()
                                    ?: DefaultThemeColor
                            } catch (e: Exception) {
                                // Fallback to default on error
                                themeColor = DefaultThemeColor
                            }
                        }
                    } else {
                        themeColor = DefaultThemeColor
                    }
                }
            }

            EchoTheme(
                darkTheme = useDarkTheme,
                pureBlack = pureBlack,
                themeColor = themeColor,
            ) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = showSplash,
                    exit = fadeOut(animationSpec = tween(300))
                ) {
                    SplashScreen(
                        onTimeout = { showSplash = false }
                    )
                }
                
                androidx.compose.animation.AnimatedVisibility(
                    visible = !showSplash,
                    enter = fadeIn(animationSpec = tween(500))
                ) {
                    BoxWithConstraints(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .background(
                                if (pureBlack) Color.Black else MaterialTheme.colorScheme.surface
                            )
                    ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val focusManager = LocalFocusManager.current
                    val density = LocalDensity.current
                    val configuration = LocalConfiguration.current
                    val cutoutInsets = WindowInsets.displayCutout
                    val windowsInsets = WindowInsets.systemBars
                    val bottomInset = with(density) { windowsInsets.getBottom(density).toDp() }
                    val bottomInsetDp = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

                    val navController = rememberNavController()
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val accountImageUrl by homeViewModel.accountImageUrl.collectAsState()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val (previousTab, setPreviousTab) = rememberSaveable { mutableStateOf("home") }

                    val navigationItems = remember { Screens.MainScreens }
                    val (slimNav) = rememberPreference(SlimNavBarKey, defaultValue = false)
                    val (useNewMiniPlayerDesign) = rememberPreference(UseNewMiniPlayerDesignKey, defaultValue = false)
                    val defaultOpenTab = remember {
                        dataStore[DefaultOpenTabKey].toEnum(defaultValue = NavigationTab.HOME)
                    }
                    val tabOpenedFromShortcut = remember {
                        when (intent?.action) {
                            ACTION_LIBRARY -> NavigationTab.LIBRARY
                            ACTION_SEARCH -> NavigationTab.SEARCH
                            else -> null
                        }
                    }

                    val topLevelScreens = remember {
                        listOf(
                            Screens.Home.route,
                            Screens.Search.route,
                            Screens.Library.route,
                            "settings",
                        )
                    }

                    val (query, onQueryChange) =
                        rememberSaveable(stateSaver = TextFieldValue.Saver) {
                            mutableStateOf(TextFieldValue())
                        }

                    var active by rememberSaveable {
                        mutableStateOf(false)
                    }

                    val onActiveChange: (Boolean) -> Unit = { newActive ->
                        active = newActive
                        if (!newActive) {
                            focusManager.clearFocus()
                            if (navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                                onQueryChange(TextFieldValue())
                            }
                        }
                    }

                    var searchSource by rememberEnumPreference(SearchSourceKey, SearchSource.ONLINE)

                    val searchBarFocusRequester = remember { FocusRequester() }

                    // Speech recognition launcher
                    val speechRecognizerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == RESULT_OK) {
                            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                            if (!spokenText.isNullOrEmpty()) {
                                onQueryChange(TextFieldValue(spokenText))
                                // Automatically search after speech input
                                lifecycleScope.launch {
                                    delay(300)
                                    onActiveChange(false)
                                    navController.navigate("search/${URLEncoder.encode(spokenText, "UTF-8")}")
                                    
                                    if (dataStore[PauseSearchHistoryKey] != true) {
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            database.query {
                                                insert(SearchHistory(query = spokenText))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val onSearch: (String) -> Unit = remember {
                        { searchQuery ->
                            if (searchQuery.isNotEmpty()) {
                                onActiveChange(false)
                                navController.navigate("search/${URLEncoder.encode(searchQuery, "UTF-8")}")

                                if (dataStore[PauseSearchHistoryKey] != true) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        database.query {
                                            insert(SearchHistory(query = searchQuery))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    var openSearchImmediately: Boolean by remember {
                        mutableStateOf(intent?.action == ACTION_SEARCH)
                    }

                    val inSearchScreen = remember(navBackStackEntry) {
                        navBackStackEntry?.destination?.route?.startsWith("search/") == true
                    }

                    val shouldShowSearchBar = remember(active, navBackStackEntry) {
                        active ||
                                navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route } ||
                                inSearchScreen
                    }

                    val shouldShowNavigationBar = remember(navBackStackEntry, active) {
                        navBackStackEntry?.destination?.route == null ||
                                navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route } &&
                                !active
                    }

                    val isLandscape = remember(configuration) {
                        configuration.screenWidthDp > configuration.screenHeightDp
                    }
                    val showRail = isLandscape && !inSearchScreen

                    val getNavPadding: () -> Dp = remember {
                        {
                            if (shouldShowNavigationBar && !showRail) {
                                if (slimNav) SlimNavBarHeight else NavigationBarHeight
                            } else {
                                0.dp
                            }
                        }
                    }

                    val navigationBarHeight by animateDpAsState(
                        targetValue = if (shouldShowNavigationBar && !showRail) NavigationBarHeight else 0.dp,
                        animationSpec = NavigationBarAnimationSpec,
                        label = "",
                    )

                    val playerBottomSheetState =
                        rememberBottomSheetState(
                            dismissedBound = 0.dp,
                            collapsedBound = bottomInset +
                                (if (!showRail && shouldShowNavigationBar) getNavPadding() else 0.dp) +
                                (if (useNewMiniPlayerDesign) MiniPlayerBottomSpacing else 0.dp) +
                                MiniPlayerHeight,
                            expandedBound = maxHeight,
                        )

                    val playerAwareWindowInsets = remember(
                        bottomInset,
                        shouldShowNavigationBar,
                        playerBottomSheetState.isDismissed,
                        showRail,
                    ) {
                        var bottom = bottomInset
                        if (shouldShowNavigationBar && !showRail) {
                            bottom += NavigationBarHeight
                        }
                        if (!playerBottomSheetState.isDismissed) bottom += MiniPlayerHeight
                        windowsInsets
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                            .add(WindowInsets(top = AppBarHeight, bottom = bottom))
                    }

                    appBarScrollBehavior(
                        canScroll = {
                            !inSearchScreen &&
                                    (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                        }
                    )

                    val searchBarScrollBehavior =
                        appBarScrollBehavior(
                            canScroll = {
                                !inSearchScreen &&
                                        (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                            },
                        )
                    val topAppBarScrollBehavior =
                        appBarScrollBehavior(
                            canScroll = {
                                !inSearchScreen &&
                                        (playerBottomSheetState.isCollapsed || playerBottomSheetState.isDismissed)
                            },
                        )

                    // Navigation tracking
                    LaunchedEffect(navBackStackEntry) {
                        if (inSearchScreen) {
                            val searchQuery =
                                withContext(Dispatchers.IO) {
                                    if (navBackStackEntry
                                            ?.arguments
                                            ?.getString(
                                                "query",
                                            )!!
                                            .contains(
                                                "%",
                                            )
                                    ) {
                                        navBackStackEntry?.arguments?.getString(
                                            "query",
                                        )!!
                                    } else {
                                        URLDecoder.decode(
                                            navBackStackEntry?.arguments?.getString("query")!!,
                                            "UTF-8"
                                        )
                                    }
                                }
                            onQueryChange(
                                TextFieldValue(
                                    searchQuery,
                                    TextRange(searchQuery.length)
                                )
                            )
                        } else if (navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                            onQueryChange(TextFieldValue())
                        }

                        // Reset scroll behavior for main navigation items
                        if (navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route }) {
                            if (navigationItems.fastAny { it.route == previousTab }) {
                                searchBarScrollBehavior.state.resetHeightOffset()
                            }
                        }

                        searchBarScrollBehavior.state.resetHeightOffset()
                        topAppBarScrollBehavior.state.resetHeightOffset()

                        // Track previous tab for animations
                        navController.currentBackStackEntry?.destination?.route?.let {
                            setPreviousTab(it)
                        }
                    }

                    LaunchedEffect(active) {
                        if (active) {
                            searchBarScrollBehavior.state.resetHeightOffset()
                            topAppBarScrollBehavior.state.resetHeightOffset()
                            searchBarFocusRequester.requestFocus()
                        }
                    }

                    LaunchedEffect(playerConnection) {
                        val player = playerConnection?.player ?: return@LaunchedEffect
                        if (player.currentMediaItem == null) {
                            if (!playerBottomSheetState.isDismissed) {
                                playerBottomSheetState.dismiss()
                            }
                        } else {
                            if (playerBottomSheetState.isDismissed) {
                                playerBottomSheetState.collapseSoft()
                            }
                        }
                    }

                    DisposableEffect(playerConnection, playerBottomSheetState) {
                        val player =
                            playerConnection?.player ?: return@DisposableEffect onDispose { }
                        val listener =
                            object : Player.Listener {
                                override fun onMediaItemTransition(
                                    mediaItem: MediaItem?,
                                    reason: Int,
                                ) {
                                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED &&
                                        mediaItem != null &&
                                        playerBottomSheetState.isDismissed
                                    ) {
                                        playerBottomSheetState.collapseSoft()
                                    }
                                }
                            }
                        player.addListener(listener)
                        onDispose {
                            player.removeListener(listener)
                        }
                    }

                    var shouldShowTopBar by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(navBackStackEntry) {
                        shouldShowTopBar =
                            !active && navBackStackEntry?.destination?.route in topLevelScreens && navBackStackEntry?.destination?.route != "settings"
                    }

                    val coroutineScope = rememberCoroutineScope()
                    var sharedSong: SongItem? by remember {
                        mutableStateOf(null)
                    }

                    LaunchedEffect(Unit) {
                        if (pendingIntent != null) {
                            handleDeepLinkIntent(pendingIntent!!, navController)
                            pendingIntent = null
                        } else {
                            handleDeepLinkIntent(intent, navController)
                        }
                    }

                    DisposableEffect(Unit) {
                        val listener = Consumer<Intent> { intent ->
                            handleDeepLinkIntent(intent, navController)
                        }

                        addOnNewIntentListener(listener)
                        onDispose { removeOnNewIntentListener(listener) }
                    }

                    val currentTitleRes = remember(navBackStackEntry) {
                        when (navBackStackEntry?.destination?.route) {
                            Screens.Home.route -> R.string.home
                            Screens.Search.route -> R.string.search
                            Screens.Library.route -> R.string.filter_library
                            else -> null
                        }
                    }

                    var showAccountDialog by remember { mutableStateOf(false) }

                    val baseBg = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
                    val insetBg = if (playerBottomSheetState.progress > 0f) Color.Transparent else baseBg

                    CompositionLocalProvider(
                        LocalDatabase provides database,
                        LocalContentColor provides if (pureBlack) Color.White else contentColorFor(MaterialTheme.colorScheme.surface),
                        LocalPlayerConnection provides playerConnection,
                        LocalPlayerAwareWindowInsets provides playerAwareWindowInsets,
                        LocalDownloadUtil provides downloadUtil,
                        LocalShimmerTheme provides ShimmerTheme,
                        LocalSyncUtils provides syncUtils,
                    ) {
                        Scaffold(
                            topBar = {
                                AnimatedVisibility(
                                    visible = shouldShowTopBar,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { -it / 4 },
                                        animationSpec = tween(durationMillis = 100)
                                    ) + fadeIn(animationSpec = tween(durationMillis = 100)),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { -it / 4 },
                                        animationSpec = tween(durationMillis = 100)
                                    ) + fadeOut(animationSpec = tween(durationMillis = 100))
                                ) {
                                    Row {
                                        TopAppBar(
                                            title = {
                                                Text(
                                                    text = currentTitleRes?.let { stringResource(it) } ?: "",
                                                    style = MaterialTheme.typography.titleLarge.copy(
                                                        fontFamily = FontFamily(Font(R.font.zalando_sans_expanded)),
                                                        fontWeight = FontWeight.Bold
                                                    ),
                                                )
                                            },
                                            actions = {
                                                IconButton(onClick = { navController.navigate("history") }) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.history),
                                                        contentDescription = stringResource(R.string.history)
                                                    )
                                                }
                                                IconButton(onClick = { navController.navigate("stats") }) {
                                                    Icon(
                                                        painter = painterResource(R.drawable.stats),
                                                        contentDescription = stringResource(R.string.stats)
                                                    )
                                                }
                                                IconButton(onClick = { showAccountDialog = true }) {
                                                    BadgedBox(badge = {
                                                        if (latestVersionName != BuildConfig.VERSION_NAME) {
                                                            Badge()
                                                        }
                                                    }) {
                                                        if (accountImageUrl != null) {
                                                            AsyncImage(
                                                                model = accountImageUrl,
                                                                contentDescription = stringResource(R.string.account),
                                                                modifier = Modifier
                                                                    .size(24.dp)
                                                                    .clip(CircleShape)
                                                            )
                                                        } else {
                                                            Icon(
                                                                painter = painterResource(R.drawable.account),
                                                                contentDescription = stringResource(R.string.account),
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            },
                                            scrollBehavior = searchBarScrollBehavior,
                                            colors = TopAppBarDefaults.topAppBarColors(
                                                containerColor = if (useDarkTheme) Color.Black else Color.White,
                                                scrolledContainerColor = if (useDarkTheme) Color.Black else Color.White,
                                                titleContentColor = MaterialTheme.colorScheme.onSurface,
                                                actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.windowInsetsPadding(
                                                if (showRail) {
                                                    WindowInsets(left = NavigationBarHeight)
                                                        .add(cutoutInsets.only(WindowInsetsSides.Start))
                                                } else {
                                                    cutoutInsets.only(WindowInsetsSides.Start + WindowInsetsSides.End)
                                                }
                                            )
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    visible = active || inSearchScreen,
                                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(150)),
                                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(100))
                                ) {
                                    TopSearch(
                                        query = query,
                                        onQueryChange = onQueryChange,
                                        onSearch = onSearch,
                                        active = active,
                                        onActiveChange = onActiveChange,
                                        placeholder = {
                                            Text(
                                                text = stringResource(
                                                    when (searchSource) {
                                                        SearchSource.LOCAL -> R.string.search_library
                                                        SearchSource.ONLINE -> R.string.search_yt_music
                                                    }
                                                ),
                                            )
                                        },
                                        leadingIcon = {
                                            IconButton(
                                                onClick = {
                                                    when {
                                                        active -> onActiveChange(false)
                                                        !navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route } -> {
                                                            navController.navigateUp()
                                                        }

                                                        else -> onActiveChange(true)
                                                    }
                                                },
                                                onLongClick = {
                                                    when {
                                                        active -> {}
                                                        !navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route } -> {
                                                            navController.backToMain()
                                                        }
                                                        else -> {}
                                                    }
                                                },
                                            ) {
                                                Icon(
                                                    painterResource(
                                                        if (active ||
                                                            !navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route }
                                                        ) {
                                                            R.drawable.arrow_back
                                                        } else {
                                                            R.drawable.search
                                                        },
                                                    ),
                                                    contentDescription = null,
                                                )
                                            }
                                        },
                                        trailingIcon = {
                                            Row {
                                                if (active) {
                                                    if (query.text.isNotEmpty()) {
                                                        IconButton(
                                                            onClick = {
                                                                onQueryChange(
                                                                    TextFieldValue(
                                                                        ""
                                                                    )
                                                               )
                                                            },
                                                        ) {
                                                            Icon(
                                                                painter = painterResource(R.drawable.close),
                                                                contentDescription = null,
                                                            )
                                                        }
                                                    }
                                                    // Microphone button for voice search
                                                    IconButton(
                                                        onClick = {
                                                            try {
                                                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())
                                                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Say the song name")
                                                                }
                                                                speechRecognizerLauncher.launch(intent)
                                                            } catch (e: Exception) {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Speech recognition not available",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        },
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(R.drawable.mic),
                                                            contentDescription = "Voice search",
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = {
                                                            searchSource =
                                                                if (searchSource == SearchSource.ONLINE) SearchSource.LOCAL else SearchSource.ONLINE
                                                        },
                                                    ) {
                                                        Icon(
                                                            painter = painterResource(
                                                                when (searchSource) {
                                                                    SearchSource.LOCAL -> R.drawable.library_music
                                                                    SearchSource.ONLINE -> R.drawable.language
                                                                },
                                                            ),
                                                            contentDescription = null,
                                                        )
                                                    }
                                                }
                                            }
                                        },
                                        focusRequester = searchBarFocusRequester,
                                        modifier = Modifier
                                            .align(Alignment.TopCenter)
                                            .windowInsetsPadding(
                                                if (showRail) {
                                                    WindowInsets(left = NavigationBarHeight)
                                                } else {
                                                    WindowInsets(0.dp)
                                                }
                                            ),
                                        colors = if (pureBlack && active) {
                                            SearchBarDefaults.colors(
                                                containerColor = Color.Black,
                                                dividerColor = Color.DarkGray,
                                                inputFieldColors = TextFieldDefaults.colors(
                                                    focusedTextColor = Color.White,
                                                    unfocusedTextColor = Color.Gray,
                                                    focusedContainerColor = Color.Transparent,
                                                    unfocusedContainerColor = Color.Transparent,
                                                    cursorColor = Color.White,
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                )
                                            )
                                        } else {
                                            SearchBarDefaults.colors(
                                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                            )
                                        }
                                    ) {
                                        // Disable crossfade transitions as requested
                                        Crossfade(
                                            targetState = searchSource,
                                            label = "",
                                            animationSpec = tween(150),
                                            modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .padding(bottom = if (!playerBottomSheetState.isDismissed) MiniPlayerHeight else 0.dp)
                                                .navigationBarsPadding(),
                                        ) { searchSource ->
                                            when (searchSource) {
                                                SearchSource.LOCAL ->
                                                    LocalSearchScreen(
                                                        query = query.text,
                                                        navController = navController,
                                                        onDismiss = { onActiveChange(false) },
                                                        pureBlack = pureBlack,
                                                    )

                                                SearchSource.ONLINE ->
                                                    OnlineSearchScreen(
                                                        query = query.text,
                                                        onQueryChange = onQueryChange,
                                                        navController = navController,
                                                        onSearch = { searchQuery ->
                                                            navController.navigate(
                                                                "search/${URLEncoder.encode(searchQuery, "UTF-8")}"
                                                            )
                                                            if (dataStore[PauseSearchHistoryKey] != true) {
                                                                lifecycleScope.launch(Dispatchers.IO) {
                                                                    database.query {
                                                                        insert(SearchHistory(query = searchQuery))
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        onDismiss = { onActiveChange(false) },
                                                        pureBlack = pureBlack
                                                    )
                                            }
                                        }
                                    }
                                }
                            },
                            bottomBar = {
                                if (!showRail) {
                                    Box {
                                        BottomSheetPlayer(
                                            state = playerBottomSheetState,
                                            navController = navController,
                                            pureBlack = pureBlack
                                        )
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .height(bottomInset + getNavPadding())
                                                .fillMaxWidth()
                                                .offset {
                                                    if (navigationBarHeight == 0.dp) {
                                                        IntOffset(
                                                            x = 0,
                                                            y = (bottomInset + NavigationBarHeight).roundToPx(),
                                                        )
                                                    } else {
                                                        val slideOffset =
                                                            (bottomInset + NavigationBarHeight) *
                                                                    playerBottomSheetState.progress.coerceIn(
                                                                        0f,
                                                                        1f,
                                                                    )
                                                        val hideOffset =
                                                            (bottomInset + NavigationBarHeight) * (1 - navigationBarHeight / NavigationBarHeight)
                                                        IntOffset(
                                                            x = 0,
                                                            y = (slideOffset + hideOffset).roundToPx(),
                                                        )
                                                    }
                                                }
                                                .background(if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer)
                                        ) {
                                        NavigationBar(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .height(bottomInset + getNavPadding()),
                                            containerColor = Color.Transparent,
                                            contentColor = if (pureBlack) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        ) {
                                            navigationItems.fastForEach { screen ->
                                                val isSelected =
                                                    navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true

                                                NavigationBarItem(
                                                    selected = isSelected,
                                                    icon = {
                                                        Icon(
                                                            painter = painterResource(
                                                                id = if (isSelected) screen.iconIdActive else screen.iconIdInactive
                                                            ),
                                                            contentDescription = null,
                                                        )
                                                    },
                                                    label = {
                                                        if (!slimNav) {
                                                            Text(
                                                                text = stringResource(screen.titleId),
                                                                maxLines = 1,
                                                                overflow = TextOverflow.Ellipsis
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        if (screen.route == Screens.Search.route) {
                                                            onActiveChange(true)
                                                        } else if (isSelected) {
                                                            navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                                            coroutineScope.launch {
                                                                searchBarScrollBehavior.state.resetHeightOffset()
                                                            }
                                                        } else {
                                                            navController.navigate(screen.route) {
                                                                popUpTo(navController.graph.startDestinationId) {
                                                                    saveState = true
                                                                }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .background(insetBg)
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .height(bottomInsetDp)
                                        )
                                    }
                                } else {
                                    BottomSheetPlayer(
                                        state = playerBottomSheetState,
                                        navController = navController,
                                        pureBlack = pureBlack
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(insetBg)
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter)
                                            .height(bottomInsetDp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(searchBarScrollBehavior.nestedScrollConnection)
                        ) {
                            Row(Modifier.fillMaxSize()) {
                                if (showRail) {
                                    NavigationRail(
                                        containerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
                                    ) {
                                        Spacer(modifier = Modifier.weight(1f))

                                        navigationItems.fastForEach { screen ->
                                            val isSelected =
                                                navBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true
                                            NavigationRailItem(
                                                selected = isSelected,
                                                onClick = {
                                                    if (screen.route == Screens.Search.route) {
                                                        onActiveChange(true)
                                                    } else if (isSelected) {
                                                        navController.currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
                                                        coroutineScope.launch {
                                                            searchBarScrollBehavior.state.resetHeightOffset()
                                                        }
                                                    } else {
                                                        navController.navigate(screen.route) {
                                                            popUpTo(navController.graph.startDestinationId) {
                                                                inclusive = false
                                                            }
                                                            launchSingleTop = true
                                                            restoreState = false
                                                        }
                                                    }
                                                },
                                                icon = {
                                                    Icon(
                                                        painter = painterResource(
                                                            id = if (isSelected) screen.iconIdActive else screen.iconIdInactive
                                                        ),
                                                        contentDescription = null,
                                                    )
                                                },
                                            )
                                        }

                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                                Box(Modifier.weight(1f)) {
                                    // NavHost with animations
                                    NavHost(
                                        navController = navController,
                                        startDestination = when (tabOpenedFromShortcut ?: defaultOpenTab) {
                                            NavigationTab.HOME -> Screens.Home
                                            NavigationTab.LIBRARY -> Screens.Library
                                            else -> Screens.Home
                                        }.route,
                                        // Enter Transition
                                        enterTransition = {
                                            val currentRouteIndex = navigationItems.indexOfFirst {
                                                it.route == targetState.destination.route
                                            }
                                            val previousRouteIndex = navigationItems.indexOfFirst {
                                                it.route == initialState.destination.route
                                            }

                                            if (currentRouteIndex == -1 || currentRouteIndex > previousRouteIndex)
                                                slideInHorizontally { it / 4 } + fadeIn(tween(150))
                                            else
                                                slideInHorizontally { -it / 4 } + fadeIn(tween(150))
                                        },
                                        // Exit Transition
                                        exitTransition = {
                                            val currentRouteIndex = navigationItems.indexOfFirst {
                                                it.route == initialState.destination.route
                                            }
                                            val targetRouteIndex = navigationItems.indexOfFirst {
                                                it.route == targetState.destination.route
                                            }

                                            if (targetRouteIndex == -1 || targetRouteIndex > currentRouteIndex)
                                                slideOutHorizontally { -it / 4 } + fadeOut(tween(100))
                                            else
                                                slideOutHorizontally { it / 4 } + fadeOut(tween(100))
                                        },
                                        // Pop Enter Transition
                                        popEnterTransition = {
                                            val currentRouteIndex = navigationItems.indexOfFirst {
                                                it.route == targetState.destination.route
                                            }
                                            val previousRouteIndex = navigationItems.indexOfFirst {
                                                it.route == initialState.destination.route
                                            }

                                            if (previousRouteIndex != -1 && previousRouteIndex < currentRouteIndex)
                                                slideInHorizontally { it / 4 } + fadeIn(tween(150))
                                            else
                                                slideInHorizontally { -it / 4 } + fadeIn(tween(150))
                                        },
                                        // Pop Exit Transition
                                        popExitTransition = {
                                            val currentRouteIndex = navigationItems.indexOfFirst {
                                                it.route == initialState.destination.route
                                            }
                                            val targetRouteIndex = navigationItems.indexOfFirst {
                                                it.route == targetState.destination.route
                                            }

                                            if (currentRouteIndex != -1 && currentRouteIndex < targetRouteIndex)
                                                slideOutHorizontally { -it / 4 } + fadeOut(tween(100))
                                            else
                                                slideOutHorizontally { it / 4 } + fadeOut(tween(100))
                                        },
                                        modifier = Modifier.nestedScroll(
                                            if (navigationItems.fastAny { it.route == navBackStackEntry?.destination?.route } ||
                                                inSearchScreen
                                            ) {
                                                searchBarScrollBehavior.nestedScrollConnection
                                            } else {
                                                topAppBarScrollBehavior.nestedScrollConnection
                                            }
                                        )
                                    ) {
                                        navigationBuilder(
                                            navController,
                                            topAppBarScrollBehavior,
                                            latestVersionName
                                        )
                                    }
                                }
                            }
                        }

                        BottomSheetMenu(
                            state = LocalMenuState.current,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )

                        BottomSheetPage(
                            state = LocalBottomSheetPageState.current,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )

                        if (showAccountDialog) {
                            AccountSettingsDialog(
                                navController = navController,
                                onDismiss = {
                                    showAccountDialog = false
                                    homeViewModel.refresh()
                                },
                                latestVersionName = latestVersionName
                            )
                        }

                        sharedSong?.let { song ->
                            playerConnection?.let {
                                Dialog(
                                    onDismissRequest = { sharedSong = null },
                                    properties = DialogProperties(usePlatformDefaultWidth = false),
                                ) {
                                    Surface(
                                        modifier = Modifier.padding(24.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        color = AlertDialogDefaults.containerColor,
                                        tonalElevation = AlertDialogDefaults.TonalElevation,
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            YouTubeSongMenu(
                                                song = song,
                                                navController = navController,
                                                onDismiss = { sharedSong = null },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    LaunchedEffect(shouldShowSearchBar, openSearchImmediately) {
                        if (shouldShowSearchBar && openSearchImmediately) {
                            onActiveChange(true)
                            searchBarFocusRequester.requestFocus()
                            openSearchImmediately = false
                        }
                    }
                }
                }
            }
        }
    }

    private fun handleDeepLinkIntent(intent: Intent, navController: NavHostController) {
        // Handle both VIEW and SEND actions
        val uri = when (intent.action) {
            Intent.ACTION_SEND -> {
                // Handle shared text (from YouTube share)
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                    // Extract URL from shared text
                    val urlPattern = "(https?://[^\\s]+)".toRegex()
                    urlPattern.find(sharedText)?.value?.toUri()
                }
            }
            Intent.ACTION_VIEW -> intent.data
            else -> intent.data ?: intent.extras?.getString(Intent.EXTRA_TEXT)?.toUri()
        } ?: return
        
        val coroutineScope = lifecycleScope

        when (val path = uri.pathSegments.firstOrNull()) {
            "playlist" -> uri.getQueryParameter("list")?.let { playlistId ->
                if (playlistId.startsWith("OLAK5uy_")) {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.albumSongs(playlistId).onSuccess { songs ->
                            songs.firstOrNull()?.album?.id?.let { browseId ->
                                withContext(Dispatchers.Main) {
                                    navController.navigate("album/$browseId")
                                }
                            }
                        }.onFailure { reportException(it) }
                    }
                } else {
                    navController.navigate("online_playlist/$playlistId")
                }
            }

            "browse" -> uri.lastPathSegment?.let { browseId ->
                navController.navigate("album/$browseId")
            }

            "channel", "c" -> uri.lastPathSegment?.let { artistId ->
                navController.navigate("artist/$artistId")
            }

            else -> {
                val videoId = when {
                    path == "watch" -> uri.getQueryParameter("v")
                    uri.host == "youtu.be" -> uri.pathSegments.firstOrNull()
                    else -> null
                }

                val playlistId = uri.getQueryParameter("list")

                videoId?.let {
                    coroutineScope.launch(Dispatchers.IO) {
                        YouTube.queue(listOf(it), playlistId).onSuccess { queue ->
                            withContext(Dispatchers.Main) {
                                playerConnection?.playQueue(
                                    YouTubeQueue(
                                        WatchEndpoint(videoId = queue.firstOrNull()?.id, playlistId = playlistId),
                                        queue.firstOrNull()?.toMediaMetadata()
                                    )
                                )
                            }
                        }.onFailure {
                            reportException(it)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setSystemBarAppearance(isDark: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView.rootView).apply {
            isAppearanceLightStatusBars = !isDark
            isAppearanceLightNavigationBars = !isDark
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window.statusBarColor =
                (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            window.navigationBarColor =
                (if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.2f)).toArgb()
        }
    }

    companion object {
        const val ACTION_SEARCH = "iad1tya.echo.music.action.SEARCH"
        const val ACTION_LIBRARY = "iad1tya.echo.music.action.LIBRARY"
    }
}

val LocalDatabase = staticCompositionLocalOf<MusicDatabase> { error("No database provided") }
val LocalPlayerConnection =
    staticCompositionLocalOf<PlayerConnection?> { error("No PlayerConnection provided") }
val LocalPlayerAwareWindowInsets =
    compositionLocalOf<WindowInsets> { error("No WindowInsets provided") }
val LocalDownloadUtil = staticCompositionLocalOf<DownloadUtil> { error("No DownloadUtil provided") }
val LocalSyncUtils = staticCompositionLocalOf<SyncUtils> { error("No SyncUtils provided") }
