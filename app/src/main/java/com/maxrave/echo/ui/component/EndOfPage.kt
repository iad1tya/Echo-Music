package iad1tya.echo.music.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EndOfPage(withoutCredit: Boolean = false) {
    // Removed credit text and extra spacing as requested
    // This component now serves as a minimal end marker
}

@Composable
fun EndOfPageWithMiniPlayerSpacing() {
    // Add spacing for Mood & Genre screens to position content above mini player
    // Mini player height: 72dp + bottom nav height: ~56dp + extra spacing: 24dp = 152dp total
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .height(152.dp)
    )
}

@Composable
fun EndOfPageWithSettingsSpacing() {
    // Minimal spacing for Settings screen - bring content closer to mini player
    // Just enough space to prevent overlap: 16dp total
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .height(16.dp)
    )
}