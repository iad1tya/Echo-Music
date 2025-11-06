package iad1tya.echo.music.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import iad1tya.echo.music.LocalPlayerAwareWindowInsets
import iad1tya.echo.music.ui.utils.isScrollingUp

@Composable
fun BoxScope.HideOnScrollFAB(
    visible: Boolean = true,
    lazyListState: LazyListState,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    val useDarkTheme = isSystemInDarkTheme()
    AnimatedVisibility(
        visible = visible && lazyListState.isScrollingUp(),
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier =
        Modifier
            .align(Alignment.BottomEnd)
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Horizontal),
            ),
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp)
                .border(
                    width = 1.dp,
                    color = if (useDarkTheme) 
                        Color.White.copy(alpha = 0.15f) 
                    else 
                        Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(28.dp)
                ),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun BoxScope.HideOnScrollFAB(
    visible: Boolean = true,
    lazyListState: LazyGridState,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    val useDarkTheme = isSystemInDarkTheme()
    AnimatedVisibility(
        visible = visible && lazyListState.isScrollingUp(),
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(250, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ),
        modifier =
        Modifier
            .align(Alignment.BottomEnd)
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Horizontal),
            ),
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp)
                .border(
                    width = 1.dp,
                    color = if (useDarkTheme) 
                        Color.White.copy(alpha = 0.15f) 
                    else 
                        Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(28.dp)
                ),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun BoxScope.HideOnScrollFAB(
    visible: Boolean = true,
    scrollState: ScrollState,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
) {
    val useDarkTheme = isSystemInDarkTheme()
    AnimatedVisibility(
        visible = visible && scrollState.isScrollingUp(),
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(250, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200, easing = FastOutSlowInEasing)
        ),
        modifier =
        Modifier
            .align(Alignment.BottomEnd)
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current
                    .only(WindowInsetsSides.Horizontal),
            ),
    ) {
        FloatingActionButton(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 150.dp)
                .border(
                    width = 1.dp,
                    color = if (useDarkTheme) 
                        Color.White.copy(alpha = 0.15f) 
                    else 
                        Color.Black.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(28.dp)
                ),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        }
    }
}
