package com.davidrevolt.playground.feature.camerax

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val CAMERAX_ROUTE = "camerax_route"

fun NavController.navigateToCameraX(navOptions: NavOptions? = null) {
    this.navigate(CAMERAX_ROUTE, navOptions)
}

fun NavGraphBuilder.cameraxScreen() {
    composable(route = CAMERAX_ROUTE) {
        CameraXScreen()
    }
}