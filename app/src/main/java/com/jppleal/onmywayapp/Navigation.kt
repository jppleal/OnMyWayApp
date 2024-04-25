package com.jppleal.onmywayapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme

sealed class Screen(val route: String){
    data object LogInScreen : Screen("login")
    data object HomeScreen : Screen("home")
    data object OptionScreen : Screen("option")

    data object CredentialsForm : Screen("credentialsLogin")
}

@Composable
fun OnMyWayApp(navController: NavHostController) {
    OnMyWayAppTheme {
        NavHost(navController = navController, startDestination = Screen.LogInScreen.route) {
            composable(Screen.LogInScreen.route) {
                LoginScreen(navController)
            }
            composable(Screen.HomeScreen.route) {
                HomeScreen("","", listOf(), navController)
            }
            composable(Screen.OptionScreen.route) {
                OptionScreen(navController)
            }
            composable(Screen.CredentialsForm.route){
                CredentialsForm(navController)
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    OnMyWayApp(navController)
}


