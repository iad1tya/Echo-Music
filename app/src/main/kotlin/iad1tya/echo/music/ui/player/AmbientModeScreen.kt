package iad1tya.echo.music.ui.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.component.Lyrics
import iad1tya.echo.music.utils.rememberEnumPreference
import iad1tya.echo.music.constants.PlayerBackgroundStyleKey
import iad1tya.echo.music.constants.PlayerBackgroundStyle

@Composable
fun AmbientModeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val playerConnection = LocalPlayerConnection.current ?: return
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    
    // State for options
    var showAlbumArt by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(false) }

    // Force Landscape and Fullscreen
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        // Hide system bars and dim screen
        activity?.window?.let { window ->
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            
            val layoutParams = window.attributes
            layoutParams.screenBrightness = 0.01f
            window.attributes = layoutParams
        }

        onDispose {
            activity?.requestedOrientation = originalOrientation
            activity?.window?.let { window ->
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                
                val layoutParams = window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                window.attributes = layoutParams
            }
        }
    }

    BackHandler {
        navController.popBackStack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album Art Section (Left, if enabled)
            AnimatedVisibility(visible = showAlbumArt) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    mediaMetadata?.let { metadata ->
                        AsyncImage(
                            model = metadata.thumbnailUrl,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Lyrics Section
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(32.dp)
            ) {
                // We need to override the text color in Lyrics composable. 
                // Since Lyrics composable reads from preferences or themes, we might need a wrapper 
                // or pass a color modifier if supported. 
                // Looking at Lyrics.kt, it uses MaterialTheme.typography which uses LocalContentColor.
                // So wrapping it in a CompositionLocalProvider for LocalContentColor might work?
                // Actually, Lyrics.kt might handle its own colors.
                // Let's try CompositionLocalProvider(LocalContentColor provides lyricsColor)
                
                Lyrics(
                    sliderPositionProvider = { playerConnection.player.currentPosition },
                    isVisible = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Controls Overlay (Replaced by Floating Settings Icon)
        /*
        AnimatedVisibility(
            visible = areControlsVisible,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { ... }
        */
        
        // Permanent Settings Icon (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            IconButton(onClick = { areControlsVisible = true }) {
                Icon(
                    painter = painterResource(R.drawable.settings_outlined),
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.5f), // Dimmed to prevent burn-in
                    modifier = Modifier.size(24.dp)
                )
            }
            
            DropdownMenu(
                expanded = areControlsVisible,
                onDismissRequest = { areControlsVisible = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                DropdownMenuItem(
                    text = { Text("Album Art") },
                    onClick = { 
                        showAlbumArt = !showAlbumArt
                        areControlsVisible = false 
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(if (showAlbumArt) R.drawable.insert_photo else R.drawable.hide_image),
                            contentDescription = null
                        )
                    }
                )
                DropdownMenuItem(
                    text = { Text("Exit Ambient Mode") },
                    onClick = { 
                        areControlsVisible = false
                        navController.popBackStack() 
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
