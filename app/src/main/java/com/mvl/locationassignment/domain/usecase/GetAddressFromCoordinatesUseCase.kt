package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.datasource.LocationAddress
import com.mvl.locationassignment.domain.repository.LocationRepository
import javax.inject.Inject

class GetAddressFromCoordinatesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): LocationAddress {
        return repository.getLocationFromCoordinates(latitude, longitude)
    }
}
