package com.mvl.locationassignment.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mvl.locationassignment.presentation.viewmodel.LocationViewModel
import com.mvl.locationassignment.presentation.viewmodel.BookingViewModel
import com.mvl.locationassignment.presentation.ui.screen.BookingConfirmationScreen
import com.mvl.locationassignment.presentation.ui.screen.DetailsScreen
import com.mvl.locationassignment.presentation.ui.screen.MapScreen
import com.mvl.locationassignment.presentation.ui.screen.TripDetailsScreen

@Composable
fun NavigationHost(navController: NavHostController) {
    // Create ViewModels at the NavigationHost level so they're shared across all screens
    val locationViewModel: LocationViewModel = hiltViewModel()
    val bookingViewModel: BookingViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.MapScreen.route
    ) {
        composable(Screen.MapScreen.route) {
            MapScreen(
                viewModel = locationViewModel,
                bookingViewModel = bookingViewModel,
                onNavigateToDetails = { locationIndex ->
                    navController.navigate("${Screen.DetailsScreen.route}/$locationIndex")
                },
                onNavigateToBooking = {
                    navController.navigate(Screen.BookingConfirmationScreen.route)
                }
            )
        }
        composable("${Screen.DetailsScreen.route}/{locationIndex}") { backStackEntry ->
            val locationIndex = backStackEntry.arguments?.getString("locationIndex")?.toIntOrNull() ?: 0
            DetailsScreen(
                locationIndex = locationIndex,
                viewModel = locationViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.BookingConfirmationScreen.route) {
            BookingConfirmationScreen(
                viewModel = bookingViewModel,
                onContinue = {
                    // Navigate to Trip Details instead of back to map
                    navController.navigate(Screen.TripDetailsScreen.route)
                },
                navController = navController
            )
        }
        composable(Screen.TripDetailsScreen.route) {
            TripDetailsScreen(
                onNavigateBack = {
                    bookingViewModel.resetBooking()
                    locationViewModel.resetLocationState()
                    navController.navigate(Screen.MapScreen.route) {
                        popUpTo(Screen.TripDetailsScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
