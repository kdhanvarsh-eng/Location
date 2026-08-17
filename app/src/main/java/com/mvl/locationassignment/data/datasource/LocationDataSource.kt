package com.mvl.locationassignment.data.datasource

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LocationDataSource"

interface LocationDataSource {
    suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String
    suspend fun getCityFromCoordinates(latitude: Double, longitude: Double): String
}

class LocationDataSourceImpl @Inject constructor(
    private val context: Context
) : LocationDataSource {

    private suspend fun getDataFromGeoCoder(latitude: Double, longitude: Double): Address? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    Log.d(TAG, "Geocoding lookup successful for ($latitude, $longitude)")
                    Log.d(TAG, "Full address : ${addresses}")
                    Log.d(TAG, "Full address 1: ${addresses[0]}")
                    addresses[0]
                } else {
                    Log.d(TAG, "No addresses found for coordinates ($latitude, $longitude)")
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Geocoder error: ${e.message}", e)
                null
            }
        }
    }
    
    override suspend fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        val addr = getDataFromGeoCoder(latitude, longitude)
        Log.d(TAG, "Full address :2 ${addr?.toString()}")
        return if (addr != null) {
            val fullAddress =   addr.getAddressLine(0)

            Log.d(TAG, "Full Address sending: $fullAddress")

            fullAddress

        } else {
            ""
        }
    }

    override suspend fun getCityFromCoordinates(latitude: Double, longitude: Double): String {
        val addr = getDataFromGeoCoder(latitude, longitude)
        
        return if (addr != null) {
            val city = addr.locality ?: addr.adminArea ?: ""
            val local = addr.locality
            val admin = addr.adminArea
            Log.d(TAG, "Extracted city: '$city' from coordinates ($latitude, $longitude)")
            Log.d(TAG, "Local city: '$local' from coordinates ($latitude, $longitude)")
            Log.d(TAG, "Local admin area: '$admin' from coordinates ($latitude, $longitude)")
            city
        } else {
            Log.d(TAG, "City extraction failed - returning empty string")
            ""
        }
    }
}
