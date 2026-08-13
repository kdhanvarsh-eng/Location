package com.mvl.locationassignment.domain.repository

import com.mvl.locationassignment.data.model.LocationInfo

interface LocationRepository {
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String
    suspend fun getCityFromCoordinates(latitude: Double, longitude: Double): String
}
