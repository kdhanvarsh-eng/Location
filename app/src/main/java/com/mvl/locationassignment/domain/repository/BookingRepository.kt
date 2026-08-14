package com.mvl.locationassignment.domain.repository

import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse

interface BookingRepository {
    suspend fun bookTrip(request: BookingRequest): BookingResponse
}
