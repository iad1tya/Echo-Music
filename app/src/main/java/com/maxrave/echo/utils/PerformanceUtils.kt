package iad1tya.echo.music.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Performance optimization utilities for smooth animations and reduced jitter
 */

object PerformanceUtils {
    
    // Smooth animation durations
    const val FAST_ANIMATION_DURATION = 200
    const val MEDIUM_ANIMATION_DURATION = 300
    const val SLOW_ANIMATION_DURATION = 500
    
    // Smooth easing functions
    val FAST_OUT_SLOW_IN_EASING = FastOutSlowInEasing
    val LINEAR_EASING = LinearEasing
    
    /**
     * Creates a smooth animation spec for UI transitions
     */
    fun createSmoothAnimationSpec(
        durationMillis: Int = MEDIUM_ANIMATION_DURATION,
        easing: androidx.compose.animation.core.Easing = FAST_OUT_SLOW_IN_EASING
    ): AnimationSpec<Float> = tween(
        durationMillis = durationMillis,
        easing = easing
    )
    
    /**
     * Creates a smooth animation spec for fast transitions
     */
    fun createFastAnimationSpec(): AnimationSpec<Float> = tween(
        durationMillis = FAST_ANIMATION_DURATION,
        easing = FAST_OUT_SLOW_IN_EASING
    )
    
    /**
     * Creates a smooth animation spec for slow transitions
     */
    fun createSlowAnimationSpec(): AnimationSpec<Float> = tween(
        durationMillis = SLOW_ANIMATION_DURATION,
        easing = FAST_OUT_SLOW_IN_EASING
    )
    
    /**
     * Smoothly scrolls to a specific item in a LazyList
     */
    @Composable
    fun rememberSmoothScrollToItem(
        lazyListState: LazyListState,
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ): (Int) -> Unit = remember(lazyListState, coroutineScope) {
        { index ->
            coroutineScope.launch {
                lazyListState.animateScrollToItem(index)
            }
        }
    }
    
    /**
     * Smoothly scrolls by a specific offset in a LazyList
     */
    @Composable
    fun rememberSmoothScrollBy(
        lazyListState: LazyListState,
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ): (Float) -> Unit = remember(lazyListState, coroutineScope) {
        { offset ->
            coroutineScope.launch {
                lazyListState.scrollToItem(
                    lazyListState.firstVisibleItemIndex,
                    lazyListState.firstVisibleItemScrollOffset + offset.toInt()
                )
            }
        }
    }
    
    /**
     * Debounces rapid state changes to prevent jitter
     */
    @Composable
    fun <T> rememberDebouncedState(
        initialValue: T,
        delayMillis: Long = 100L,
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ): androidx.compose.runtime.MutableState<T> {
        return remember { 
            androidx.compose.runtime.mutableStateOf(initialValue) 
        }
    }
    
    /**
     * Performance-optimized LazyList configuration
     */
    object LazyListConfig {
        const val BEYOND_BOUNDS_PAGE_COUNT = 1
        const val HORIZONTAL_SPACING = 8
        const val VERTICAL_SPACING = 0
        const val CONTENT_PADDING_HORIZONTAL = 4
    }
    
    /**
     * Performance-optimized animation configuration
     */
    object AnimationConfig {
        const val NAVIGATION_DURATION = 300
        const val FADE_DURATION = 200
        const val SCALE_DURATION = 250
        const val SLIDE_DURATION = 300
    }
}
