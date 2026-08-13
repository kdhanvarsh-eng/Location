package com.mvl.locationassignment.data.repository

import com.mvl.locationassignment.data.datasource.AqiDataSource
import com.mvl.locationassignment.data.model.AqiInfo
import com.mvl.locationassignment.domain.repository.AqiRepository
import javax.inject.Inject

class AqiRepositoryImpl @Inject constructor(
    private val aqiDataSource: AqiDataSource
) : AqiRepository {
    
    override suspend fun getAqiByCityName(cityName: String): AqiInfo {
        return aqiDataSource.getAqiByCityName(cityName)
    }
}
