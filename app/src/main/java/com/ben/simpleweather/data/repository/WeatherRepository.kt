package com.ben.simpleweather.data.repository

import com.ben.simpleweather.data.remote.dto.WeatherResponse

interface WeatherRepository {
    suspend fun getWeather(
        cityId: Int,
        latitude: Double,
        longitude: Double,
    ): WeatherResponse
}