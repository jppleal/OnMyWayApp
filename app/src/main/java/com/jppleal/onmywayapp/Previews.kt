package com.jppleal.onmywayapp
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

/*@Composable
fun PreviewContent(navController: NavController) {
    OnMyWayAppTheme {
        NavGraph(navController = navController, modifier = Modifier.fillMaxSize())
    }
}*/
@Preview
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}

/*@Preview
@Composable
fun AlertListPreview() {
    val alerts = getSomeGoodHardcodedAlerts()
    AlertList(alerts = alerts)
}*/

@Preview
@Composable
fun OptionScreenPreview() {
    val navController = rememberNavController()
    OptionScreen(navController)

}

/*@Preview
@Composable
fun EstimatedTimeOfArrivalPreview() {
    EstimatedTimeOfArrival(onDismiss = { true }, onAcceptance = {selectedAlertId = null})
}*/

@Preview
@Composable
fun LogOutDialogPreview() {
    LogOutDialog(onDismiss = { false }, onConfirmation = { true }, onCancel = { true })
}
@Preview
@Composable
fun CredentialsFormPreview(){
    val  navController = rememberNavController()
    CredentialsForm(navController)
}
