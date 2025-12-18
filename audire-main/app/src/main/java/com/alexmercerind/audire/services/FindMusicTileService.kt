package com.alexmercerind.audire.services

import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import androidx.core.service.quicksettings.PendingIntentActivityWrapper
import androidx.core.service.quicksettings.TileServiceCompat
import com.alexmercerind.audire.ui.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class FindMusicTileService : TileService() {
    companion object {
        var handled = true
    }

    override fun onClick() {
        super.onClick()
        handled = false

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
        }
        val wrapper = PendingIntentActivityWrapper(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT,
            false
        )
        TileServiceCompat.startActivityAndCollapse(this, wrapper)
    }
}
