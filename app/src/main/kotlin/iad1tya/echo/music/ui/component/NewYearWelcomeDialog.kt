package iad1tya.echo.music.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import iad1tya.echo.music.R
import kotlin.random.Random

@Composable
fun NewYearWelcomeDialog(
    onDismiss: () -> Unit,
    onSeeWrap: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        
        // Gentle rotation for background elements
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(60000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        
        // Floating animation for text
        val floatOffset by infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 10f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val zalandoFont = FontFamily(
            Font(R.font.zalando_sans_expanded_bold, FontWeight.Bold),
            Font(R.font.zalando_sans_expanded_medium, FontWeight.Medium),
            Font(R.font.zalando_sans_expanded_regular, FontWeight.Normal)
        )

        // Generate stars once
        val stars = remember {
            List(80) {
                StarProp(
                    x = Random.nextFloat(),
                    y = Random.nextFloat(),
                    radius = Random.nextFloat() * 4f + 1f,
                    alpha = Random.nextFloat() * 0.7f + 0.3f
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F0C29), // Night dark
                            Color(0xFF302B63), // Deep purple
                            Color(0xFF24243E)  // Dark slate
                        )
                    )
                )
        ) {
            // Animated Stars Background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                
                stars.forEach { star ->
                    drawCircle(
                        color = Color.White.copy(alpha = star.alpha),
                        center = Offset(star.x * canvasWidth, star.y * canvasHeight),
                        radius = star.radius.dp.toPx()
                    )
                }
            }

            // Glow effects
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(300.dp)
                    .offset(x = 100.dp, y = (-50).dp)
                    .blur(80.dp)
                    .background(Color(0xFF8E2DE2).copy(alpha = 0.3f), shape = RoundedCornerShape(100))
            )
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(300.dp)
                    .offset(x = (-100).dp, y = 100.dp)
                    .blur(80.dp)
                    .background(Color(0xFF4A00E0).copy(alpha = 0.3f), shape = RoundedCornerShape(100))
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp)
                    .graphicsLayer { translationY = floatOffset }, // Add floating effect
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Happy New Year!",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = zalandoFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 42.sp,
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "May this year bring prosperity\nand good health.",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = zalandoFont,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 22.sp,
                        lineHeight = 30.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                 Text(
                    text = "Thank you for being a part of Echo Music.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = zalandoFont,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(56.dp))
                
                Button(
                    onClick = onSeeWrap,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "See Your Wrap",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = zalandoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

private data class StarProp(val x: Float, val y: Float, val radius: Float, val alpha: Float)

fun Modifier.offset(x: androidx.compose.ui.unit.Dp, y: androidx.compose.ui.unit.Dp) = this.then(
    Modifier.graphicsLayer {
        translationX = x.toPx()
        translationY = y.toPx()
    }
)

// End of file
