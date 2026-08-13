package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.domain.model.LocationData
import com.mvl.locationassignment.domain.repository.LocationRepository
import javax.inject.Inject

/**
 * Combined use case that fetches both full address and city from coordinates.
 * Eliminates the need for two separate use cases and calls.
 * 
 * Usage:
 * val locationData = GetLocationDataFromCoordinatesUseCase(12.99, 77.69)
 * val address = locationData.fullAddress  // "1st Main Road, Bengaluru, Karnataka, India"
 * val city = locationData.city             // "Bengaluru"
 */
class GetLocationDataFromCoordinatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): LocationData {
        val locationAddress = repository.getLocationFromCoordinates(latitude, longitude)
        val city = locationAddress.city

        return LocationData(
            fullAddress = locationAddress.fullAddress,
            city = city
        )
    }
}
