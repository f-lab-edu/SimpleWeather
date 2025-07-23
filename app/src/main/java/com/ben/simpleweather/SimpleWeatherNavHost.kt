package com.ben.simpleweather

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ben.simpleweather.features.detail.WeatherDetailScreen
import com.ben.simpleweather.features.main.WeatherListScreen
import com.ben.simpleweather.features.search.CitySearchScreen

@Composable
fun SimpleWeatherNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { WeatherListScreen() }
        composable("detail/{cityName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName")
            WeatherDetailScreen()
        }
        composable("search") { CitySearchScreen() }
    }
}
