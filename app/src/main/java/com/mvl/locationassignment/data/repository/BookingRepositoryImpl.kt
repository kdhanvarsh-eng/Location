package com.mvl.locationassignment.data.repository

import com.mvl.locationassignment.data.datasource.BookingDataSource
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val bookingDataSource: BookingDataSource
) : BookingRepository {
    override suspend fun bookTrip(request: BookingRequest): BookingResponse {
        return bookingDataSource.bookTrip(request)
    }
}
