package com.mvl.locationassignment.presentation.state

import com.mvl.locationassignment.data.model.LocationInfo

enum class ButtonState {
    SET_A, SET_B, BOOK
}

data class LocationUiState(
    val currentLocationInfo: LocationInfo? = null,
    val locationA: LocationInfo? = null,
    val locationB: LocationInfo? = null,
    val buttonState: ButtonState = ButtonState.SET_A,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedLocationIndex: Int? = null
)
