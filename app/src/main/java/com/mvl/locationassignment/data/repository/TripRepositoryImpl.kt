package com.mvl.locationassignment.data.repository

import com.mvl.locationassignment.data.datasource.TripDataSource
import com.mvl.locationassignment.data.model.TripList
import javax.inject.Inject

interface TripRepository {
    suspend fun getTrips(year: Int, month: Int): TripList
}

class TripRepositoryImpl @Inject constructor(
    private val tripDataSource: TripDataSource
) : TripRepository {
    override suspend fun getTrips(year: Int, month: Int): TripList {
        return tripDataSource.getTrips(year, month)
    }
}
