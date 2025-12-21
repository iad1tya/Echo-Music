package iad1tya.echo.music.lyrics

import iad1tya.echo.music.api.OpenRouterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object LyricsTranslationHelper {
    private val _status = MutableStateFlow<TranslationStatus>(TranslationStatus.Idle)
    val status: StateFlow<TranslationStatus> = _status.asStateFlow()

    private val _manualTrigger = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val manualTrigger: SharedFlow<Unit> = _manualTrigger.asSharedFlow()
    
    private var translationJob: Job? = null

    fun triggerManualTranslation() {
        _manualTrigger.tryEmit(Unit)
    }
    
    fun resetStatus() {
        _status.value = TranslationStatus.Idle
    }

    fun translateLyrics(
        lyrics: List<LyricsEntry>,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        scope: CoroutineScope
    ) {
        translationJob?.cancel()
        _status.value = TranslationStatus.Translating
        
        // Clear existing translations to indicate re-translation
        lyrics.forEach { it.translatedTextFlow.value = null }
        
        translationJob = scope.launch(Dispatchers.IO) {
            val fullText = lyrics.joinToString("\n") { it.text }
            if (fullText.isBlank()) {
                _status.value = TranslationStatus.Idle
                return@launch
            }

            val result = OpenRouterService.translate(
                text = fullText,
                targetLanguage = targetLanguage,
                apiKey = apiKey,
                baseUrl = baseUrl,
                model = model
            )
            
            result.onSuccess { translatedLines ->
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
                _status.value = TranslationStatus.Success
                delay(3000) // Show success for 3 seconds
                _status.value = TranslationStatus.Idle
            }.onFailure { error ->
                if (lyrics.isNotEmpty()) {
                    lyrics[0].translatedTextFlow.value = "Error: ${error.message}"
                }
                _status.value = TranslationStatus.Error(error.message ?: "Unknown error")
            }
        }
    }

    sealed class TranslationStatus {
        data object Idle : TranslationStatus()
        data object Translating : TranslationStatus()
        data object Success : TranslationStatus()
        data class Error(val message: String) : TranslationStatus()
    }
}
