package com.ben.simpleweather.features.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ben.simpleweather.R
import com.ben.simpleweather.data.City
import com.ben.simpleweather.data.repository.CitySearchRepository
import com.ben.simpleweather.data.repository.CityStorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor(
    private val citySearchRepository: CitySearchRepository,
    private val cityStorageRepository: CityStorageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<List<City>>(emptyList())
    val searchResult: StateFlow<List<City>> = _searchResult.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        // cities.json 강제 로드 (lazy 초기화 유도)
        viewModelScope.launch {
            citySearchRepository.searchCities("")
        }
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            val result = citySearchRepository.searchCities(query)
            _searchResult.value = result
        }
    }

    fun addCity(city: City) {
        viewModelScope.launch {
            val savedCities = cityStorageRepository.getSavedCities()

            when {
                savedCities.any { it.id == city.id } -> {
                    _message.value = context.getString(R.string.city_add_duplicate)
                }

                savedCities.size >= 10 -> {
                    _message.value = context.getString(R.string.city_add_limit)
                }

                else -> {
                    cityStorageRepository.addCity(city)
                    _message.value = context.getString(R.string.city_add_success)
                }
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
