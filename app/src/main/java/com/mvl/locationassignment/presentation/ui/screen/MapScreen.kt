package com.mvl.locationassignment.presentation.ui.screen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.mvl.locationassignment.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.mvl.locationassignment.presentation.state.ButtonState
import com.mvl.locationassignment.presentation.viewmodel.LocationViewModel
import com.mvl.locationassignment.presentation.viewmodel.BookingViewModel
import com.mvl.locationassignment.data.model.BookingRequestBuilder
import com.mvl.locationassignment.utils.PermissionUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

private const val TAG = "MapScreen"
@Composable
fun MapScreen(
    viewModel: LocationViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    onNavigateToDetails: (Int) -> Unit,
    onNavigateToBooking: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val bookingUiState by bookingViewModel.uiState.collectAsState()
     
    var hasLocationPermission by remember { mutableStateOf(PermissionUtils.hasLocationPermission(context)) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var centerMarkerPosition by remember { mutableStateOf<LatLng?>(null) }
    var isCameraMoving by remember { mutableStateOf(false) }
    
    // Navigate to booking confirmation when booking response is received
    LaunchedEffect(bookingUiState.bookingResponse) {
        if (bookingUiState.bookingResponse != null) {
            Log.d(TAG, "Booking response received, navigating to confirmation screen")
            onNavigateToBooking()
        }
    }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(centerMarkerPosition ?: LatLng(0.0, 0.0), 12f)
    }
    
    val markerState = rememberMarkerState(position = centerMarkerPosition ?: LatLng(0.0, 0.0))
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.all { it }
        if (hasLocationPermission) {
            getCurrentLocation(fusedLocationClient) { location ->
               if (location != null) {
                    currentLocation = location
                    centerMarkerPosition = location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 12f)
                    markerState.position = location
                    viewModel.updateCurrentLocationInfo(location.latitude, location.longitude)
                }
            }
        }
    }
     
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(PermissionUtils.getLocationPermissions())
        } else {
            getCurrentLocation(fusedLocationClient) { location ->
                if (location != null) {
                    currentLocation = location
                    centerMarkerPosition = location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 12f)
                    markerState.position = location
                    viewModel.updateCurrentLocationInfo(location.latitude, location.longitude)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            cameraPositionState.isMoving
        }
            .collectLatest { isMoving ->
                isCameraMoving = isMoving
                if (!isMoving) {
                    val position = cameraPositionState.position.target
                    centerMarkerPosition = position
                    markerState.position = position
                    viewModel.updateCurrentLocationInfo(position.latitude, position.longitude)
                }
            }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Map area - takes available space
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(mapType = MapType.NORMAL),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                )
                
                // Pin Icon in center
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pin),
                        contentDescription = "Location Pin",
                        modifier = Modifier
                            .size(48.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // AQI Display in top right corner
                if (!isCameraMoving) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFDDDDDD),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        val currentAqi = when (uiState.buttonState) {
                            ButtonState.SET_A -> uiState.aqiA
                            ButtonState.SET_B -> uiState.aqiB ?: uiState.aqiA
                            else -> uiState.aqiB ?: uiState.aqiA
                        }
                        
                        Text(
                            text = if (uiState.aqi_error != null) {
                                "AQI - NA"
                            } else if (currentAqi != null) {
                                "AQI - $currentAqi"
                            } else {
                                "AQI - --"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                uiState.aqi_error != null -> Color.Red
                                currentAqi == null -> Color.Gray
                                currentAqi < 50 -> Color.Green
                                currentAqi < 100 -> Color(0xFFFFB74D)
                                currentAqi < 150 -> Color(0xFFFFA726)
                                currentAqi < 200 -> Color(0xFFEF5350)
                                else -> Color(0xFF8B0000)
                            }
                        )
                    }
                }
            }
            
            // Bottom Control Panel - fixed height
            if (!isCameraMoving) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side: A and B labels stacked vertically
                        Column(
                            modifier = Modifier
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LocationLabel(
                                label = "A",
                                address = uiState.locationA?.getDisplayName(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (uiState.locationA != null) {
                                            viewModel.selectLocation(0)
                                            onNavigateToDetails(0)
                                        }
                                    }
                            )
                            LocationLabel(
                                label = "B",
                                address = uiState.locationB?.getDisplayName(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (uiState.locationB != null) {
                                            viewModel.selectLocation(1)
                                            onNavigateToDetails(1)
                                        }
                                    }
                            )
                        }
                        
                        // Right side: Large V Button
                        Button(
                            onClick = {
                                Log.d(TAG, "Button clicked - buttonState=${uiState.buttonState}")
                                when (uiState.buttonState) {
                                    ButtonState.BOOK -> {
                                        Log.d(TAG, "Booking trip...")
                                        val locationA = uiState.locationA
                                        val locationB = uiState.locationB
                                        if (locationA != null && locationB != null) {
                                            val requestBuilder = BookingRequestBuilder(
                                                locationA = locationA,
                                                locationB = locationB,
                                                aqiA = uiState.aqiA ?: 0,
                                                aqiB = uiState.aqiB ?: 0
                                            )
                                            val bookingRequest = requestBuilder.build()
                                            bookingViewModel.bookTrip(bookingRequest, requestBuilder)
                                        } else {
                                            Log.e(TAG, "Cannot book: Location A or B is null")
                                        }
                                    }
                                    else -> {
                                        uiState.currentLocationInfo?.let { locationInfo ->
                                            Log.d(TAG, "Calling onVButtonClicked with $locationInfo")
                                            viewModel.onVButtonClicked(locationInfo)
                                        } ?: run {
                                            Log.d(TAG, "currentLocationInfo is null, cannot save")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(80.dp)
                                .fillMaxHeight(),
                            enabled = !uiState.isLoading && !bookingUiState.isLoading && centerMarkerPosition != null && uiState.currentLocationInfo != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFC107), // Golden/Yellow color
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFFCCCCCC)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            if (uiState.isLoading || (uiState.buttonState == ButtonState.BOOK && bookingUiState.isLoading)) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = when (uiState.buttonState) {
                                        ButtonState.SET_A -> stringResource(R.string.set_a)
                                        ButtonState.SET_B -> stringResource(R.string.set_b)
                                        ButtonState.BOOK -> stringResource(R.string.book)
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                        
                        if (uiState.error != null) {
                            // Show error if needed
                            Log.e(TAG, "Error occurred: ${uiState.error}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationLabel(
    label: String,
    address: String?,
    modifier: Modifier = Modifier
) {

    val hasAddress = !address.isNullOrBlank() && address != "-"

    Surface(
        modifier = modifier.border(
            width = 1.dp,
            color = Color.LightGray,
            shape = RoundedCornerShape(4.dp)
        ),
        shape = RoundedCornerShape(4.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Text(
                text = if (hasAddress) {
                    address!!
                } else {
                    "Enter $label"
                },
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 12.sp,
                color = if (hasAddress) {
                    Color.DarkGray
                } else {
                    Color.LightGray
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    callback: (LatLng?) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(LatLng(location.latitude, location.longitude))
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    } catch (e: SecurityException) {
        callback(null)
    }
}
