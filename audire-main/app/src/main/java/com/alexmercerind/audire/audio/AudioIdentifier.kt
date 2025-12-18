package com.alexmercerind.audire.audio

import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.repository.IdentifyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.sample

@OptIn(FlowPreview::class)
class AudioIdentifier(
    private val scope: CoroutineScope,
    private val audioRecorder: AudioRecorder,
    private val identifyRepository: IdentifyRepository
) {
    private val _error = MutableSharedFlow<Unit>()
    private val _music = MutableSharedFlow<Music>()
    val error get() = _error.asSharedFlow()
    val music get() = _music.asSharedFlow()

    init {
        combine(audioRecorder.duration, audioRecorder.buffer) { duration, buffer -> duration to buffer }
            .sample(2000L)
            .onEach(::process)
            .launchIn(scope)
    }

    private suspend fun process(data: Pair<Int, ByteArray>) {
        val (duration, buffer) = data
        runCatching {
            if (buffer.isEmpty()) return
            if (duration < MIN_DURATION) return
            if (duration > MAX_DURATION) {
                _error.emit(Unit)
                audioRecorder.stop()
                return
            }
            identifyRepository.identify(duration, buffer)?.let {
                // HACK: Prevent obscure music from being displayed if duration lesser than MAX_DURATION.
                if (it.album.isNullOrEmpty() && duration < MAX_DURATION) return
                _music.emit(it)
                audioRecorder.stop()
            }
        }
    }

    companion object {
        const val MIN_DURATION = 2
        const val MAX_DURATION = 12
    }
}
