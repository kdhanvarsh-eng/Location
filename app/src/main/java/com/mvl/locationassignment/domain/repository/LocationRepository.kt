package com.mvl.locationassignment.domain.repository

import com.mvl.locationassignment.data.datasource.LocationAddress
import com.mvl.locationassignment.data.model.LocationInfo


interface LocationRepository {
    suspend fun getLocationFromCoordinates(latitude: Double, longitude: Double): LocationAddress
}
