package com.jhb.cameraAppTemplate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jhb.cameraAppTemplate.ui.InfoScreen.InfoScreen
import com.jhb.cameraAppTemplate.ui.cameraScreen.CameraScreen
import com.jhb.cameraAppTemplate.ui.main.MainScreenViewModel

@Composable
fun TemplateAppNavHost(navController: NavHostController, modifier : Modifier){
    val mainScreenViewModel: MainScreenViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.home.route,
        modifier = modifier
    ) {
        composable(route = Screen.home.route) {
            mainScreenViewModel.setTitle(stringResource(id = Screen.home.pageTitle))
            CameraScreen()
        }
        composable(route = Screen.info.route) {
            mainScreenViewModel.setTitle(stringResource(id = Screen.info.pageTitle))
            InfoScreen(navigation = {navController.navigate(Screen.home.route) })
        }
    }
}
