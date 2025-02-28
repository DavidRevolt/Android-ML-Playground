package com.davidrevolt.playground.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.davidrevolt.feature.home.HOME_ROUTE
import com.davidrevolt.feature.home.homeScreen
import com.davidrevolt.playground.feature.camerax.cameraxScreen
import com.davidrevolt.playground.feature.camerax.navigateToCameraX
import com.davidrevolt.playground.ui.AppState


@Composable
fun AppNavHost(appState: AppState) {
    val navController = appState.navController
    val startDestination = HOME_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen(onCameraPermissionGranted = navController::navigateToCameraX)
        cameraxScreen()
    }
}