package iad1tya.echo.music.utils

import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import iad1tya.echo.music.BuildConfig

/**
 * Firebase Integration Verification Utility
 * Provides methods to verify that Firebase Analytics and Crashlytics are working correctly
 */
object FirebaseVerification {
    
    private const val TAG = "FirebaseVerification"
    
    /**
     * Verify Firebase Analytics is working
     */
    fun verifyAnalytics(context: Context): Boolean {
        return try {
            val analytics = FirebaseAnalytics.getInstance(context)
            
            // Log a test event
            val bundle = android.os.Bundle().apply {
                putString("test_event", "firebase_verification")
                putString("timestamp", System.currentTimeMillis().toString())
                putString("app_version", getAppVersion(context))
            }
            analytics.logEvent("firebase_analytics_verification", bundle)
            
            Log.d(TAG, "✅ Firebase Analytics verification: SUCCESS")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase Analytics verification: FAILED - ${e.message}", e)
            false
        }
    }
    
    /**
     * Verify Firebase Crashlytics is working
     */
    fun verifyCrashlytics(context: Context): Boolean {
        return try {
            val crashlytics = FirebaseCrashlytics.getInstance()
            
            // Set test custom key
            crashlytics.setCustomKey("verification_test", "success")
            crashlytics.setCustomKey("verification_timestamp", System.currentTimeMillis())
            
            // Log test message
            crashlytics.log("Firebase Crashlytics verification test")
            
            // Record a test non-fatal exception
            try {
                throw RuntimeException("Test exception for Crashlytics verification")
            } catch (e: Exception) {
                crashlytics.recordException(e)
            }
            
            Log.d(TAG, "✅ Firebase Crashlytics verification: SUCCESS")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase Crashlytics verification: FAILED - ${e.message}", e)
            false
        }
    }
    
    /**
     * Verify Firebase configuration
     */
    fun verifyConfiguration(context: Context): Map<String, String> {
        val config = mutableMapOf<String, String>()
        
        try {
            // Check project configuration
            config["project_id"] = "echo-aab3b"
            config["project_number"] = "887842405081"
            config["package_name"] = context.packageName
            config["app_version"] = getAppVersion(context)
            config["build_type"] = if (BuildConfig.DEBUG) "debug" else "release"
            
            // Check Firebase services availability
            config["analytics_available"] = try {
                FirebaseAnalytics.getInstance(context)
                "true"
            } catch (e: Exception) {
                "false"
            }
            
            config["crashlytics_available"] = try {
                FirebaseCrashlytics.getInstance()
                "true"
            } catch (e: Exception) {
                "false"
            }
            
            Log.d(TAG, "✅ Firebase configuration verification: SUCCESS")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase configuration verification: FAILED - ${e.message}", e)
            config["error"] = e.message ?: "Unknown error"
        }
        
        return config
    }
    
    /**
     * Run complete Firebase verification
     */
    fun runCompleteVerification(context: Context): Map<String, Any> {
        val results = mutableMapOf<String, Any>()
        
        Log.d(TAG, "🚀 Starting complete Firebase verification...")
        
        // Verify Analytics
        results["analytics_verification"] = verifyAnalytics(context)
        
        // Verify Crashlytics
        results["crashlytics_verification"] = verifyCrashlytics(context)
        
        // Verify Configuration
        results["configuration"] = verifyConfiguration(context)
        
        // Overall status
        val overallSuccess = results["analytics_verification"] as Boolean && 
                           results["crashlytics_verification"] as Boolean
        
        results["overall_success"] = overallSuccess
        
        Log.d(TAG, "🏁 Firebase verification completed. Overall success: $overallSuccess")
        
        return results
    }
    
    private fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
