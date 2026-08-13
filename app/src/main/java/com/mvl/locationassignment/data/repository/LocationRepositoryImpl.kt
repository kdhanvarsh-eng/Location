package com.mvl.locationassignment.data.repository

import com.mvl.locationassignment.data.datasource.LocationAddress
import com.mvl.locationassignment.data.datasource.LocationDataSource
import com.mvl.locationassignment.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationDataSource: LocationDataSource) : LocationRepository {

    override suspend fun getLocationFromCoordinates(latitude: Double, longitude: Double): LocationAddress {
        return locationDataSource.getLocationFromCoordinates(latitude, longitude)
    }
}