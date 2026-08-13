package com.mvl.locationassignment.data.repository

import com.mvl.locationassignment.data.api.LocationApiService
import com.mvl.locationassignment.data.model.AirQualityInfo
import com.mvl.locationassignment.data.model.LocationInfo
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val apiService: LocationApiService
) {
    suspend fun getLocationInfo(latitude: Double, longitude: Double): LocationInfo {
        return apiService.getLocationInfo(latitude, longitude)
    }

    suspend fun getAirQuality(latitude: Double, longitude: Double): AirQualityInfo {
        return apiService.getAirQuality(latitude, longitude)
    }

    suspend fun getLocationDataForCoordinates(
        latitude: Double,
        longitude: Double
    ): Pair<LocationInfo, AirQualityInfo> {
        val locationInfo = getLocationInfo(latitude, longitude)
        val airQuality = getAirQuality(latitude, longitude)
        return locationInfo to airQuality
    }
}
