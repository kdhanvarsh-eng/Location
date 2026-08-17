package com.mvl.locationassignment.presentation.state

import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.data.model.Trip

data class BookingUiState(
    val bookingResponse: BookingResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)