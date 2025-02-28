package com.davidrevolt.playground.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope

// Created for future app builds
@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): AppState {
    return remember(
        navController,
        coroutineScope
    ) {
        AppState(
            navController,
            coroutineScope
        )
    }
}

class AppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope
) {

}