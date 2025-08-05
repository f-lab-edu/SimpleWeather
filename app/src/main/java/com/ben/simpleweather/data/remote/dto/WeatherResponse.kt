package com.ben.simpleweather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val coord: CoordDto? = null,
    val weather: List<WeatherDto>? = null,
    val base: String? = null,
    val main: MainDto? = null,
    val visibility: Int? = null,
    val wind: WindDto? = null,
    val clouds: CloudsDto? = null,
    val rain: RainDto? = null,
    val snow: SnowDto? = null,
    val dt: Long? = null,
    val sys: SysDto? = null,
    val timezone: Int? = null,
    val id: Int? = null,
    val name: String? = null,
    val cod: Int? = null,
)

@Serializable
data class CoordDto(
    val lon: Double? = null,
    val lat: Double? = null,
)

@Serializable
data class WeatherDto(
    val id: Int? = null,
    val main: String? = null,
    val description: String? = null,
    val icon: String? = null,
)

@Serializable
data class MainDto(
    val temp: Double? = null,
    @SerialName("feels_like")
    val feelsLike: Double? = null,
    @SerialName("temp_min")
    val tempMin: Double? = null,
    @SerialName("temp_max")
    val tempMax: Double? = null,
    val pressure: Int? = null,
    val humidity: Int? = null,
    @SerialName("sea_level")
    val seaLevel: Int? = null,
    @SerialName("grnd_level")
    val grndLevel: Int? = null,
    // Forecast specific field, adding here for reusability if MainDto is used for forecast items
    @SerialName("temp_kf")
    val tempKf: Double? = null,
)

@Serializable
data class WindDto(
    val speed: Double? = null,
    val deg: Int? = null,
    val gust: Double? = null,
)

@Serializable
data class CloudsDto(
    val all: Int? = null,
)

@Serializable
data class RainDto(
    @SerialName("1h")
    val oneHour: Double? = null,
    @SerialName("3h")
    val threeHours: Double? = null,
)

@Serializable
data class SnowDto(
    @SerialName("1h")
    val oneHour: Double? = null,
    @SerialName("3h")
    val threeHours: Double? = null,
)

@Serializable
data class SysDto( // For Current Weather
    val type: Int? = null,
    val id: Int? = null,
    val country: String? = null,
    val sunrise: Long? = null,
    val sunset: Long? = null,
)

