package com.ben.simpleweather.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.simpleweather.data.ForecastItem
import com.ben.simpleweather.data.WeatherDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor() : ViewModel() {

    private val _weather = MutableStateFlow<WeatherDetail?>(null)
    val weather: StateFlow<WeatherDetail?> = _weather.asStateFlow()

    private val _forecast = MutableStateFlow<List<ForecastItem>>(emptyList())
    val forecast: StateFlow<List<ForecastItem>> = _forecast.asStateFlow()

    fun loadWeather() {
        viewModelScope.launch {
            _weather.value = WeatherDetail(
                temperature = 27,
                feelsLike = 25,
                description = "Sunny",
                iconCode = "01d",
                humidity = 60,
                windSpeed = 10,
                precipitationChance = 0.1f,
                visibility = 9800,
                cloudiness = 78,
                windDegree = 135,
                pressure = 1013,
                sunrise = 1726636384,
                sunset = 1726680975,
                rainAmount = 1.2,
                snowAmount = null
            )

            _forecast.value = listOf(
                ForecastItem(time = "12:00", temperature = 27, iconCode = "01d"),
                ForecastItem(time = "15:00", temperature = 28, iconCode = "02d"),
                ForecastItem(time = "18:00", temperature = 26, iconCode = "03d"),
                ForecastItem(time = "21:00", temperature = 24, iconCode = "01n")
            )
        }
    }
}