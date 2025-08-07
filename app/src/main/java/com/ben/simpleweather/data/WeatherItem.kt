package com.ben.simpleweather.data

data class WeatherItem(
    val city: City,
    val cityName: String,
    val temperature: Int,
    val weatherType: String,
    val iconCode: String,
)