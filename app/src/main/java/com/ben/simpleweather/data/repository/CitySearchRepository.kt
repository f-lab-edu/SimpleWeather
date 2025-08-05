package com.ben.simpleweather.data.repository

import com.ben.simpleweather.data.City

interface CitySearchRepository {
    suspend fun searchCities(query: String): List<City>
}