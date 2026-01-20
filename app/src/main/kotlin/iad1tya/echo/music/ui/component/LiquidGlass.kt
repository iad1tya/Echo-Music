package iad1tya.echo.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import kotlin.math.sign
import com.kyant.backdrop.backdrops.layerBackdrop as nativeBackdrop
import androidx.compose.runtime.staticCompositionLocalOf

typealias PlatformBackdrop = LayerBackdrop

val LocalBackdrop = staticCompositionLocalOf<PlatformBackdrop?> { null }
val LocalLayer = staticCompositionLocalOf<GraphicsLayer?> { null }
val LocalLuminance = staticCompositionLocalOf<Float> { 0f }

@Composable
fun rememberBackdrop(): PlatformBackdrop = rememberLayerBackdrop {
    drawRect(Color.Black)
    drawContent()
}

fun Modifier.layerBackdrop(backdrop: PlatformBackdrop): Modifier = this.nativeBackdrop(backdrop)

fun Modifier.drawBackdropCustomShape(
    backdrop: PlatformBackdrop,
    layer: GraphicsLayer,
    luminanceAnimation: Float,
    shape: Shape,
    surfaceAlpha: Float = 0.1f,
    useLens: Boolean = true,
    customBlur: androidx.compose.ui.unit.Dp? = null,
): Modifier {
    return this.drawBackdrop(
        backdrop = backdrop,
        effects = {
            val l = (luminanceAnimation * 2f - 1f).let { sign(it) * it * it }
            vibrancy()
            colorControls(
                brightness =
                    if (l > 0f) {
                        lerp(0.1f, 0.5f, l)
                    } else {
                        lerp(0.1f, -0.2f, -l)
                    },
                contrast =
                    if (l > 0f) {
                        lerp(1f, 0f, l)
                    } else {
                        1f
                    },
                saturation = 1.5f,
            )
            blur(
                if (customBlur != null) {
                    customBlur.toPx()
                } else if (l > 0f) {
                    lerp(8f.dp.toPx(), 16f.dp.toPx(), l)
                } else {
                    lerp(8f.dp.toPx(), 2f.dp.toPx(), -l)
                },
            )
        },
        onDrawBackdrop = { drawBackdrop ->
            drawBackdrop()
            try {
                layer.record { drawBackdrop() }
            } catch (e: Throwable) {
                // Ignore exceptions during recording to prevent crashes
            }
        },
        shape = { shape },
        onDrawSurface = { drawRect(Color.Black.copy(alpha = surfaceAlpha)) }
    )
}

/**
 * Optimized version of drawBackdropCustomShape for menus.
 * Removes expensive vibrancy and color controls to improve performance during animations.
 * Keeps only blur and basic dimming.
 */
fun Modifier.drawOptimizedGlass(
    backdrop: PlatformBackdrop,
    layer: GraphicsLayer,
    shape: Shape,
    surfaceAlpha: Float = 0.4f, // Slightly higher alpha to compensate for lack of other effects
): Modifier {
    return this.drawBackdrop(
        backdrop = backdrop,
        effects = {
            // Only apply blur, skip expensive color matrix operations
            blur(16.dp.toPx())
        },
        onDrawBackdrop = { drawBackdrop ->
            drawBackdrop()
            try {
                layer.record { drawBackdrop() }
            } catch (e: IllegalStateException) {
                // Ignore "Attempting to drawContent for a null node" during transitions
            }
        },
        shape = { shape },
        onDrawSurface = { drawRect(Color.Black.copy(alpha = surfaceAlpha)) }
    )
}
