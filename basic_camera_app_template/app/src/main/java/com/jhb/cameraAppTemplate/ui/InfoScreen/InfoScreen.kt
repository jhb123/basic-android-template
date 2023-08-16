package com.jhb.cameraAppTemplate.ui.InfoScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun InfoScreen(navigation: ()->Unit){
    InfoScreenComposable(navigation = navigation)
}

@Composable
fun InfoScreenComposable(navigation: ()->Unit ){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(onClick = navigation ) {
            Text(text = "Press me!")
        }
    }
}