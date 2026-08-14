package com.mvl.locationassignment.data.model

import com.mvl.locationassignment.data.model.LocationInfo

data class BookingRequestBuilder(
    val locationA: LocationInfo,
    val locationB: LocationInfo,
    val aqiA: Int = 0,
    val aqiB: Int = 0
) {
    fun build(): BookingRequest {
        return BookingRequest(
            a = BookingLocationData(
                latitude = locationA.latitude,
                longitude = locationA.longitude,
                aqi = aqiA,
                name = locationA.address
            ),
            b = BookingLocationData(
                latitude = locationB.latitude,
                longitude = locationB.longitude,
                aqi = aqiB,
                name = locationB.address
            )
        )
    }
}
