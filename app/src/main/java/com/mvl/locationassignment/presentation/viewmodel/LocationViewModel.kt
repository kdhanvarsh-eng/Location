package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.domain.usecase.GetLocationDataFromCoordinatesUseCase
import com.mvl.locationassignment.domain.usecase.GetAqiByCityNameUseCase
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
    private val getLocationDataFromCoordinatesUseCase: GetLocationDataFromCoordinatesUseCase,
    private val getAqiByCityNameUseCase: GetAqiByCityNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    fun updateCurrentLocationInfo(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                Log.d(TAG, "Fetching address for: $latitude, $longitude")
                
                // Single call returns both address and city
                val locationData = withContext(Dispatchers.IO) {
                    getLocationDataFromCoordinatesUseCase(latitude, longitude)
                }
                
                val locationInfo = LocationInfo(latitude, longitude, locationData.fullAddress)
                Log.d(TAG, "Address fetched: ${locationData.fullAddress}")
                Log.d(TAG, "City fetched: ${locationData.city}")

                _uiState.value = _uiState.value.copy(
                    currentLocationInfo = locationInfo,
                    currentLatitude = latitude,
                    currentLongitude = longitude,
                    isLoading = false
                )

                fetchAqiIfCityChanged(locationData.city)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching location info", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error fetching location info",
                    isLoading = false
                )
            }
        }
    }

    private fun fetchAqiIfCityChanged(newCity: String) {
        viewModelScope.launch {
            try {
                val previousCity = _uiState.value.currentCity
                Log.d(TAG, "fetchAqiIfCityChanged: Previous city='$previousCity', New city='$newCity'")
                
                // Normalize for comparison
                val normalizedNewCity = newCity.trim()
                val normalizedPreviousCity = previousCity?.trim()
                
                // Check if city changed (case-insensitive)
                val cityChanged = normalizedNewCity.isNotEmpty() && 
                                 normalizedNewCity.lowercase() != normalizedPreviousCity?.lowercase()
                
                if (cityChanged) {
                    Log.d(TAG, "City changed from '$normalizedPreviousCity' to '$normalizedNewCity' - Fetching AQI")

                    val aqiInfo = withContext(Dispatchers.IO) {
                        getAqiByCityNameUseCase(normalizedNewCity)
                    }
                    
                    updateAqiState(aqiInfo, normalizedNewCity)
                } else {
                    Log.d(TAG, "City unchanged - Skipping AQI API call")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching AQI", e)
                _uiState.value = _uiState.value.copy(
                    aqi_error = "NA",
                    isAqiLoading = false
                )
            }
        }
    }

    private fun updateAqiState(aqiInfo: com.mvl.locationassignment.data.model.AqiInfo, extractedCityName: String) {
        if (aqiInfo.isError) {
            Log.d(TAG, "AQI fetch returned error")
            _uiState.value = _uiState.value.copy(
                currentCity = extractedCityName,
                aqi_error = "NA",
                isAqiLoading = false
            )
        } else {
            Log.d(TAG, "AQI fetched: ${aqiInfo.aqi} for city: ${aqiInfo.cityName}")
            // Store AQI based on current button state
            val updatedState = when (_uiState.value.buttonState) {
                ButtonState.SET_A -> _uiState.value.copy(
                    currentCity = extractedCityName,
                    aqiA = aqiInfo.aqi,
                    aqi_error = null,
                    isAqiLoading = false
                )
                ButtonState.SET_B -> _uiState.value.copy(
                    currentCity = extractedCityName,
                    aqiB = aqiInfo.aqi,
                    aqi_error = null,
                    isAqiLoading = false
                )
                else -> _uiState.value.copy(
                    currentCity = extractedCityName,
                    aqi_error = null,
                    isAqiLoading = false
                )
            }
            _uiState.value = updatedState
        }
    }

    fun onVButtonClicked(locationInfo: LocationInfo) {
        Log.d(TAG, "Setting location: ${locationInfo.address}, buttonState=${uiState.value.buttonState}")
        when (uiState.value.buttonState) {
            ButtonState.SET_A -> {
               // Save the current AQI as aqiA (it was just fetched for this location)
               _uiState.value = _uiState.value.copy(
                   locationA = locationInfo,
                   buttonState = ButtonState.SET_B,
                   isLoading = false,
                   aqiA = _uiState.value.aqiA ?: 0  // Preserve the AQI that was fetched
               )
               Log.d(TAG, "Location A saved: ${locationInfo.address} with AQI=${_uiState.value.aqiA}")
           }
           ButtonState.SET_B -> {
               // Check if location B is same as A
               val isSameLocation = locationInfo.latitude == _uiState.value.locationA?.latitude &&
                                    locationInfo.longitude == _uiState.value.locationA?.longitude
                
               _uiState.value = _uiState.value.copy(locationB = locationInfo, buttonState = ButtonState.BOOK,
                   isLoading = false
               )
                
               // If same location, copy A's AQI to B now (since API won't be called again for same city)
               if (isSameLocation) {
                   Log.d(TAG, "Location B is same as A - copying AQI=${_uiState.value.aqiA} from A to B")
                   _uiState.value = _uiState.value.copy(aqiB = _uiState.value.aqiA)
               }
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

    fun resetLocationState() {
        _uiState.value = LocationUiState()
    }
}
