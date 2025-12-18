package com.alexmercerind.audire.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.audio.AudioIdentifier
import com.alexmercerind.audire.audio.AudioRecorder
import com.alexmercerind.audire.repository.ShazamIdentifyRepository

class IdentifyViewModel : ViewModel() {
    private val audioRecorder = AudioRecorder(viewModelScope)
    private val audioIdentifier = AudioIdentifier(viewModelScope, audioRecorder, ShazamIdentifyRepository())
    val error = audioIdentifier.error
    val music = audioIdentifier.music
    val duration = audioRecorder.duration
    val active = audioRecorder.active

    fun start() = audioRecorder.start()

    fun stop() = audioRecorder.stop()

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}
