package com.mvl.locationassignment.data.model

data class Trip(
    val a: BookingLocationData,
    val b: BookingLocationData,
    val price: Int
)

typealias TripList = List<Trip>
