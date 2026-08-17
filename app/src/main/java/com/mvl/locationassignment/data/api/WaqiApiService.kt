package com.mvl.locationassignment.data.api

import com.mvl.locationassignment.data.model.AqiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WaqiApiService {
    @GET("feed/{city}/")
    suspend fun getAqiByCity(
        @Path("city") city: String,
        @Query("token") token: String
    ): AqiResponse
}
