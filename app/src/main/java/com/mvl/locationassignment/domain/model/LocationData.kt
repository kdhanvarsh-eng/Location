package com.mvl.locationassignment.domain.model

/**
 * Combined location data containing both full address and city name.
 * Used by GetLocationDataFromCoordinatesUseCase to return both in a single call.
 */
data class LocationData(
    val fullAddress: String,
    val city: String
)
