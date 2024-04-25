package com.jppleal.onmywayapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

sealed class Screen(val route: String){
    object LogInScreen : Screen("login")
    object HomeScreen : Screen("home")
    object OptionScreen : Screen("option")
}

@Composable
fun NavGraph(navController: NavController, modifier: Modifier) {
    // Define navigation graph using NavHost and composable functions
    NavHost(navController = navController, startDestination = Screen.LogInScreen.route) {
        composable(Screen.LogInScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.HomeScreen.route) {
            // Pass any necessary parameters to HomeScreen
            HomeScreen("","", getSomeGoodHardcodedAlerts(), navController = navController)
        }
        composable(Screen.OptionScreen.route) {
            OptionScreen(navController = navController)
        }

    }
}

@Composable
fun NavHost(
    navController: NavController,
    startDestination: String,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {

}