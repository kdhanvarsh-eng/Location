package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.domain.repository.BookingRepository
import javax.inject.Inject

class BookTripUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    suspend operator fun invoke(request: BookingRequest): BookingResponse {
        return bookingRepository.bookTrip(request)
    }
}
