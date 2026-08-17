package com.mvl.locationassignment.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvl.locationassignment.R
import com.mvl.locationassignment.data.model.Trip
import com.mvl.locationassignment.presentation.viewmodel.TripHistoryViewModel
import java.util.Calendar

private const val TAG = "TripDetailsScreen"

@Composable
fun TripDetailsScreen(
    viewModel: TripHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        viewModel.fetchTrips(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // Header with back arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable {
                        onNavigateBack()
                    },
                tint = Color.Black
            )
        }

        // Content
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorResource(R.color.primary_green)
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error ?: "Something went wrong",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        fontSize = 14.sp,
                        color = colorResource(R.color.error_red)
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TripSummarySection(
                            totalCount = uiState.trips.size,
                            totalPrice = uiState.totalPrice
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(color = Color(0xFFF4F4F4))
                        )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp)
                            .weight(1f)
                    ) {

                        itemsIndexed(
                            items = uiState.trips
                        ) {
                            index, trip ->
                            val locationALabel = getLocationLabel(index * 2)
                            val locationBLabel = getLocationLabel(index * 2 + 1)

                            TripItem(
                                trip = trip,
                                locationALabel = locationALabel,
                                locationBLabel = locationBLabel
                            )

                                if (index < uiState.trips.lastIndex) {
                                    Spacer(
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(Color(0xFFF4F4F4))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TripSummarySection(totalCount: Int, totalPrice: Int) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem(
                title = "Total Count",
                value = totalCount.toString(),
                modifier = Modifier.weight(1f)
            )

            SummaryItem(
                title = "Total Price",
                value = formatPrice(totalPrice),
                modifier = Modifier.weight(1f)
            )
        }
}

@Composable
private fun SummaryItem(title: String, value: String, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF737373)
            )

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
}

@Composable
fun TripItem(trip: Trip, locationALabel: String, locationBLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(start = 28.dp, end = 28.dp, top = 2.dp, bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LocationRow(
            label = locationALabel,
            locationName = trip.a.name
        )

        LocationRow(
            label = locationBLabel,
            locationName = trip.b.name
        )
    }
}

@Composable
private fun LocationRow(label: String, locationName: String) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    modifier = Modifier.width(30.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = locationName,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
}


private fun getLocationLabel(index: Int): String {
    return ('A'.code + index).toChar().toString()
}

private fun formatPrice(price: Int): String {
    return if (price % 1.0 == 0.0) {
        price.toLong().toString()
    } else {
        String.format("%.2f", price)
    }
}