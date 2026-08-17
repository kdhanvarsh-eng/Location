package com.mvl.location.presentation.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle as ComposeTextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvl.locationassignment.R
import com.mvl.locationassignment.presentation.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    locationIndex: Int,
    viewModel: LocationViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var nicknameInput by remember { mutableStateOf("") }

    val selectedLocation = if (locationIndex == 0) uiState.locationA else uiState.locationB
    val selectedAqi = if (locationIndex == 0) uiState.aqiA else uiState.aqiB
    val label = if (locationIndex == 0) "A" else "B"

    Log.d("DetailsScreen", "locationIndex=$locationIndex, selectedLocation=$selectedLocation, selectedAqi=$selectedAqi")

    // Initialize nickname input when location changes
    LaunchedEffect(selectedLocation) {
        nicknameInput = selectedLocation?.nickname ?: ""
    }

    Scaffold(
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
                    .padding(start = 10.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedLocation != null) {
                    // Location Label and Address
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.width(30.dp),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.text_primary)
                        )
                        Text(
                            text = selectedLocation.address,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.text_primary),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // AQI Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "aqi",
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_gray),
                            modifier = Modifier.weight(0.3f)
                        )
                        Text(
                            text = (selectedAqi ?: 0).toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.text_primary),
                            modifier = Modifier.weight(0.7f)
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_location_data),
                        color = colorResource(R.color.error_red),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Bottom Section - Nickname Input Field and Button
            if (selectedLocation != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = { newValue ->
                            if (newValue.length <= 20) {
                                nicknameInput = newValue
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        placeholder = {
                            Text(
                                "Enter nickname",
                                color = colorResource(R.color.text_gray)
                            )
                        },
                        textStyle = ComposeTextStyle(
                            fontSize = 14.sp,
                            color = colorResource(R.color.text_primary)
                        ),
                        singleLine = true
                    )

                    // Save Button
                    Button(
                        onClick = {
                            if (selectedLocation != null) {
                                viewModel.updateLocationNickname(locationIndex, nicknameInput)
                            }
                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .navigationBarsPadding(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.primary_yellow),
                            contentColor = colorResource(R.color.text_primary)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.continue_v),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

