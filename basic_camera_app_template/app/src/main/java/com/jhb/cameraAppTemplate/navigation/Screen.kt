package com.jhb.cameraAppTemplate.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jhb.cameraAppTemplate.R

sealed class Screen(val route: String,
                    @StringRes val pageTitle: Int,
                    @DrawableRes val icon : Int
) {
    object home : Screen( "home", R.string.home, R.drawable.baseline_home_24)
    object info : Screen( "info", R.string.info, R.drawable.baseline_info_24)
}