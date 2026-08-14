package com.mvl.locationassignment.data.datasource

import android.util.Log
import com.mvl.locationassignment.data.api.BookingApiService
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import javax.inject.Inject

private const val TAG = "BookingDataSource"

interface BookingDataSource {
    suspend fun bookTrip(request: BookingRequest): BookingResponse
}

class BookingDataSourceImpl @Inject constructor(
    private val bookingApiService: BookingApiService
) : BookingDataSource {
    
    override suspend fun bookTrip(request: BookingRequest): BookingResponse {
        return try {
            Log.d(TAG, "🚀 Initiating booking request...")
            Log.d(TAG, "Location A: ${request.a.name}, Lat: ${request.a.latitude}, Long: ${request.a.longitude}, AQI: ${request.a.aqi}")
            Log.d(TAG, "Location B: ${request.b.name}, Lat: ${request.b.latitude}, Long: ${request.b.longitude}, AQI: ${request.b.aqi}")
            
            // Make actual HTTP POST request through Retrofit
            // The MockBookingInterceptor will intercept and handle it
            Log.d(TAG, "📡 Making HTTP POST request to bookingApiService...")
            val response = bookingApiService.bookTrip(request)
            Log.d(TAG, "✅ Booking successful!")
            response
        } catch (e: Exception) {
            Log.e(TAG, "❌ Booking failed: ${e.message}", e)
            throw e
        }
    }
}
