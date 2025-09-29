package iad1tya.echo.music.utils

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase

/**
 * Centralized Firebase configuration and initialization
 */
object FirebaseConfig {
    
    private var isInitialized = false
    
    /**
     * Initialize all Firebase services
     */
    fun initialize(context: Context) {
        if (isInitialized) return
        
        try {
            // Initialize Analytics
            initializeAnalytics(context)
            
            // Initialize Crashlytics
            initializeCrashlytics(context)
            
            isInitialized = true
        } catch (e: Exception) {
            // Log error but don't crash the app
            android.util.Log.e("FirebaseConfig", "Firebase initialization failed: ${e.message}")
        }
    }
    
    private fun initializeAnalytics(context: Context) {
        try {
            val analytics = Firebase.analytics
            analytics.setAnalyticsCollectionEnabled(true)
            
            // Set user properties
            analytics.setUserProperty("app_version", getAppVersion(context))
            analytics.setUserProperty("platform", "android")
            
            android.util.Log.d("FirebaseConfig", "Analytics initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseConfig", "Analytics initialization failed: ${e.message}")
        }
    }
    
    private fun initializeCrashlytics(context: Context) {
        try {
            val crashlytics = Firebase.crashlytics
            crashlytics.setCrashlyticsCollectionEnabled(true)
            
            // Set custom keys for better crash reporting
            crashlytics.setCustomKey("app_version", getAppVersion(context))
            crashlytics.setCustomKey("platform", "android")
            
            android.util.Log.d("FirebaseConfig", "Crashlytics initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("FirebaseConfig", "Crashlytics initialization failed: ${e.message}")
        }
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
    
    /**
     * Check if Firebase is properly initialized
     */
    fun isFirebaseInitialized(): Boolean = isInitialized
}
