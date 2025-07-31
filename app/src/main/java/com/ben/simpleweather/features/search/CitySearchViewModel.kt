package com.ben.simpleweather.features.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.ben.simpleweather.data.City
import com.ben.simpleweather.data.Coord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CitySearchViewModel @Inject constructor() : ViewModel() {

    private val _cityList = MutableStateFlow<List<City>>(emptyList())
    val cityList: StateFlow<List<City>> = _cityList

    init {
        _cityList.value = listOf(
            City(1, "Seoul", "서울", "", "KR", Coord(126.978, 37.5665)),
            City(2, "Busan", "부산", "", "KR", Coord(129.0756, 35.1796)),
            City(3, "Incheon", "인천", "", "KR", Coord(126.7052, 37.4563)),
            City(4, "Daegu", "대구", "", "KR", Coord(128.6014, 35.8714)),
            City(5, "Gwangju", "광주", "", "KR", Coord(126.8514, 35.1595))
        )
    }
}
