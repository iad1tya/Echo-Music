package iad1tya.echo.music.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import iad1tya.echo.music.LocalPlayerAwareWindowInsets
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.AutoTranslateLyricsKey
import iad1tya.echo.music.constants.LanguageCodeToName
import iad1tya.echo.music.constants.OpenRouterApiKey
import iad1tya.echo.music.constants.OpenRouterModelKey
import iad1tya.echo.music.constants.TranslateLanguageKey
import iad1tya.echo.music.ui.component.EditTextPreference
import iad1tya.echo.music.ui.component.ListPreference
import iad1tya.echo.music.ui.component.SwitchPreference
import iad1tya.echo.music.ui.component.InfoLabel
import iad1tya.echo.music.utils.rememberPreference
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var openRouterApiKey by rememberPreference(OpenRouterApiKey, "")
    var openRouterModel by rememberPreference(OpenRouterModelKey, "mistralai/mistral-small-3.1-24b-instruct:free")
    var autoTranslateLyrics by rememberPreference(AutoTranslateLyricsKey, false)
    var translateLanguage by rememberPreference(TranslateLanguageKey, "en")

    val models = listOf(
        "google/gemini-flash-1.5",
        "openai/gpt-3.5-turbo",
        "anthropic/claude-3-haiku",
        "meta-llama/llama-3-8b-instruct"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Settings") },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = { navController.navigateUp() }) {
                        androidx.compose.material3.Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Bottom))
        ) {
            EditTextPreference(
                title = { Text("OpenRouter API Key") },
                value = openRouterApiKey,
                onValueChange = { openRouterApiKey = it },
                icon = { androidx.compose.material3.Icon(painterResource(R.drawable.key), null) }
            )

            EditTextPreference(
                title = { Text("OpenRouter Model") },
                value = openRouterModel,
                onValueChange = { openRouterModel = it },
                icon = { androidx.compose.material3.Icon(painterResource(R.drawable.discover_tune), null) }
            )

            SwitchPreference(
                title = { Text("Auto translate all songs") },
                checked = autoTranslateLyrics,
                onCheckedChange = { autoTranslateLyrics = it },
                icon = { androidx.compose.material3.Icon(painterResource(R.drawable.language), null) }
            )

            ListPreference(
                title = { Text("Target Language") },
                selectedValue = translateLanguage,
                values = LanguageCodeToName.keys.sortedBy { LanguageCodeToName[it] },
                valueText = { LanguageCodeToName[it] ?: it },
                onValueSelected = { translateLanguage = it },
                icon = { androidx.compose.material3.Icon(painterResource(R.drawable.language), null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.Card(
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Setup Guide", 
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val annotatedString = androidx.compose.ui.text.buildAnnotatedString {
                        append("To use this feature, you need an API key from OpenRouter.ai.\n\n")
                        
                        append("1. Go to ")
                        pushStringAnnotation(tag = "URL", annotation = "https://openrouter.ai")
                        withStyle(style = androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                            append("OpenRouter.ai")
                        }
                        pop()
                        append(" and sign up.\n")
                        
                        append("2. Generate a new API Key and paste it above.\n")
                        
                        append("3. OpenRouter offers free models (like 'mistralai/mistral-small-3.1-24b-instruct:free') which have generous rate limits for personal use.\n\n")
                        
                        append("Check their ")
                        pushStringAnnotation(tag = "URL", annotation = "https://openrouter.ai/docs#pricing")
                        withStyle(style = androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)) {
                            append("pricing and limits")
                        }
                        pop()
                        append(" for more details.")
                    }
                    
                    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                    
                    androidx.compose.foundation.text.ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        onClick = { offset ->
                            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    uriHandler.openUri(annotation.item)
                                }
                        }
                    )
                }
            }
        }
    }
}
