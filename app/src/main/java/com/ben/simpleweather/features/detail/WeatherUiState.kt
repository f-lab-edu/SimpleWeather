package com.ben.simpleweather.features.detail

import com.ben.simpleweather.data.ForecastItem
import com.ben.simpleweather.data.WeatherDetail

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(
        val weather: WeatherDetail,
        val forecast: List<ForecastItem>
    ) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}
