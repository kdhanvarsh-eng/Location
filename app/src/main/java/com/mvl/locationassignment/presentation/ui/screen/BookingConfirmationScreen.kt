package com.mvl.locationassignment.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mvl.locationassignment.R
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.presentation.viewmodel.BookingViewModel

private const val TAG = "BookingConfirmation"

@Composable
fun BookingConfirmationScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    navController: NavHostController? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.bookingResponse != null) {
        BookingConfirmationContent(
            response = uiState.bookingResponse!!,
            isLoading = uiState.isLoading,
            onContinue = onContinue,
            onBackClick = {
                Log.d(TAG, "Back button clicked - resetting state")
                viewModel.resetBooking()
                navController?.popBackStack()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingConfirmationContent(
    response: BookingResponse,
    isLoading: Boolean,
    onContinue: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.booking_confirmation),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(R.color.primary_green)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.white))
                .padding(paddingValues)
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Location A Section
                LocationDetailSection(
                    title = stringResource(R.string.location_a),
                    aqi = response.a.aqi,
                    name = response.a.name
                )

                // Location B Section
                LocationDetailSection(
                    title = stringResource(R.string.location_b),
                    aqi = response.b.aqi,
                    name = response.b.name
                )

                // Price Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = stringResource(R.string.price_label, response.price),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.text_dark_gray)
                    )
                }
            }

            // Continue Button - Fixed at bottom with system navigation bar padding
            Button(
                onClick = {
                    Log.d(TAG, "Continue clicked")
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                    .navigationBarsPadding(),  // Automatically handles Android navigation bar
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.primary_green),
                    contentColor = colorResource(R.color.white),
                    disabledContainerColor = colorResource(R.color.disabled_gray)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = colorResource(R.color.white),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.continue_button),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationDetailSection(
    title: String,
    aqi: Int,
    name: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(8.dp),
        color = colorResource(R.color.light_background),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.text_gray)
            )

            // AQI Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.aqi_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_gray)
                )
                Text(
                    text = "- $aqi",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.primary_green)
                )
            }

            // Name Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.name_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.text_gray)
                )
                Text(
                    text = "- $name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.text_dark_gray),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
