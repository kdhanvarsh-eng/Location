package com.mvl.locationassignment.data.datasource

import android.content.Context
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface LocationDataSource {
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String
}

class LocationDataSourceImpl @Inject constructor(
    private val context: Context
) : LocationDataSource {
    
    override suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    listOfNotNull(
                        addr.thoroughfare,
                        addr.locality,
                        addr.adminArea,
                        addr.countryName
                    ).joinToString(", ")
                } else {
                    "Lat: $latitude, Long: $longitude"
                }
            } catch (e: Exception) {
                Log.e("LocationDataSource", "Geocoder error", e)
                "Lat: $latitude, Long: $longitude"
            }


        }
    }
}
