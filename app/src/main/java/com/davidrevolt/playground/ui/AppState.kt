package com.davidrevolt.playground.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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


    /**
     * Clearing the whole backstack and then navigating to destination according to Auth state.
     * Not logged in -> navigate to Login screen.
     * Logged in -> navigate to Home screen.
     */
    fun onAuthStateChangeNavigation() {
        val navOptions = navOptions {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true

        }
    }

}