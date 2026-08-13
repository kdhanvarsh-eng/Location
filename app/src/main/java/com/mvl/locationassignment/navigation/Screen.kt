package com.mvl.locationassignment.navigation

sealed class Screen(val route: String) {
    object MapScreen : Screen("map_screen")
    object DetailsScreen : Screen("details_screen")
}
