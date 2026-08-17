package com.mvl.locationassignment

import android.app.Application
import android.content.ComponentCallbacks2
import android.util.Log
import com.mvl.locationassignment.network.MockWebServerManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

private const val TAG = "LocationAssignmentApp"

@HiltAndroidApp
class LocationAssignmentApp : Application(), ComponentCallbacks2 {

    @Inject
    lateinit var mockWebServerManager: MockWebServerManager

    override fun onCreate() {
        super.onCreate()
        try {
            // Start MockWebServer for the app lifecycle
            mockWebServerManager.start()
            Log.d(TAG, "MockWebServer requested start from Application.onCreate")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start MockWebServer in Application.onCreate: ${e.message}", e)
        }
    }

    // this is optional since we are using MockWebServer for testing purposes
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // Stop the MockWebServer when the app is being trimmed aggressively

        if (level >= TRIM_MEMORY_RUNNING_CRITICAL) {
            try {
                mockWebServerManager.stop()
                Log.d(TAG, "MockWebServer stop requested from onTrimMemory(level=$level)")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop MockWebServer in onTrimMemory: ${e.message}", e)
            }
        }
    }
    // this is optional since we are using MockWebServer for testing purposes
    override fun onLowMemory() {
        super.onLowMemory()
        try {

            mockWebServerManager.stop()
            Log.d(TAG, "MockWebServer stop requested from onLowMemory")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop MockWebServer in onLowMemory: ${e.message}", e)
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
