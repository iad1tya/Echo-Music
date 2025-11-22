package iad1tya.echo.music.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider
import com.google.android.gms.cast.framework.media.CastMediaOptions
import com.google.android.gms.cast.framework.media.NotificationOptions
import timber.log.Timber

class CastOptionsProvider : OptionsProvider {
    override fun getCastOptions(context: Context): CastOptions {
        return try {
            val notificationOptions = NotificationOptions.Builder()
                .setTargetActivityClassName(iad1tya.echo.music.MainActivity::class.java.name)
                .build()
            
            val mediaOptions = CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(iad1tya.echo.music.MainActivity::class.java.name)
                .build()
            
            CastOptions.Builder()
                .setReceiverApplicationId(com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
                .setCastMediaOptions(mediaOptions)
                .build()
        } catch (e: Exception) {
            Timber.w(e, "Failed to initialize Cast options - creating minimal configuration")
            // Return minimal CastOptions to prevent crash
            CastOptions.Builder()
                .setReceiverApplicationId(com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
                .build()
        }
    }

    override fun getAdditionalSessionProviders(context: Context): List<SessionProvider>? {
        return null
    }
}
