package com.mvl.locationassignment.network

import android.util.Log
import com.google.gson.Gson
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.data.model.MockTripData
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "MockWebServerManager"

@Singleton
class MockWebServerManager @Inject constructor(
    private val gson: Gson
) {
    private val mockWebServer = MockWebServer()
    private var isStarted = false

    fun start() {
        if (isStarted) return

        try {
            // Start MockWebServer asynchronously to avoid NetworkOnMainThreadException
            val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
            executor.submit {
                try {
                    mockWebServer.start()
                    setupBookingEndpoint()
                    isStarted = true
                    Log.d(TAG, "MockWebServer started at: ${mockWebServer.url("")}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed while starting MockWebServer on background thread: ${e.message}", e)
                }
            }
            executor.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to queue MockWebServer start: ${e.message}", e)
        }
    }

    fun stop() {
        if (!isStarted) return
        try {
            // Shutdown on background thread
            val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
            val future = executor.submit {
                mockWebServer.shutdown()
            }
            future.get()
            executor.shutdown()

            isStarted = false
            Log.d(TAG, "MockWebServer stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop MockWebServer: ${e.message}", e)
        }
    }

    private fun setupBookingEndpoint() {
        // Set up queue dispatcher to handle booking and trip requests
        mockWebServer.dispatcher = MockTripApi(gson)
    }

    fun getBaseUrl(): String {
        return mockWebServer.url("").toString()
    }

}

class MockTripApi(private val gson: Gson) : okhttp3.mockwebserver.Dispatcher() {
    
    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return MockResponse().setResponseCode(404)
        
        Log.d(TAG, "MockWebServer Request:")
        Log.d(TAG, "Method: ${request.method}")
        Log.d(TAG, "Path: $path")
        
        return when {
            request.method == "POST" && path == "/books" -> {
                handleBookingRequest(request)
            }
            request.method == "GET" && path.startsWith("/books") -> {
                handleGetTripsRequest(request)
            }
            else -> {
                Log.d(TAG, " No handler for: ${request.method} $path")
                MockResponse().setResponseCode(404).setBody("Not Found")
            }
        }
    }

    private fun handleBookingRequest(request: RecordedRequest): MockResponse {
        return try {
            val requestBody = request.body.readUtf8()
            Log.d(TAG, "REQUEST BODY:")
            Log.d(TAG, requestBody)

            val bookingRequest = gson.fromJson(requestBody, BookingRequest::class.java)
            Log.d(TAG, "   Booking Request: ${bookingRequest}")

            Log.d(TAG, "Processing for 1 second...")
            Thread.sleep(1000)
            Log.d(TAG, "Processing complete")
            
            // Create mock response
            val mockResponse = BookingResponse(
                a = bookingRequest.a.copy(),
                b = bookingRequest.b.copy(),
                price = 10000
            )
            
            val responseBody = gson.toJson(mockResponse)
            Log.d(TAG, responseBody)
            
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(responseBody)
        } catch (e: Exception) {
            Log.e(TAG, " Error handling booking request: ${e.message}", e)
            MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"${e.message}"}""")
        }
    }

    private fun handleGetTripsRequest(request: RecordedRequest): MockResponse {
        return try {
            val year = request.requestUrl?.queryParameter("year") ?: "2020"
            val month = request.requestUrl?.queryParameter("month") ?: "11"
            
            Log.d(TAG, "📥 GET /books?year=$year&month=$month")
            
            // Simulate processing delay
            Log.d(TAG, "⏳ Processing for 1 second...")
            Thread.sleep(1000)
            Log.d(TAG, "✅ Processing complete")
            
            // Mock trip data from MockTripData
            val trips = MockTripData.getDefaultTrips()
            
            val responseBody = gson.toJson(trips)
            Log.d(TAG, "📤 RESPONSE BODY:")
            Log.d(TAG, responseBody)
            
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(responseBody)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error handling GET trips request: ${e.message}", e)
            MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"${e.message}"}""")
        }
    }
}
