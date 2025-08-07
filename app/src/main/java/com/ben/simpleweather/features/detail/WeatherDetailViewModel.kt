package com.ben.simpleweather.features.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.simpleweather.data.ForecastItem
import com.ben.simpleweather.data.WeatherDetail
import com.ben.simpleweather.data.remote.dto.ForecastResponse
import com.ben.simpleweather.data.remote.dto.WeatherResponse
import com.ben.simpleweather.data.repository.CityStorageRepository
import com.ben.simpleweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityStorageRepository: CityStorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _cityName = MutableStateFlow<String?>(null)
    val cityName: StateFlow<String?> = _cityName

    fun loadWeather(cityId: Int) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {
                val city = cityStorageRepository.getCityById(cityId) ?: throw IllegalStateException("도시 정보 없음")
                val isKorean = Locale.getDefault().language == "ko"
                _cityName.value = when {
                    isKorean -> city.nameKo
                    else -> city.name
                }
                val weather = weatherRepository.getWeather(city.id, city.coord.lat, city.coord.lon)
                val forecast = weatherRepository.getForecast(city.coord.lat, city.coord.lon)

                val precipitationChance = (forecast.list?.firstOrNull()?.pop?.toFloat() ?: 0f) * 100f

                val weatherDetail = weather.toWeatherDetail(precipitationChance)
                val forecastList = forecast.toForecastList()

                _uiState.value = WeatherUiState.Success(weatherDetail, forecastList)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error("날씨 데이터를 불러오는 데 실패했습니다.")
            }
        }
    }

    fun ForecastResponse.toForecastList(): List<ForecastItem> {
        return this.list?.take(8)?.mapNotNull { item ->
            val temp = item.main?.temp?.toInt() ?: return@mapNotNull null
            val icon = item.weather?.firstOrNull()?.icon.orEmpty()
            val time = item.dt?.let {
                val local = Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalTime()
                local.format(DateTimeFormatter.ofPattern("HH:mm"))
            } ?: "??:??"

            ForecastItem(
                time = time,
                temperature = temp,
                iconCode = icon,
            )
        }.orEmpty()
    }

    fun WeatherResponse.toWeatherDetail(precipitationChance: Float): WeatherDetail {
        return WeatherDetail(
            temperature = main?.temp?.toInt() ?: 0,
            feelsLike = main?.feelsLike?.toInt() ?: 0,
            description = weather?.firstOrNull()?.description.orEmpty(),
            iconCode = weather?.firstOrNull()?.icon.orEmpty(),
            humidity = main?.humidity ?: 0,
            windSpeed = wind?.speed?.toInt() ?: 0,
            precipitationChance = precipitationChance,
            visibility = visibility ?: 0,
            cloudiness = clouds?.all ?: 0,
            windDegree = wind?.deg ?: 0,
            pressure = main?.pressure ?: 0,
            sunrise = sys?.sunrise ?: 0,
            sunset = sys?.sunset ?: 0,
            rainAmount = rain?.oneHour ?: 0.0,
            snowAmount = snow?.oneHour ?: 0.0,
        )
    }

}
