package com.mvl.locationassignment.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.stringResource
import com.mvl.locationassignment.R
import com.mvl.locationassignment.data.model.Trip
import com.mvl.locationassignment.presentation.viewmodel.TripHistoryViewModel
import java.util.Calendar

private const val TAG = "TripDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(
    viewModel: TripHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Fetch trips on screen load (current year and month)
    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        viewModel.fetchTrips(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.trip_details)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(R.color.primary_green),
                    titleContentColor = colorResource(R.color.white)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorResource(R.color.primary_green)
                )
            } else if (uiState.error != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.error_label, uiState.error ?: ""),
                        color = colorResource(R.color.error_red),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary_green),
                            contentColor = colorResource(R.color.white)
                        )
                    ) {
                        Text(stringResource(R.string.go_back))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Summary Section
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp,
                        color = colorResource(R.color.dark_gray)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.total_count, uiState.trips.size),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.white)
                            )
                            Text(
                                text = stringResource(R.string.total_price, uiState.totalPrice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.primary_green)
                            )
                        }
                    }
                    
                    // Trips List
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.trips) { trip ->
                            TripItem(trip)
                        }
                    }
                    
                    // Back Button
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary_green),
                            contentColor = colorResource(R.color.white)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.go_back),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TripItem(trip: Trip) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 2.dp,
        color = colorResource(R.color.dark_gray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.trip_location_a, trip.a.name),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.white),
                        maxLines = 1
                    )
                    Text(
                        text = stringResource(R.string.aqi_value, trip.a.aqi),
                        fontSize = 11.sp,
                        color = colorResource(R.color.gray)
                    )
                }
                Text(
                    text = stringResource(R.string.trip_price, trip.price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.primary_green)
                )
            }
            
            Column {
                Text(
                    text = stringResource(R.string.trip_location_b, trip.b.name),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.white),
                    maxLines = 1
                )
                Text(
                    text = stringResource(R.string.aqi_value, trip.b.aqi),
                    fontSize = 11.sp,
                    color = colorResource(R.color.gray)
                )
            }
        }
    }
}
