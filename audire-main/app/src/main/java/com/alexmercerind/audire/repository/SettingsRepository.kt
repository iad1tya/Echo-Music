package com.alexmercerind.audire.repository

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.alexmercerind.audire.R
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class SettingsRepository(private val application: Application) {
    val autoStart: StateFlow<Boolean>
        get() = _autoStart
    private val _autoStart = MutableStateFlow(false)

    val theme: StateFlow<String?>
        get() = _theme
    private val _theme = MutableStateFlow<String?>(null)

    val systemColorScheme: StateFlow<Boolean?>
        get() = _systemColorScheme
    private val _systemColorScheme = MutableStateFlow<Boolean?>(null)

    private val sharedPreferences =
        application.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val autoStartSharedValue
        get() = sharedPreferences.getBoolean(
            SHARED_PREFERENCES_KEY_BEHAVIOR_AUTO_START,
            false
        )
    private val themeSharedValue
        get() = sharedPreferences.getString(
            SHARED_PREFERENCES_KEY_APPEARANCE_THEME,
            application.getString(R.string.settings_appearance_theme_system)
        ) ?: ""
    private val systemColorSchemeValue
        get() = sharedPreferences.getBoolean(
            SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        )

    init {
        _autoStart.value = autoStartSharedValue
        _theme.value = themeSharedValue
        _systemColorScheme.value = systemColorSchemeValue
    }

    suspend fun setAutoStart(value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(SHARED_PREFERENCES_KEY_BEHAVIOR_AUTO_START, value)
            apply()
        }
        _autoStart.emit(value)
    }

    suspend fun setTheme(value: String) {
        sharedPreferences.edit().apply {
            putString(SHARED_PREFERENCES_KEY_APPEARANCE_THEME, value)
            apply()
        }
        _theme.emit(value)
        withContext(Dispatchers.Main) { applyTheme(value) }
    }

    suspend fun setSystemColorScheme(value: Boolean) {
        sharedPreferences.edit().apply {
            putBoolean(SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME, value)
            apply()
        }
        _systemColorScheme.emit(value)
        withContext(Dispatchers.Main) { applySystemColorScheme(value) }
    }

    fun apply() {
        runCatching { applyTheme(themeSharedValue) }
        runCatching { applySystemColorScheme(systemColorSchemeValue) }
    }

    private fun applyTheme(value: String) {
        val mode = when (value) {
            application.getString(R.string.settings_appearance_theme_light) -> AppCompatDelegate.MODE_NIGHT_NO
            application.getString(R.string.settings_appearance_theme_dark) -> AppCompatDelegate.MODE_NIGHT_YES
            application.getString(R.string.settings_appearance_theme_system) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> throw IllegalStateException()
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun applySystemColorScheme(value: Boolean) {
        if (value) {
            DynamicColors.applyToActivitiesIfAvailable(application)
        } else {
            application.setTheme(R.style.Theme_Audire)
        }
    }


    companion object {
        private const val SHARED_PREFERENCES_NAME = "SETTINGS"
        private const val SHARED_PREFERENCES_KEY_BEHAVIOR_AUTO_START = "AUTO_START"
        private const val SHARED_PREFERENCES_KEY_APPEARANCE_THEME = "THEME"
        private const val SHARED_PREFERENCES_KEY_APPEARANCE_SYSTEM_COLOR_SCHEME = "SYSTEM_COLOR_SCHEME"

        @Volatile
        private var instance: SettingsRepository? = null
        private val lock = Any()

        operator fun invoke(application: Application) = instance ?: synchronized(lock) {
            instance ?: SettingsRepository(application).also { instance = it }
        }
    }
}
