package com.ben.simpleweather.data.repository

import com.ben.simpleweather.data.City

interface CityStorageRepository {
    suspend fun getSavedCities(): List<City>
    suspend fun addCity(city: City)
    suspend fun removeCities(cities: List<City>)
}