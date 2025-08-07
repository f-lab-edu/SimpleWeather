package com.ben.simpleweather.data.repository

import com.ben.simpleweather.data.CachedWeather
import com.ben.simpleweather.data.remote.dto.ForecastResponse
import com.ben.simpleweather.data.remote.dto.WeatherResponse
import com.ben.simpleweather.network.WeatherApi
import jakarta.inject.Inject
import java.util.Locale

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    private val weatherCache = mutableMapOf<Int, CachedWeather>() // cityId → CachedWeather
    private val cacheDurationMillis = 30 * 60 * 1000L // 30분

    override suspend fun getWeather(cityId: Int, latitude: Double, longitude: Double): WeatherResponse {
        val lang = getLang()

        val now = System.currentTimeMillis()
        val cached = weatherCache[cityId]

        return if (cached != null && now - cached.timestamp < cacheDurationMillis) {
            cached.weather
        } else {
            val weather = api.getCurrentWeather(latitude, longitude, lang = lang)
            weatherCache[cityId] = CachedWeather(weather, now)
            weather
        }
    }

    override suspend fun getForecast(lat: Double, lon: Double): ForecastResponse {
        val lang = getLang()
        return api.getWeatherForecast(lat, lon, lang = lang)
    }

    private fun getLang(): String = if (Locale.getDefault().language == "ko") "kr" else "en"
}