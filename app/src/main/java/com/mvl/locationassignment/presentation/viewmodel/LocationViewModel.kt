package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingRequestBuilder
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.domain.usecase.BookTripUseCase
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
    private val getAqiByCityNameUseCase: GetAqiByCityNameUseCase,
    private val bookTripUseCase: BookTripUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private val _bookingResponse = MutableStateFlow<BookingResponse?>(null)
    val bookingResponse: StateFlow<BookingResponse?> = _bookingResponse.asStateFlow()

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
                    aqi = null,
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
                aqi = null,
                aqi_error = "NA",
                isAqiLoading = false
            )
        } else {
            Log.d(TAG, "AQI fetched: ${aqiInfo.aqi} for city: ${aqiInfo.cityName}")
            _uiState.value = _uiState.value.copy(
                currentCity = extractedCityName,
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

    fun bookTrip() {
        viewModelScope.launch {
            try {
                val locationA = _uiState.value.locationA
                val locationB = _uiState.value.locationB

                if (locationA == null || locationB == null) {
                    Log.e(TAG, "Cannot book: Location A or B is null")
                    _uiState.value = _uiState.value.copy(error = "Both locations must be set to book")
                    return@launch
                }

                _uiState.value = _uiState.value.copy(isLoading = true)

                // Use BookingRequestBuilder to create the booking request
                val bookingRequest = BookingRequestBuilder(
                    locationA = locationA,
                    locationB = locationB,
                    aqiA = _uiState.value.aqi ?: 0,
                    aqiB = _uiState.value.aqi ?: 0
                ).build()

                Log.d(TAG, "Booking request: A=${bookingRequest.a.name}, B=${bookingRequest.b.name}")

                val response = withContext(Dispatchers.IO) {
                    bookTripUseCase(bookingRequest)
                }

                Log.d(TAG, "Booking confirmed - Price: ${response.price}")
                _bookingResponse.value = response
                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                Log.e(TAG, "Booking failed: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = "Booking failed: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun resetBooking() {
        _bookingResponse.value = null
        _uiState.value = LocationUiState()
    }
}
