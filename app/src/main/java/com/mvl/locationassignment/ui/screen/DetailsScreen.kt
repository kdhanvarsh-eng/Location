package com.mvl.locationassignment.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mvl.locationassignment.presentation.viewmodel.LocationViewModel

@Composable
fun DetailsScreen(
    locationIndex: Int,
    viewModel: LocationViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var nicknameInput by remember { mutableStateOf("") }
    
    val selectedLocation = if (locationIndex == 0) uiState.locationA else uiState.locationB
    val label = if (locationIndex == 0) "Location A" else "Location B"
    
    Log.d("DetailsScreen", "locationIndex=$locationIndex, selectedLocation=$selectedLocation, locationA=${uiState.locationA}, locationB=${uiState.locationB}")
    
    // Initialize nickname input when location changes
    LaunchedEffect(selectedLocation) {
        nicknameInput = selectedLocation?.nickname ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Location Details",
            fontSize = 24.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        if (selectedLocation != null) {
            LocationDetailCard(
                label = label,
                address = selectedLocation.address,
                latitude = selectedLocation.latitude,
                longitude = selectedLocation.longitude
            )
            
            // Nickname Input Field
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add Nickname (Optional, Max 20 characters)",
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = nicknameInput,
                    onValueChange = { newValue ->
                        if (newValue.length <= 20) {
                            nicknameInput = newValue
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter nickname") },
                    singleLine = true
                )
                Text(
                    text = "${nicknameInput.length}/20",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        } else {
            Text(
                text = "No location data available for index $locationIndex",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Button(
            onClick = {
                if (selectedLocation != null) {
                    viewModel.updateLocationNickname(locationIndex, nicknameInput)
                }
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Save & Back to Map")
        }
    }
}

@Composable
fun LocationDetailCard(
    label: String,
    address: String,
    latitude: Double,
    longitude: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = label, fontSize = 18.sp)
            Text(text = "Address: $address", fontSize = 14.sp)
            Text(text = "Latitude: $latitude", fontSize = 12.sp)
            Text(text = "Longitude: $longitude", fontSize = 12.sp)
        }
    }
}
