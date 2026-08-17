package com.mvl.locationassignment.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mvl.locationassignment.domain.usecase.GetTripsUseCase
import com.mvl.locationassignment.presentation.state.TripHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "TripHistoryViewModel"


@HiltViewModel
class TripHistoryViewModel @Inject constructor(
    private val getTripsUseCase: GetTripsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TripHistoryUiState())
    val uiState: StateFlow<TripHistoryUiState> = _uiState
    
    fun fetchTrips(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                Log.d(TAG, "Fetching trips for $year-$month")
                val trips = withContext(Dispatchers.IO) {
                    getTripsUseCase(year, month)
                }
                
                val totalPrice = trips.sumOf { it.price }
                Log.d(TAG, "Got ${trips.size} trips, Total Price: $totalPrice")
                
                _uiState.value = _uiState.value.copy(
                    trips = trips,
                    totalPrice = totalPrice,
                    isLoading = false
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching trips: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }
}
