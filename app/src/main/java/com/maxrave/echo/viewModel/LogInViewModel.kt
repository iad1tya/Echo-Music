package iad1tya.echo.music.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import iad1tya.echo.music.viewModel.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@UnstableApi
class LogInViewModel(
    private val application: Application,
) : BaseViewModel(application) {
    private val _spotifyStatus: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val spotifyStatus: StateFlow<Boolean> get() = _spotifyStatus

    private val _fullSpotifyCookies: MutableStateFlow<List<Pair<String, String?>>> = MutableStateFlow(emptyList())
    val fullSpotifyCookies: StateFlow<List<Pair<String, String?>>> get() = _fullSpotifyCookies.asStateFlow()

    private val _fullYouTubeCookies: MutableStateFlow<List<Pair<String, String?>>> = MutableStateFlow(emptyList())
    val fullYouTubeCookies: StateFlow<List<Pair<String, String?>>> get() = _fullYouTubeCookies.asStateFlow()

    fun saveSpotifySpdc(cookie: String) {
        viewModelScope.launch {
            try {
                val cookieMap = cookie
                    .split("; ")
                    .filter { it.isNotEmpty() }
                    .associate { cookiePair ->
                        val parts = cookiePair.split("=", limit = 2)
                        if (parts.size == 2) {
                            parts[0] to parts[1]
                        } else {
                            parts[0] to ""
                        }
                    }
                
                val spdc = cookieMap["sp_dc"] ?: ""
                if (spdc.isNotEmpty()) {
                    // Save the cookie first
                    dataStoreManager.setSpdc(spdc)
                    // Give a small delay to ensure DataStore is updated
                    delay(50)
                    // Then set the status
                    _spotifyStatus.value = true
                } else {
                    _spotifyStatus.value = false
                }
            } catch (e: Exception) {
                // Handle parsing errors
                _spotifyStatus.value = false
            }
        }
    }

    fun setVisitorData(visitorData: String) {
        viewModelScope.launch {
            dataStoreManager.setVisitorData(visitorData)
        }
    }

    fun setDataSyncId(dataSyncId: String) {
        viewModelScope.launch {
            dataStoreManager.setDataSyncId(dataSyncId)
        }
    }

    fun setFullSpotifyCookies(cookies: List<Pair<String, String?>>) {
        viewModelScope.launch {
            _fullSpotifyCookies.value = cookies
        }
    }

    fun setFullYouTubeCookies(cookies: List<Pair<String, String?>>) {
        viewModelScope.launch {
            _fullYouTubeCookies.value = cookies
        }
    }
}