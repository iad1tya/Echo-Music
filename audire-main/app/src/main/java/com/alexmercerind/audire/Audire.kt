package com.alexmercerind.audire

import android.app.Application
import com.alexmercerind.audire.repository.SettingsRepository

class Audire : Application() {
    override fun onCreate() {
        super.onCreate()

        // Apply current settings from SettingsRepository.
        SettingsRepository(this).apply()
    }
}
