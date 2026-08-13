package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.domain.usecase.GetAddressFromCoordinatesUseCase
import com.mvl.locationassignment.presentation.state.ButtonState
import com.mvl.locationassignment.presentation.state.LocationUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocationViewModel"

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getAddressFromCoordinatesUseCase: GetAddressFromCoordinatesUseCase
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
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching location info", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error fetching location info",
                    isLoading = false
                )
            }
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
