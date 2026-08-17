package com.mvl.locationassignment.presentation.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mvl.locationassignment.R
import com.mvl.locationassignment.data.model.BookingResponse
import com.mvl.locationassignment.data.model.LocationInfo
import com.mvl.locationassignment.presentation.viewmodel.BookingViewModel

private const val TAG = "BookingConfirmation"

@Composable
fun BookingConfirmationScreen(
    viewModel: BookingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    navController: NavHostController? = null
) {

    val uiState by viewModel.uiState.collectAsState()

    val bookingResponse = uiState.bookingResponse

    if (bookingResponse != null) {
        BookingConfirmationContent(
            response = bookingResponse,
            locationA = uiState.locationA,
            locationB = uiState.locationB,
            isLoading = uiState.isLoading,
            onContinue = onContinue
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingConfirmationContent(
    response: BookingResponse,
    locationA: LocationInfo?,
    locationB: LocationInfo?,
    isLoading: Boolean,
    onContinue: () -> Unit
) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                LocationDetailItem(
                    label = "A",
                    address = response.a.name,
                    aqi = response.a.aqi,
                    nickname = locationA?.nickname
                        ?.takeIf { it.isNotBlank() } ?: "NA"
                )

                LocationDetailItem(
                    label = "B",
                    address = response.b.name,
                    aqi = response.b.aqi,
                    nickname = locationB?.nickname
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "NA"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 16.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = stringResource(R.string.price),
                    fontSize = 14.sp,
                    color = colorResource(
                        R.color.text_gray
                    )
                )

                Text(
                    text = response.price.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(
                        R.color.text_primary
                    )
                )
            }

            Button(
                onClick = {
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp, end = 16.dp, bottom = 12.dp
                    )
                    .height(50.dp)
                    .navigationBarsPadding(),

                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        R.color.primary_yellow
                    ),
                    contentColor = colorResource(
                        R.color.text_primary
                    ),
                    disabledContainerColor = colorResource(
                        R.color.disabled_gray
                    )
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
                        text = stringResource(R.string.continue_v),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationDetailItem(label: String, address: String, aqi: Int, nickname: String) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = label,
                modifier = Modifier.width(50.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    R.color.text_primary
                )
            )

            Text(
                text = address,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.text_primary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "aqi",
                modifier = Modifier.width(180.dp),
                fontSize = 14.sp,
                color = colorResource(
                    R.color.text_gray
                )
            )

            Text(
                text = aqi.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    R.color.text_primary
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "nickname",
                modifier = Modifier.width(180.dp),
                fontSize = 14.sp,
                color = colorResource(
                    R.color.text_gray
                )
            )

            Text(
                text = nickname,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(
                    R.color.text_primary
                )
            )
        }
    }
}