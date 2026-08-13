package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.model.AqiInfo
import javax.inject.Inject

class GetAqiFromCoordinatesUseCase @Inject constructor(
    private val getCityFromCoordinatesUseCase: GetCityFromCoordinatesUseCase,
    private val getAqiByCityNameUseCase: GetAqiApiUseCase
) {
    suspend operator fun invoke(latitude: Double, longitude: Double): AqiInfo {
        // Step 1: Get city from coordinates
        val locationAddress = getCityFromCoordinatesUseCase(latitude, longitude)
        val cityName = locationAddress.city

        // Step 2: Get AQI for that city
        return if (cityName.isNotEmpty()) {
            getAqiByCityNameUseCase(cityName)
        } else {
            // If city extraction failed, return error
            AqiInfo(
                aqi = 0,
                cityName = "Unknown",
                isError = true
            )
        }
    }
}
