package com.ben.simpleweather.data

import com.ben.simpleweather.data.remote.dto.WeatherResponse

data class CachedWeather(
    val weather: WeatherResponse,
    val timestamp: Long
)