package com.mvl.locationassignment.data.model

import com.google.gson.annotations.SerializedName

data class BookingLocationData(
    val latitude: Double,
    val longitude: Double,
    val aqi: Int,
    val name: String
)

data class BookingRequest(
    val a: BookingLocationData,
    val b: BookingLocationData
)

data class BookingResponse(
    val a: BookingLocationData,
    val b: BookingLocationData,
    val price: Int
)
