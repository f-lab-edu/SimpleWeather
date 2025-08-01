package com.ben.simpleweather.data

data class ForecastItem(
    val time: String, // "12PM"
    val temperature: Int,
    val iconCode: String,
)