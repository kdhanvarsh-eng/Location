package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.model.TripList
import com.mvl.locationassignment.data.repository.TripRepository

class GetTripsUseCase(
    private val tripRepository: TripRepository
) {
    suspend operator fun invoke(year: Int, month: Int): TripList {
        return tripRepository.getTrips(year, month)
    }
}
