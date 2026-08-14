package com.mvl.locationassignment.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mvl.locationassignment.presentation.viewmodel.LocationViewModel
import com.mvl.locationassignment.ui.screen.BookingConfirmationScreen
import com.mvl.locationassignment.ui.screen.DetailsScreen
import com.mvl.locationassignment.ui.screen.MapScreen

@Composable
fun NavigationHost(navController: NavHostController) {
    // Create ViewModel at the NavigationHost level so it's shared across all screens
    val viewModel: LocationViewModel = hiltViewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.MapScreen.route
    ) {
        composable(Screen.MapScreen.route) {
            MapScreen(
                viewModel = viewModel,
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
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.BookingConfirmationScreen.route) {
            BookingConfirmationScreen(
                viewModel = viewModel,
                onContinue = {
                    viewModel.resetBooking()
                    navController.navigate(Screen.MapScreen.route) {
                        popUpTo(Screen.BookingConfirmationScreen.route) { inclusive = true }
                    }
                },
                navController = navController
            )
        }
    }
}
