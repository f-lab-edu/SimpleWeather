package com.ben.simpleweather

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ben.simpleweather.features.detail.WeatherDetailScreen
import com.ben.simpleweather.features.main.WeatherListScreen
import com.ben.simpleweather.features.search.CitySearchScreen

@Composable
fun SimpleWeatherNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { WeatherListScreen(navController = navController) }
        composable(
            route = "detail/{cityid}",
            arguments = listOf(navArgument("cityid") { type = NavType.IntType })
        ) { backStackEntry ->
            val cityid = backStackEntry.arguments?.getInt("cityid")
            if (cityid != null) {
                WeatherDetailScreen(
                    cityid = cityid,
                    navController = navController
                )
            } else {
                // 예외 처리 화면
            }
        }
        composable("search") {
            CitySearchScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
