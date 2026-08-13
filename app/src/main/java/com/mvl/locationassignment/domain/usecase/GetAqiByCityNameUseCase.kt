package com.mvl.locationassignment.domain.usecase

import com.mvl.locationassignment.data.model.AqiInfo
import com.mvl.locationassignment.domain.repository.AqiRepository
import javax.inject.Inject

class GetAqiByCityNameUseCase @Inject constructor(
    private val aqiRepository: AqiRepository
) {
    suspend operator fun invoke(cityName: String): AqiInfo {
        return aqiRepository.getAqiByCityName(cityName)
    }
}
