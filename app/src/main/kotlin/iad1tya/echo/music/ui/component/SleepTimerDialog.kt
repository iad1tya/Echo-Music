package iad1tya.echo.music.ui.component

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import iad1tya.echo.music.LocalPlayerConnection
import iad1tya.echo.music.R
import kotlin.math.roundToInt

@Composable
fun SleepTimerDialog(
    onDismiss: () -> Unit
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val sheetBackgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
    val sheetContentColor = MaterialTheme.colorScheme.onSurface
    
    var sleepTimerValue by remember { mutableFloatStateOf(30f) }
    
    val scrollState = rememberLazyListState()
    val isScrolled by remember { derivedStateOf { scrollState.canScrollBackward } }
    val headerAlpha by animateFloatAsState(
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
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 80.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 80.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                         Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        ) {

                            
                            Text(
                                text = pluralStringResource(
                                    R.plurals.minute,
                                    sleepTimerValue.roundToInt(),
                                    sleepTimerValue.roundToInt()
                                ),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Text(
                                text = stringResource(R.string.sleep_timer),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            Slider(
                                value = sleepTimerValue,
                                onValueChange = { sleepTimerValue = it },
                                valueRange = 5f..120f,
                                steps = (120 - 5) / 5 - 1,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "5 min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "120 min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        FilledTonalButton(
                            onClick = {
                                onDismiss()
                                playerConnection.service.sleepTimer.start(-1)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {

                            Text(stringResource(R.string.end_of_song))
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                onDismiss()
                                playerConnection.service.sleepTimer.start(sleepTimerValue.roundToInt())
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Start Timer")
                        }
                    }
                }

                // Header Background (Foggy Blur)
                if (headerAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.TopCenter)
                            .zIndex(1f)
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

                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .zIndex(2f)
                        .padding(WindowInsets.systemBars.asPaddingValues())
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Sleep Timer",
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
