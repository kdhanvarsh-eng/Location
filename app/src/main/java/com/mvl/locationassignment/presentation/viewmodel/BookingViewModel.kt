package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.data.model.BookingRequest
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.domain.usecase.BookTripUseCase
import com.mvl.locationassignment.presentation.state.BookingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "BookingViewModel"


@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookTripUseCase: BookTripUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun bookTrip(request: BookingRequest) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val response = withContext(Dispatchers.IO) {
                    bookTripUseCase(request)
                }

                _uiState.value = _uiState.value.copy(bookingResponse = response, isLoading = false)

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
        _uiState.value = BookingUiState()
    }
}
