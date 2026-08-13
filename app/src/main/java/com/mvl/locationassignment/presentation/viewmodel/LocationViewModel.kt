package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.domain.usecase.GetAddressFromCoordinatesUseCase
import com.mvl.locationassignment.domain.usecase.GetAqiByCityNameUseCase
import com.mvl.locationassignment.domain.usecase.GetCityFromCoordinatesUseCase
import com.mvl.locationassignment.presentation.state.ButtonState
import com.mvl.locationassignment.presentation.state.LocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "LocationViewModel"

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase,
    private val getCityFromCoordinatesUseCase: GetCityFromCoordinatesUseCase,
    private val getAqiByCityNameUseCase: GetAqiByCityNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun updateCurrentLocationInfo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.d(TAG, "Fetching address for: $latitude, $longitude")
                
                val address = getAddressFromCoordinatesUseCase(latitude, longitude)
                
                val locationInfo = LocationInfo(latitude, longitude, address)
                Log.d(TAG, "Address fetched: $address")
                
                _uiState.value = _uiState.value.copy(
                    currentLocationInfo = locationInfo,
                    currentLatitude = latitude,
                    currentLongitude = longitude,
                    isLoading = false
                )
                
                // Extract city and fetch AQI if city changed
                fetchAqiIfCityChanged(latitude, longitude)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching location info", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error fetching location info",
                    isLoading = false
                )
            }
        }
    }

    private fun fetchAqiIfCityChanged(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                val previousCity = _uiState.value.currentCity
                Log.d(TAG, "fetchAqiIfCityChanged START:")
                Log.d(TAG, "  Coordinates: lat=$latitude, lng=$longitude")
                Log.d(TAG, "  Previous city from state: '$previousCity'")
                
                val newCity = withContext(Dispatchers.IO) {
                    getCityFromCoordinatesUseCase(latitude, longitude)
                }
                
                val normalizedNewCity = newCity.trim()
                val normalizedPreviousCity = previousCity?.trim()
                
                Log.d(TAG, "  Extracted new city: '$newCity'")
                Log.d(TAG, "  Normalized new city: '$normalizedNewCity'")
                Log.d(TAG, "  Normalized previous city: '$normalizedPreviousCity'")
                
                // Check if city changed (case-insensitive, trimmed comparison)
                val cityChanged = normalizedNewCity.isNotEmpty() && 
                                 normalizedNewCity.lowercase() != normalizedPreviousCity?.lowercase()
                
                Log.d(TAG, "  City changed? $cityChanged")
                
                if (cityChanged) {
                    Log.d(TAG, "✅ CITY CHANGED from '$normalizedPreviousCity' to '$normalizedNewCity' - CALLING API")
                    
                    val aqiInfo = withContext(Dispatchers.IO) {
                        getAqiByCityNameUseCase(normalizedNewCity)
                    }
                    
                    Log.d(TAG, "  API Response: AQI=${aqiInfo.aqi}, isError=${aqiInfo.isError}, cityFromAPI='${aqiInfo.cityName}'")
                    
                    // Store the EXTRACTED city name (not API's city name) so comparison works correctly
                    updateAqiState(aqiInfo, normalizedNewCity)
                } else {
                    Log.d(TAG, "⏭️ CITY UNCHANGED - SKIPPING API CALL")
                    Log.d(TAG, "  Reason: New city matches previous (both: '$normalizedNewCity')")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching AQI from coordinates", e)
                _uiState.value = _uiState.value.copy(
                    aqi = null,
                    aqi_error = "NA",
                    isAqiLoading = false
                )
            }
        }
    }

    private fun updateAqiState(aqiInfo: com.mvl.locationassignment.data.model.AqiInfo, extractedCityName: String) {
        if (aqiInfo.isError) {
            Log.d(TAG, "AQI fetch returned error for city: ${aqiInfo.cityName}")
            _uiState.value = _uiState.value.copy(
                currentCity = extractedCityName,  // ← Use extracted city, not API response
                aqi = null,
                aqi_error = "NA",
                isAqiLoading = false
            )
        } else {
            Log.d(TAG, "✅ AQI fetched: ${aqiInfo.aqi} for city: ${aqiInfo.cityName}")
            _uiState.value = _uiState.value.copy(
                currentCity = extractedCityName,  // ← Use extracted city, not API response
                aqi = aqiInfo.aqi,
                aqi_error = null,
                isAqiLoading = false
            )
        }
    }

    fun onVButtonClicked(locationInfo: LocationInfo) {
        Log.d(TAG, "Setting location: ${locationInfo.address}, buttonState=${uiState.value.buttonState}")
        when (uiState.value.buttonState) {
            ButtonState.SET_A -> {
                _uiState.value = _uiState.value.copy(
                    locationA = locationInfo,
                    buttonState = ButtonState.SET_B,
                    isLoading = false
                )
                Log.d(TAG, "Location A saved: ${locationInfo.address}")
            }
            ButtonState.SET_B -> {
                _uiState.value = _uiState.value.copy(
                    locationB = locationInfo,
                    buttonState = ButtonState.BOOK,
                    isLoading = false
                )
                Log.d(TAG, "Location B saved: ${locationInfo.address}")
            }
            ButtonState.BOOK -> {
                Log.d(TAG, "Booking completed")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun selectLocation(index: Int) {
        _uiState.value = _uiState.value.copy(selectedLocationIndex = index)
    }
    
    fun updateLocationNickname(index: Int, nickname: String) {
        val trimmedNickname = nickname.trim().take(20)
        Log.d(TAG, "Updating nickname for location $index: $trimmedNickname")
        when (index) {
            0 -> {
                _uiState.value = _uiState.value.copy(
                    locationA = _uiState.value.locationA?.copy(nickname = trimmedNickname.takeIf { it.isNotEmpty() })
                )
            }
            1 -> {
                _uiState.value = _uiState.value.copy(
                    locationB = _uiState.value.locationB?.copy(nickname = trimmedNickname.takeIf { it.isNotEmpty() })
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
