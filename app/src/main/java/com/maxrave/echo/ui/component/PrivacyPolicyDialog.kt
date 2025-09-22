package iad1tya.echo.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.theme.typo

@Composable
fun PrivacyPolicyDialog(
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.privacy_policy),
                style = typo.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Echo Music is committed to protecting your privacy. This policy explains how we handle your information.",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Data We Collect",
                    style = typo.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Account info when you log in to Spotify/YouTube\n• Usage data (songs played, playlists, searches)\n• Device info for app improvement\n• Your music preferences and settings",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "How We Use It",
                    style = typo.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Provide music streaming and playback\n• Improve app performance and features\n• Personalize your experience\n• Fix bugs and crashes",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Data Sharing",
                    style = typo.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• YouTube Music & Spotify: For streaming\n• Google Analytics: Anonymous usage data\n• We DO NOT sell your personal data",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Your Control",
                    style = typo.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• View/export your data in settings\n• Clear app data anytime\n• Disable analytics if desired\n• Control which services you connect to",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Contact",
                    style = typo.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Questions? Visit: github.com/iad1tya/Echo-Music",
                    style = typo.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "By using Echo Music, you agree to this privacy policy.",
                    style = typo.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "I Understand",
                    color = androidx.compose.ui.graphics.Color.Black
                )
            }
        }
    )
}
