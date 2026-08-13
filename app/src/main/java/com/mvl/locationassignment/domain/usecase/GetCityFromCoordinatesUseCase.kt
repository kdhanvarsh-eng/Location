package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.datasource.LocationAddress
import com.mvl.locationassignment.domain.repository.LocationRepository
import javax.inject.Inject

class GetCityFromCoordinatesUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): LocationAddress {
        return locationRepository.getLocationFromCoordinates(latitude, longitude)
    }
}
