package iad1tya.echo.music.lyrics

import iad1tya.echo.music.api.OpenRouterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object LyricsTranslationHelper {
    private var translationJob: Job? = null

    fun translateLyrics(
        lyrics: List<LyricsEntry>,
        targetLanguage: String,
        apiKey: String,
        model: String,
        scope: CoroutineScope
    ) {
        translationJob?.cancel()
        translationJob = scope.launch(Dispatchers.IO) {
            // Group lyrics into chunks to reduce API calls if needed, 
            // but for now let's just translate the whole block or line by line.
            // Since we want to display it line by line, passing the whole text and asking 
            // the LLM to preserve structure is risky but cheaper.
            // Let's try to translate manageable chunks or the entire text if it's not too long.

            val fullText = lyrics.joinToString("\n") { it.text }
            if (fullText.isBlank()) return@launch

            val result = OpenRouterService.translate(
                text = fullText,
                targetLanguage = targetLanguage,
                apiKey = apiKey,
                model = model
            )
            
            result.onSuccess { translatedFullText ->
                val translatedLines = translatedFullText.lines()
                
                if (translatedLines.size == lyrics.size) {
                     lyrics.forEachIndexed { index, entry ->
                         entry.translatedTextFlow.value = translatedLines[index]
                     }
                } else {
                    val minSize = minOf(translatedLines.size, lyrics.size)
                    for (i in 0 until minSize) {
                        lyrics[i].translatedTextFlow.value = translatedLines[i]
                    }
                }
            }.onFailure { error ->
                if (lyrics.isNotEmpty()) {
                    lyrics[0].translatedTextFlow.value = "Error: ${error.message}"
                }
            }
        }
    }

}
