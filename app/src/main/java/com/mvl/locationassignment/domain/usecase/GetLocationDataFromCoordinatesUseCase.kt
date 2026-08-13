package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.domain.model.LocationData
import com.mvl.locationassignment.domain.repository.LocationRepository
import javax.inject.Inject

class GetLocationDataFromCoordinatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): LocationData {
        val fullAddress = repository.getAddressFromCoordinates(latitude, longitude)
        val city = repository.getCityFromCoordinates(latitude, longitude)
        return LocationData(fullAddress, city)
    }
}
