package com.mvl.locationassignment.data.datasource

import android.util.Log
import com.mvl.locationassignment.BuildConfig
import com.mvl.locationassignment.data.api.WaqiApiService
import com.mvl.locationassignment.data.model.AqiInfo
import javax.inject.Inject

private const val TAG = "AqiDataSource"

interface AqiDataSource {
    suspend fun getAqiByCityName(cityName: String): AqiInfo
}

class AqiDataSourceImpl @Inject constructor(
    private val waqiApiService: WaqiApiService
) : AqiDataSource {
    
    override suspend fun getAqiByCityName(cityName: String): AqiInfo {
        return try {
            Log.d(TAG, "Fetching AQI for city: $cityName")
            
            val response = waqiApiService.getAqiByCity(
                city = cityName,
                token = BuildConfig.AQI_API_KEY
            )

            if (response.status == "ok" && response.data != null) {
                val aqi = response.data.aqi
                val retrievedCityName = response.data.city?.name ?: cityName
                Log.d(TAG, "AQI fetched: $aqi for city: $retrievedCityName")
                
                AqiInfo(aqi = aqi, cityName = retrievedCityName, isError = false)
            } else {
                Log.d(TAG, "API returned error status or null data")
                AqiInfo(
                    aqi = 0,
                    cityName = cityName,
                    isError = true
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching AQI", e)
            AqiInfo(
                aqi = 0,
                cityName = cityName,
                isError = true
            )
        }
    }
}
