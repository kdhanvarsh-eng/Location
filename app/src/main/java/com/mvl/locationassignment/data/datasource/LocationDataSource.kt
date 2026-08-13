package com.mvl.locationassignment.data.datasource

import android.content.Context
import android.location.Geocoder
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LocationDataSource"

data class LocationAddress(val fullAddress: String, val city: String)

interface LocationDataSource {
    suspend fun getLocationFromCoordinates(latitude: Double, longitude: Double): LocationAddress
}

class LocationDataSourceImpl @Inject constructor(
    private val context: Context
) : LocationDataSource {

    override suspend fun getLocationFromCoordinates(
        latitude: Double,
        longitude: Double
    ): LocationAddress {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)

                val address = addresses?.firstOrNull()
                if (address != null) {
                    // Full address
                    val fullAddress = address.getAddressLine(0) ?: listOfNotNull(address.thoroughfare,
                                address.locality, address.adminArea, address.countryName).joinToString(", ")

                    // City
                    val city =
                        address.locality
                            ?: address.subAdminArea
                            ?: address.adminArea
                            ?: ""

                    Log.d(TAG, "Full Address: $fullAddress")
                    Log.d(TAG, "City: $city")
                    LocationAddress(fullAddress = fullAddress, city = city)
                } else {
                    Log.d(TAG, "No address found for ($latitude, $longitude)")
                    LocationAddress(fullAddress = "Lat: $latitude, Long: $longitude", city = "")
                }

            } catch (e: Exception) {

                Log.e(TAG, "Geocoder error", e)

                LocationAddress(
                    fullAddress = "Lat: $latitude, Long: $longitude",
                    city = ""
                )
            }
        }
    }
}
