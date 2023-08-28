package com.jhb.cameraML.ui.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jhb.cameraML.ui.theme.AppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jhb.cameraML.R
import com.jhb.cameraML.navigation.Screen
import com.jhb.cameraML.navigation.TemplateAppNavHost

@Composable
fun Main(){
    val mainScreenViewModel: MainScreenViewModel = viewModel()
    val uiState by mainScreenViewModel.uiState.collectAsState()


    MainScreen(
        uiState = uiState,
        darkModeToggle = { mainScreenViewModel.toggleDarkMode() }
    )

}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(uiState: MainScreenUiState, darkModeToggle: () -> Unit){
    val navController = rememberNavController()

    AppTheme(darkTheme = uiState.darkMode){
        Scaffold(
            topBar = {
                TopBar(
                    title = uiState.pageTitle ?: "Default Page Title",
                    darkModeToggle = darkModeToggle,
                    navController = navController
                )
            }
        ) { innerPadding ->
            TemplateAppNavHost(navController, modifier = Modifier.padding(innerPadding))
//            Text(text = "hello world", modifier = Modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title : String,
    navController: NavHostController,
    darkModeToggle: () -> Unit,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title =
        {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        navigationIcon = {HomeButton(navController)},
        actions =
        {
            DarkModeButton(onClick = darkModeToggle)
            InfoButton(navController)
        }

    , modifier = modifier)
}

@Composable
fun DarkModeButton(onClick: ()-> Unit ){
    IconButton(
        onClick = onClick,
        content = {
            Icon(
                painterResource(id = R.drawable.baseline_dark_mode_24),
                contentDescription = stringResource(id = R.string.contentDesc_darkModeToggele)
            )
        }
    )
}
@Composable
fun InfoButton(navController: NavHostController){
    IconButton(
        onClick = { navController.navigate(Screen.info.route) },
        content = {
            Icon(
                painterResource(id = Screen.info.icon),
                contentDescription = stringResource(id = Screen.info.pageTitle)
            )
        }
    )
}

@Composable
fun HomeButton(navController: NavHostController){
    IconButton(
        onClick = { navController.navigate(Screen.home.route){
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true

        }
    },
        content = {
            Icon(
                painterResource(id = Screen.home.icon),
                contentDescription = stringResource(id = R.string.home)
            )
        }
    )
}

@Preview
@Composable
fun Preview() {
    val uiState = MainScreenUiState(pageTitle = "test", darkMode = false)
    MainScreen(
        uiState = uiState,
        darkModeToggle = {}
    )
}
