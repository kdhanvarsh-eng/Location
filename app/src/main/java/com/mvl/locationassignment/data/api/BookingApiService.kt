package com.mvl.locationassignment.data.api

import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BookingApiService {
    @POST("books")
    suspend fun bookTrip(@Body request: BookingRequest): BookingResponse
}
