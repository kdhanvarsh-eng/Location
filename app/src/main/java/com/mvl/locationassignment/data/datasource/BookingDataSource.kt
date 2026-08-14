package com.mvl.locationassignment.data.datasource

import android.util.Log
import com.mvl.locationassignment.data.api.BookingApiService
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "BookingDataSource"

interface BookingDataSource {
    suspend fun bookTrip(request: BookingRequest): BookingResponse
}

class BookingDataSourceImpl @Inject constructor(
    private val bookingApiService: BookingApiService
) : BookingDataSource {

    override suspend fun bookTrip(request: BookingRequest): BookingResponse {
        try {
            Log.d(TAG,  "Initiating booking request...")

            return withContext(Dispatchers.IO) {
                Log.d(TAG, "Calling BookingApiService.bookTrip()")
                bookingApiService.bookTrip(request)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Booking failed: ${e.message}", e)
            throw e
        }
    }
}
