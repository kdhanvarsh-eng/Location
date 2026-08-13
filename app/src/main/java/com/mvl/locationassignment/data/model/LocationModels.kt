package com.mvl.locationassignment.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class LocationInfo(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("address")
    val address: String,
    @SerializedName("nickname")
    val nickname: String? = null
) {
    fun toLatLng() = LatLng(latitude, longitude)
    
    fun getDisplayName(): String = nickname.takeIf { !it.isNullOrBlank() } ?: address
}

data class AirQualityInfo(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("aqi")
    val aqi: Int,
    @SerializedName("level")
    val level: String
)

data class LocationData(
    val locationInfo: LocationInfo,
    val airQualityInfo: AirQualityInfo
)
