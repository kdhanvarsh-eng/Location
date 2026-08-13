package com.mvl.locationassignment.data.api

import com.mvl.locationassignment.data.model.AirQualityInfo
import com.mvl.locationassignment.data.model.LocationInfo
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {
    @GET("location/address")
    suspend fun getLocationInfo(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): LocationInfo

    @GET("air-quality/info")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): AirQualityInfo
}
