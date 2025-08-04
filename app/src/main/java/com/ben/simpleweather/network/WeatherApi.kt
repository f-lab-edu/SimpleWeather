package com.ben.simpleweather.network

import com.ben.simpleweather.data.remote.dto.ForecastResponse
import com.ben.simpleweather.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric", // metric for Celsius, standard for Kelvin, imperial for Fahrenheit
        @Query("lang") lang: String = "kr" // Korean language
    ): WeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr",
        @Query("cnt") count: Int? = null // Optional: A number of timestamps, which will be returned in the API response.
    ): ForecastResponse
}
