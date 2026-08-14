package com.mvl.locationassignment.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class LocationInfo(

    val latitude: Double,
    val longitude: Double,
    val address: String,
    val nickname: String? = null
) {
    fun getDisplayName(): String = nickname.takeIf { !it.isNullOrBlank() } ?: address
}
