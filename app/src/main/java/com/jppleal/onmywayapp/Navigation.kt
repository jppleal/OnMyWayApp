package com.jppleal.onmywayapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jppleal.onmywayapp.ui.theme.OnMyWayAppTheme
import io.github.jan.supabase.auth.user.UserSession

sealed class Screen(val route: String){
    data object LogInScreen : Screen("login")
    data object HomeScreen : Screen("home")
    data object OptionScreen : Screen("option")
    data object CredentialsForm : Screen("credentialsLogin")
    data object NewUserFormScreen : Screen("newUserForm")
}

@Composable
fun OnMyWayApp(navController: NavHostController, isLoggedIn: UserSession?) {
    OnMyWayAppTheme {
        NavHost(navController = navController, startDestination = if(isLoggedIn != null)Screen.HomeScreen.route else Screen.LogInScreen.route) {
            composable(Screen.LogInScreen.route) {
                LoginScreen(navController)
            }
            composable(Screen.HomeScreen.route) {
                HomeScreen(navController)
            }
            composable(Screen.OptionScreen.route) {
                OptionScreen(navController)
            }
            composable(Screen.CredentialsForm.route){
                CredentialsForm(navController)
            }
            composable(Screen.NewUserFormScreen.route){
                NewUserFormScreen(navController)
            }
        }
    }
}

