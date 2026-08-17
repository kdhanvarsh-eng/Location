package com.mvl.locationassignment.data.datasource

import com.mvl.locationassignment.data.model.TripList
import javax.inject.Inject

interface TripDataSource {
    suspend fun getTrips(year: Int, month: Int): TripList
}

class TripDataSourceImpl @Inject constructor(
    private val tripApiService: com.mvl.locationassignment.data.api.TripApiService
) : TripDataSource {
    override suspend fun getTrips(year: Int, month: Int): TripList {
        return tripApiService.getTrips(year, month)
    }
}
