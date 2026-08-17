package com.mvl.locationassignment.data.model

object MockTripData {
    fun getDefaultTrips(): List<Trip> = listOf(
        Trip(
            a = BookingLocationData(
                latitude = 36.564,
                longitude = 127.001,
                aqi = 30,
                name = "Seoul A Location"
            ),
            b = BookingLocationData(
                latitude = 36.567,
                longitude = 127.0,
                aqi = 40,
                name = "Seoul B Location"
            ),
            price = 10000
        ),
        Trip(
            a = BookingLocationData(
                latitude = 36.577,
                longitude = 127.033,
                aqi = 50,
                name = "Seoul C Location"
            ),
            b = BookingLocationData(
                latitude = 36.567,
                longitude = 127.0,
                aqi = 60,
                name = "Seoul D Location"
            ),
            price = 20000
        )
    )
}
