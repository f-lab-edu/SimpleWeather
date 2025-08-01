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
        composable("main") { WeatherListScreen(navController = navController) }
        composable("detail/{cityName}") { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName")
            if (cityName != null) {
                WeatherDetailScreen(
                    cityName = cityName,
                    navController = navController
                )
            } else {
                // 예외 처리 화면
            }
        }
        composable("search") {
            CitySearchScreen(
                onBackClick = { navController.popBackStack() },
                onAddCity = { city ->
                    // 도시 추가 로직 작업 예정 (예: ViewModel에 저장 후 popBackStack)
                    navController.popBackStack()  // 임시로 뒤로가기 처리만
                }
            )
        }
    }
}
