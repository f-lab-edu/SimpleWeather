package com.ben.simpleweather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val cod: String? = null,
    val message: Int? = null,
    val cnt: Int? = null,
    val list: List<ForecastItemDto>? = null,
    val city: CityDto? = null
)

@Serializable
data class ForecastItemDto(
    val dt: Long? = null,
    val main: MainDto? = null, // Reusing MainDto from WeatherResponse.kt
    val weather: List<WeatherDto>? = null, // Reusing WeatherDto
    val clouds: CloudsDto? = null, // Reusing CloudsDto
    val wind: WindDto? = null, // Reusing WindDto
    val visibility: Int? = null,
    val pop: Double? = null, // Probability of precipitation
    val rain: RainDto? = null, // Reusing RainDto
    val snow: SnowDto? = null, // Reusing SnowDto
    val sys: ForecastSysDto? = null, // Different SysDto for forecast
    @SerialName("dt_txt")
    val dtTxt: String? = null
)

@Serializable
data class ForecastSysDto( // Specific SysDto for forecast items
    val pod: String? = null // Part of day (d = day, n = night)
)

@Serializable
data class CityDto(
    val id: Int? = null,
    val name: String? = null,
    val coord: CoordDto? = null, // Reusing CoordDto
    val country: String? = null,
    val population: Int? = null,
    val timezone: Int? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null
)

