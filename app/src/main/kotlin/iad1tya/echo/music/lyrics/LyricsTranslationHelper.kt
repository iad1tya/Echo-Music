package iad1tya.echo.music.lyrics

import iad1tya.echo.music.api.OpenRouterService
import iad1tya.echo.music.constants.LanguageCodeToName
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
import java.util.Locale

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
        mode: String,
        scope: CoroutineScope
    ) {
        translationJob?.cancel()
        _status.value = TranslationStatus.Translating
        
        // Clear existing translations to indicate re-translation
        lyrics.forEach { it.translatedTextFlow.value = null }
        
        translationJob = scope.launch(Dispatchers.IO) {
            try {
                // Validate inputs
                if (apiKey.isBlank()) {
                    _status.value = TranslationStatus.Error("API key is required")
                    return@launch
                }
                
                if (lyrics.isEmpty()) {
                    _status.value = TranslationStatus.Error("No lyrics to translate")
                    return@launch
                }
                
                // Create full text while preserving structure (including empty lines)
                val fullText = lyrics.joinToString("\n") { it.text }
                
                if (fullText.isBlank()) {
                    _status.value = TranslationStatus.Error("Lyrics are empty")
                    return@launch
                }

                // Validate language for all modes
                if (targetLanguage.isBlank()) {
                    _status.value = TranslationStatus.Error("Target language is required")
                    return@launch
                }

                // Convert language code to full language name for better AI understanding
                val fullLanguageName = LanguageCodeToName[targetLanguage] 
                    ?: try {
                        Locale(targetLanguage).displayLanguage.takeIf { it.isNotBlank() && it != targetLanguage }
                    } catch (e: Exception) { null }
                    ?: targetLanguage

                val result = OpenRouterService.translate(
                    text = fullText,
                    targetLanguage = fullLanguageName,
                    apiKey = apiKey,
                    baseUrl = baseUrl,
                    model = model,
                    mode = mode
                )
                
                result.onSuccess { translatedLines ->
                    // Robust mapping with validation
                    when {
                        translatedLines.size == lyrics.size -> {
                            // Perfect match - direct mapping
                            lyrics.forEachIndexed { index, entry ->
                                entry.translatedTextFlow.value = translatedLines[index]
                            }
                            _status.value = TranslationStatus.Success
                        }
                        translatedLines.size > lyrics.size -> {
                            // More translations than expected - use first N
                            lyrics.forEachIndexed { index, entry ->
                                entry.translatedTextFlow.value = translatedLines[index]
                            }
                            _status.value = TranslationStatus.Success
                        }
                        translatedLines.size < lyrics.size -> {
                            // Fewer translations - map what we have, leave rest as original
                            translatedLines.forEachIndexed { index, translation ->
                                if (index < lyrics.size) {
                                    lyrics[index].translatedTextFlow.value = translation
                                }
                            }
                            // Fill remaining with original text for romanization, empty for translation
                            for (i in translatedLines.size until lyrics.size) {
                                lyrics[i].translatedTextFlow.value = if (mode == "Romanized") lyrics[i].text else ""
                            }
                            _status.value = TranslationStatus.Success
                        }
                        else -> {
                            _status.value = TranslationStatus.Error("Unexpected translation result")
                        }
                    }
                    
                    // Auto-hide success message after 3 seconds
                    delay(3000)
                    if (_status.value is TranslationStatus.Success) {
                        _status.value = TranslationStatus.Idle
                    }
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Unknown error occurred"
                    
                    // Show error in UI
                    _status.value = TranslationStatus.Error(errorMessage)
                    
                    // Also display error in first line for user visibility
                    if (lyrics.isNotEmpty()) {
                        lyrics[0].translatedTextFlow.value = "⚠️ Error: ${errorMessage.take(50)}"
                    }
                }
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Translation failed"
                _status.value = TranslationStatus.Error(errorMessage)
                
                if (lyrics.isNotEmpty()) {
                    lyrics[0].translatedTextFlow.value = "⚠️ ${errorMessage.take(50)}"
                }
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
