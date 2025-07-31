package com.ben.simpleweather.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.simpleweather.data.ForecastItem
import com.ben.simpleweather.data.WeatherDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    fun loadWeather() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                // 더미 데이터
                val weather = WeatherDetail(
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

                val forecast = listOf(
                    ForecastItem(time = "12:00", temperature = 27, iconCode = "01d"),
                    ForecastItem(time = "15:00", temperature = 28, iconCode = "02d"),
                    ForecastItem(time = "18:00", temperature = 26, iconCode = "03d"),
                    ForecastItem(time = "21:00", temperature = 24, iconCode = "01n")
                )

                _uiState.value = WeatherUiState.Success(weather, forecast)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("날씨 데이터를 불러오는 데 실패했습니다.")
            }
        }
    }
}
