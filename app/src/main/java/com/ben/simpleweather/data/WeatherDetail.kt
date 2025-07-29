package com.ben.simpleweather.data

data class WeatherDetail(
    val temperature: Int,
    val feelsLike: Int,
    val description: String,
    val iconCode: String,
    val humidity: Int,
    val windSpeed: Int,
    val precipitationChance: Float,
    val visibility: Int,            // meters
    val cloudiness: Int,            // %
    val windDegree: Int,            // 0 ~ 360
    val pressure: Int,              // hPa
    val sunrise: Long,              // Unix time
    val sunset: Long,               // Unix time
    val rainAmount: Double?,        // mm
    val snowAmount: Double?         // mm
)