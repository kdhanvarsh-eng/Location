package com.mvl.locationassignment.presentation.state

import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.data.model.Trip

data class TripHistoryUiState(
    val trips: List<Trip> = emptyList(),
    val totalPrice: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
