package com.ben.simpleweather.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.simpleweather.data.City
import com.ben.simpleweather.data.WeatherItem
import com.ben.simpleweather.data.repository.CityStorageRepository
import com.ben.simpleweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val cityStorageRepository: CityStorageRepository,
    private val weatherRepository: WeatherRepository // Retrofit 통해 API 호출
) : ViewModel() {

    private val _weatherList = MutableStateFlow<List<WeatherItem>>(emptyList())
    val weatherList: StateFlow<List<WeatherItem>> = _weatherList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadWeatherForSavedCities()
    }

    fun loadWeatherForSavedCities() {
        viewModelScope.launch {
            _isLoading.value = true

            val savedCities = cityStorageRepository.getSavedCities()

            val weatherItems = savedCities.mapNotNull { city ->
                try {
                    val weather = weatherRepository.getWeather(
                        cityId = city.id,
                        latitude = city.coord.lat,
                        longitude = city.coord.lon,
                    )
                    WeatherItem(
                        city = city,
                        cityName = city.displayName(),
                        temperature = weather.main?.temp?.toInt() ?: 0,
                        weatherType = weather.weather?.firstOrNull()?.description.orEmpty(),
                        iconCode = weather.weather?.firstOrNull()?.icon.orEmpty()
                    )
                } catch (_: Exception) {
                    null
                }
            }

            _weatherList.value = weatherItems
            _isLoading.value = false
        }
    }

    fun deleteSelected(selectedCities: List<City>) {
        viewModelScope.launch {
            cityStorageRepository.removeCities(selectedCities)

            val updatedList = _weatherList.value.toMutableList()
            val namesToDelete = selectedCities.map { it.displayName() }

            updatedList.removeAll { it.cityName in namesToDelete }
            _weatherList.value = updatedList
        }
    }


    fun moveItem(fromIndex: Int, toIndex: Int) {
        val currentList = _weatherList.value.toMutableList()
        if (fromIndex == toIndex) return
        if (fromIndex !in currentList.indices || toIndex !in currentList.indices) return

        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)
        _weatherList.value = currentList
    }

    fun City.displayName(): String {
        return if (Locale.getDefault().language == "ko") {
            nameKo
        } else {
            name
        }
    }

}