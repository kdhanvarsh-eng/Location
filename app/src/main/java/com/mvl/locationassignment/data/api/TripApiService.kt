package com.mvl.locationassignment.data.api

import com.mvl.locationassignment.data.model.TripList
import retrofit2.http.GET
import retrofit2.http.Query

interface TripApiService {
    @GET("/books")
    suspend fun getTrips(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): TripList
}
