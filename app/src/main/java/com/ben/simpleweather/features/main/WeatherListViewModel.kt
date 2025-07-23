package com.ben.simpleweather.features.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class WeatherItem(
    val cityName: String,
    val temperature: Int,
    val weatherType: String
)

@HiltViewModel
class WeatherListViewModel @Inject constructor() : ViewModel() {

    // 더미 데이터 (최대 10개 도시)
    private val _weatherList = MutableStateFlow(
        listOf(
            WeatherItem("Seoul", 25, "Sunny"),
            WeatherItem("Busan", 20, "Cloudy"),
            WeatherItem("Incheon", 18, "Rainy"),
            WeatherItem("Daegu", -5, "Snowy"),
            WeatherItem("Gwangju", 15, "Windy")
        )
    )
    val weatherList: StateFlow<List<WeatherItem>> = _weatherList.asStateFlow()

    fun deleteSelected(selectedCities: List<String>) {
        val updatedList = _weatherList.value.toMutableList()
        updatedList.removeAll { it.cityName in selectedCities }
        _weatherList.value = updatedList
    }

    fun moveItem(fromIndex: Int, toIndex: Int) {
        val currentList = _weatherList.value.toMutableList()
        if (fromIndex == toIndex) return
        if (fromIndex !in currentList.indices || toIndex !in currentList.indices) return

        val item = currentList.removeAt(fromIndex)
        currentList.add(toIndex, item)
        _weatherList.value = currentList
    }

}