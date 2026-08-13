package com.mvl.locationassignment.domain.repository

import com.mvl.locationassignment.data.model.AqiInfo

interface AqiRepository {
    suspend fun getAqiByCityName(cityName: String): AqiInfo
}
