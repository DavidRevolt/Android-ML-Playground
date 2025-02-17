package com.davidrevolt.playground.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.davidrevolt.feature.home.HOME_ROUTE
import com.davidrevolt.feature.home.homeScreen
import com.davidrevolt.playground.feature.camerax.CAMERAX_ROUTE
import com.davidrevolt.playground.feature.camerax.cameraxScreen
import com.davidrevolt.playground.ui.AppState


@Composable
fun AppNavigation(appState: AppState) {
    val navController = appState.navController
    val startDestination = CAMERAX_ROUTE

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        homeScreen()
        cameraxScreen()
    }
}